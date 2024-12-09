package com.glodblock.github.extendedae.common.inventory;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.IncludeExclude;
import appeng.api.ids.AEComponents;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.core.AEConfig;
import appeng.core.definitions.AEItems;
import appeng.items.storage.StorageCellTooltipComponent;
import appeng.util.ConfigInventory;
import appeng.util.prioritylist.IPartitionList;
import com.glodblock.github.extendedae.api.VoidMode;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.items.ItemVoidCell;
import com.glodblock.github.extendedae.xmod.ModConstants;
import com.glodblock.github.extendedae.xmod.mek.ChemBlacklist;
import com.glodblock.github.glodium.util.GlodUtil;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class VoidCellInventory implements StorageCell {

    private final ItemStack stack;
    @Nullable
    private final ISaveProvider container;
    private final IPartitionList partitionList;
    private final IncludeExclude partitionListMode;
    private final VoidMode voidMode;
    private final ItemVoidCell type;
    private Object2LongMap<AEKey> storedAmounts;
    private double voidEnergy;
    private boolean isPersisted = true;
    public static final ICellHandler HANDLER = new Handler();

    public VoidCellInventory(ItemStack stack, @Nullable ISaveProvider container) {
        if (!(stack.getItem() instanceof ItemVoidCell)) {
            throw new IllegalArgumentException("Cell isn't a void cell!");
        }
        this.stack = stack;
        this.container = container;
        this.type = (ItemVoidCell) stack.getItem();
        this.voidMode = stack.getOrDefault(EAESingletons.VOID_MODE, VoidMode.TRASH);
        this.voidEnergy = stack.getOrDefault(EAESingletons.VOID_ENERGY, 0.0);
        var builder = IPartitionList.builder();
        var upgrades = getUpgradesInventory();
        var config = getConfigInventory();
        boolean hasInverter = upgrades.isInstalled(AEItems.INVERTER_CARD);
        boolean isFuzzy = upgrades.isInstalled(AEItems.FUZZY_CARD);
        if (isFuzzy) {
            builder.fuzzyMode(getFuzzyMode());
        }
        builder.addAll(config.keySet());
        this.partitionListMode = (hasInverter ? IncludeExclude.BLACKLIST : IncludeExclude.WHITELIST);
        this.partitionList = builder.build();
    }

    public boolean isPartitioned() {
        return !this.partitionList.isEmpty();
    }

    private List<GenericStack> getStoredStacks() {
        return this.stack.getOrDefault(AEComponents.STORAGE_CELL_INV, List.of());
    }

    private ConfigInventory getConfigInventory() {
        return this.type.getConfigInventory(this.stack);
    }

    private IUpgradeInventory getUpgradesInventory() {
        return this.type.getUpgrades(this.stack);
    }

    private FuzzyMode getFuzzyMode() {
        return this.type.getFuzzyMode(this.stack);
    }

    @Override
    public CellState getStatus() {
        return CellState.NOT_EMPTY;
    }

    @Override
    public double getIdleDrain() {
        return 1;
    }

    @Override
    public void persist() {
        if (this.isPersisted) {
            return;
        }
        var stacks = new ArrayList<GenericStack>(this.storedAmounts.size());
        for (var entry : this.storedAmounts.object2LongEntrySet()) {
            long amount = entry.getLongValue();
            if (amount > 0) {
                stacks.add(new GenericStack(entry.getKey(), amount));
            }
        }
        if (stacks.isEmpty()) {
            this.stack.remove(AEComponents.STORAGE_CELL_INV);
        } else {
            this.stack.set(AEComponents.STORAGE_CELL_INV, stacks);
        }
        if (this.voidEnergy <= 0) {
            this.stack.remove(EAESingletons.VOID_ENERGY);
        } else {
            this.stack.set(EAESingletons.VOID_ENERGY, this.voidEnergy);
        }
        this.isPersisted = true;
    }

    private Object2LongMap<AEKey> getCellItems() {
        if (this.storedAmounts == null) {
            this.storedAmounts = new Object2LongOpenHashMap<>();
            this.loadCellItems();
        }
        return this.storedAmounts;
    }

    private void loadCellItems() {
        var stacks = getStoredStacks();
        for (var stack : stacks) {
            this.storedAmounts.put(stack.what(), stack.amount());
        }
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        for (var entry : this.getCellItems().object2LongEntrySet()) {
            out.add(entry.getKey(), entry.getLongValue());
        }
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (amount == 0 || what == null) {
            return 0;
        }
        if (this.partitionList.isEmpty()) {
            return 0;
        }
        if (!this.partitionList.matchesFilter(what, this.partitionListMode)) {
            return 0;
        }
        if (GlodUtil.checkMod(ModConstants.APPMEK)) {
            if (!ChemBlacklist.isValid(what)) {
                return 0;
            }
        }
        if (mode == Actionable.MODULATE) {
            this.voidEnergy += (double) amount / what.getAmountPerUnit();
            this.fillOutput();
            this.saveChanges();
        }
        return amount;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        var currentAmount = getCellItems().getLong(what);
        if (currentAmount > 0) {
            if (amount >= currentAmount) {
                if (mode == Actionable.MODULATE) {
                    getCellItems().remove(what, currentAmount);
                    this.saveChanges();
                }
                return currentAmount;
            } else {
                if (mode == Actionable.MODULATE) {
                    getCellItems().put(what, currentAmount - amount);
                    this.saveChanges();
                }
                return amount;
            }
        }
        return 0;
    }

    @Override
    public Component getDescription() {
        return this.stack.getHoverName();
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        if (this.partitionList.isEmpty()) {
            return false;
        }
        return this.partitionList.matchesFilter(what, this.partitionListMode);
    }

    protected void saveChanges() {
        this.isPersisted = false;
        if (this.container != null) {
            this.container.saveChanges();
        } else {
            this.persist();
        }
    }

    public void fillOutput() {
        this.getCellItems();
        if (this.voidMode == VoidMode.TRASH || this.voidMode.getPower() == 0) {
            this.voidEnergy = 0;
            return;
        }
        var output = AEItemKey.of(this.voidMode.output);
        long amt = (long) (this.voidEnergy / this.voidMode.getPower());
        if (output != null && amt > 0) {
            var cur = this.getCellItems().getLong(output);
            this.getCellItems().put(output, cur + amt);
        }
    }

    public Optional<TooltipComponent> getTooltipImage() {
        var upgradeStacks = new ArrayList<ItemStack>();
        if (AEConfig.instance().isTooltipShowCellUpgrades()) {
            for (var upgrade : this.getUpgradesInventory()) {
                upgradeStacks.add(upgrade);
            }
        }
        boolean hasMoreContent;
        List<GenericStack> content;
        if (AEConfig.instance().isTooltipShowCellContent()) {
            content = new ArrayList<>();
            var maxCountShown = AEConfig.instance().getTooltipMaxCellContentShown();
            var availableStacks = this.getAvailableStacks();
            for (var entry : availableStacks) {
                content.add(new GenericStack(entry.getKey(), entry.getLongValue()));
            }
            content.sort(Comparator.comparingLong(GenericStack::amount).reversed());
            hasMoreContent = content.size() > maxCountShown;
            if (content.size() > maxCountShown) {
                content.subList(maxCountShown, content.size()).clear();
            }
        } else {
            hasMoreContent = false;
            content = Collections.emptyList();
        }

        return Optional.of(new StorageCellTooltipComponent(
                upgradeStacks,
                content,
                hasMoreContent,
                true));
    }

    private static class Handler implements ICellHandler {

        @Override
        public boolean isCell(ItemStack is) {
            return is != null && is.getItem() instanceof ItemVoidCell;
        }

        @Override
        public @Nullable StorageCell getCellInventory(ItemStack is, @Nullable ISaveProvider host) {
            return isCell(is) ? new VoidCellInventory(is, host) : null;
        }
    }

}
