package com.glodblock.github.extendedae.container;

import appeng.api.inventories.InternalInventory;
import appeng.api.stacks.AEItemKey;
import appeng.client.Point;
import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.IOptionalSlot;
import appeng.menu.slot.OutputSlot;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.api.IPage;
import com.glodblock.github.extendedae.client.ExSemantics;
import com.glodblock.github.extendedae.common.tileentities.TileExMolecularAssembler;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ContainerExMolecularAssembler extends UpgradeableMenu<TileExMolecularAssembler> implements IProgressProvider, IPage, IActionHolder {

    public static final MenuType<ContainerExMolecularAssembler> TYPE = MenuTypeBuilder
            .create(ContainerExMolecularAssembler::new, TileExMolecularAssembler.class)
            .buildUnregistered(ExtendedAE.id("ex_molecular_assembler"));

    private static final int MAX_CRAFT_PROGRESS = 100;
    private final ActionMap actions = ActionMap.create();
    private static final SlotSemantic[] SLOT = new SlotSemantic[] {
            ExSemantics.EX_1,
            ExSemantics.EX_2,
            ExSemantics.EX_3,
            ExSemantics.EX_4,
            ExSemantics.EX_5,
            ExSemantics.EX_6,
            ExSemantics.EX_7,
            ExSemantics.EX_8
    };
    private final TileExMolecularAssembler molecularAssembler;
    private final List<AppEngSlot> outputs = new ArrayList<>();
    @GuiSync(4)
    public int craftProgress = 0;
    @GuiSync(7)
    public int page = 0;

    public ContainerExMolecularAssembler(int id, Inventory ip, TileExMolecularAssembler host) {
        super(TYPE, id, ip, host);
        this.actions.put("show", o -> showPage());
        this.molecularAssembler = host;
        for (int i = 0; i < TileExMolecularAssembler.MAX_THREAD; i++) {
            var inv = this.molecularAssembler.getCraftInventory(i);
            for (int j = 0; j < 9; j ++) {
                this.addSlot(new ExMolecularAssemblerPatternSlot(this, inv, j), SLOT[i]);
            }
            this.outputs.add((AppEngSlot) this.addSlot(new OutputSlot(inv, 9, null), SlotSemantics.MACHINE_OUTPUT));
        }
    }

    public void showPage() {
        for (int x = 0; x < TileExMolecularAssembler.MAX_THREAD; x ++) {
            for (var slot : this.getSlots(SLOT[x])) {
                if (slot instanceof AppEngSlot as) {
                    as.setSlotEnabled(this.page == x);
                }
            }
            this.outputs.get(x).setSlotEnabled(this.page == x);
        }
    }

    public boolean isValidItemForSlot(int slotIndex, ItemStack i) {
        var details = this.molecularAssembler.getCurrentPattern(this.page);
        if (details != null) {
            return details.isItemValid(slotIndex, AEItemKey.of(i), this.molecularAssembler.getLevel());
        }
        return false;
    }

    @Override
    public void broadcastChanges() {
        this.craftProgress = this.molecularAssembler.getCraftingProgress(this.page);
        this.standardDetectAndSendChanges();
    }

    @Override
    public int getCurrentProgress() {
        return this.craftProgress;
    }

    @Override
    public int getMaxProgress() {
        return MAX_CRAFT_PROGRESS;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getPage() {
        return this.page;
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }

    public static class ExMolecularAssemblerPatternSlot extends AppEngSlot implements IOptionalSlot {

        private final ContainerExMolecularAssembler mac;

        public ExMolecularAssemblerPatternSlot(ContainerExMolecularAssembler mac, InternalInventory inv, int invSlot) {
            super(inv, invSlot);
            this.mac = mac;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return super.mayPlace(stack) && this.mac.isValidItemForSlot(this.getSlotIndex(), stack);
        }

        @Override
        protected boolean getCurrentValidationState() {
            ItemStack stack = getItem();
            return stack.isEmpty() || mayPlace(stack);
        }

        @Override
        public boolean isRenderDisabled() {
            return true;
        }

        @Override
        public boolean isSlotEnabled() {
            if (!getInventory().getStackInSlot(getSlotIndex()).isEmpty()) {
                return true;
            }
            var pattern = mac.getHost().getCurrentPattern(this.mac.page);
            return getSlotIndex() >= 0 && getSlotIndex() < 9 && pattern != null && pattern.isSlotEnabled(getSlotIndex());
        }

        @Override
        public Point getBackgroundPos() {
            return new Point(x - 1, y - 1);
        }
    }


}
