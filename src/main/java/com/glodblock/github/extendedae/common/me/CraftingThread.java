package com.glodblock.github.extendedae.common.me;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.crafting.IMolecularAssemblerSupportedPattern;
import appeng.core.AELog;
import appeng.me.helpers.IGridConnectedBlockEntity;
import appeng.menu.AutoCraftingMenu;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.filter.IAEItemFilter;
import com.glodblock.github.extendedae.ExtendedAE;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftingThread {

    @NotNull
    private final AEBaseBlockEntity host;
    private final IGridConnectedBlockEntity girdHost;
    private final AppEngInternalInventory gridInv;
    private final InternalInventory gridInvExt;
    private final CraftingContainer craftingInv;
    private Direction pushDirection = null;
    private ItemStack myPattern = ItemStack.EMPTY;
    private IMolecularAssemblerSupportedPattern myPlan = null;
    private double progress = 0;
    private boolean isAwake = false;
    private boolean forcePlan = false;
    private boolean reboot = true;
    private ItemStack output = ItemStack.EMPTY;
    private Pusher pusher = this::pushTo;

    public CraftingThread(@NotNull AEBaseBlockEntity host) {
        if (!(host instanceof InternalInventoryHost)) {
            throw new IllegalArgumentException("Host isn't InternalInventoryHost.");
        }
        if (!(host instanceof IGridConnectedBlockEntity)) {
            throw new IllegalArgumentException("Host isn't IGridConnectedBlockEntity.");
        }
        this.host = host;
        this.girdHost = (IGridConnectedBlockEntity) host;
        this.gridInv = new AppEngInternalInventory((InternalInventoryHost) this.host, 10, 1);
        this.gridInvExt = new FilteredInternalInventory(this.gridInv, new CraftingGridFilter());
        this.craftingInv = new TransientCraftingContainer(new AutoCraftingMenu(), 3, 3);
    }

    public void setPusher(Pusher pusher) {
        this.pusher = pusher;
    }

    public boolean isAwake() {
        return this.isAwake;
    }

    public boolean acceptJob(IPatternDetails patternDetails, KeyCounter[] table, Direction where) {
        if (this.myPattern.isEmpty()) {
            if (this.gridInv.isEmpty() && patternDetails instanceof IMolecularAssemblerSupportedPattern pattern) {
                this.forcePlan = true;
                this.myPlan = pattern;
                this.pushDirection = where;
                this.fillGrid(table, pattern);
                this.updateSleepiness();
                this.saveChanges();
                return true;
            }
        }
        return false;
    }

    public void stop() {
        this.myPlan = null;
        this.myPattern = ItemStack.EMPTY;
        this.progress = 0;
        this.ejectHeldItems();
        this.updateSleepiness();
    }

    public CompoundTag writeNBT() {
        var data = new CompoundTag();
        if (this.forcePlan) {
            var pattern = this.myPlan != null ? this.myPlan.getDefinition().toStack() : this.myPattern;
            if (!pattern.isEmpty()) {
                var compound = new CompoundTag();
                pattern.save(compound);
                data.put("myPlan", compound);
                data.putInt("pushDirection", this.pushDirection.ordinal());
            }
        }
        return data;
    }

    public void readNBT(CompoundTag data) {
        this.forcePlan = false;
        this.myPattern = ItemStack.EMPTY;
        this.myPlan = null;
        if (data.contains("myPlan")) {
            var pattern = ItemStack.of(data.getCompound("myPlan"));
            if (!pattern.isEmpty()) {
                this.forcePlan = true;
                this.myPattern = pattern;
                this.pushDirection = Direction.values()[data.getInt("pushDirection")];
            }
        }
        this.recalculatePlan();
    }

    public InternalInventory getInternalInventory() {
        return this.gridInv;
    }

    public InternalInventory getExposedInventoryForSide() {
        return this.gridInvExt;
    }

    public int getCraftingProgress() {
        return (int) this.progress;
    }

    public TickRateModulation tick(int cards, int ticksSinceLastCall) {
        if (!this.gridInv.getStackInSlot(9).isEmpty()) {
            this.pushOut(this.gridInv.getStackInSlot(9));
            if (this.gridInv.getStackInSlot(9).isEmpty()) {
                this.saveChanges();
            }
            this.ejectHeldItems();
            this.updateSleepiness();
            this.progress = 0;
            return this.isAwake ? TickRateModulation.IDLE : TickRateModulation.SLEEP;
        }

        if (this.myPlan == null) {
            // clear possible jammed stuffs
            this.ejectHeldItems();
            this.updateSleepiness();
            return TickRateModulation.SLEEP;
        }

        if (this.reboot) {
            ticksSinceLastCall = 1;
        }

        if (!this.isAwake) {
            return TickRateModulation.SLEEP;
        }

        this.reboot = false;
        switch (cards) {
            case 0 -> this.progress += this.userPower(ticksSinceLastCall, 20, 1.0);
            case 1 -> this.progress += this.userPower(ticksSinceLastCall, 26, 1.3);
            case 2 -> this.progress += this.userPower(ticksSinceLastCall, 34, 1.7);
            case 3 -> this.progress += this.userPower(ticksSinceLastCall, 40, 2.0);
            case 4 -> this.progress += this.userPower(ticksSinceLastCall, 50, 2.5);
            case 5 -> this.progress += this.userPower(ticksSinceLastCall, 100, 5.0);
        }

        if (this.progress >= 100) {
            for (int x = 0; x < this.craftingInv.getContainerSize(); x++) {
                this.craftingInv.setItem(x, this.gridInv.getStackInSlot(x));
            }

            this.progress = 0;
            this.output = this.myPlan.assemble(this.craftingInv, this.host.getLevel());
            if (!this.output.isEmpty()) {
                // pushOut might reset the plan back to null, so get the remaining items before
                var craftingRemainders = this.myPlan.getRemainingItems(this.craftingInv);

                this.pushOut(this.output.copy());

                for (int x = 0; x < this.craftingInv.getContainerSize(); x++) {
                    this.gridInv.setItemDirect(x, craftingRemainders.get(x));
                }
                this.forcePlan = false;
                this.myPlan = null;
                this.pushDirection = null;
                this.ejectHeldItems();
                this.saveChanges();
                this.updateSleepiness();
                return this.isAwake ? TickRateModulation.IDLE : TickRateModulation.SLEEP;
            } else {
                ExtendedAE.LOGGER.warn("Molecular Assembler failed to craft, the crafting ingredients are returned.");
                this.forcePlan = false;
                this.myPlan = null;
                this.pushDirection = null;
                this.ejectHeldItems();
                this.saveChanges();
                this.updateSleepiness();
            }
        }
        return TickRateModulation.FASTER;
    }

    public void forceAwake() {
        this.isAwake = true;
    }

    public void recalculatePlan() {
        this.reboot = true;
        if (this.forcePlan) {
            if (this.host.getLevel() != null && myPlan == null) {
                if (!myPattern.isEmpty()) {
                    if (PatternDetailsHelper.decodePattern(myPattern, this.host.getLevel(), false) instanceof IMolecularAssemblerSupportedPattern supportedPlan) {
                        this.myPlan = supportedPlan;
                    }
                }

                this.myPattern = ItemStack.EMPTY;
                if (myPlan == null) {
                    AELog.warn("Unable to restore auto-crafting pattern after load: %s", myPattern.getTag());
                    this.forcePlan = false;
                }
            }
            return;
        }

        this.progress = 0;
        this.myPlan = null;
        this.myPattern = ItemStack.EMPTY;
        this.pushDirection = null;
        this.updateSleepiness();
    }

    @Nullable
    public IMolecularAssemblerSupportedPattern getCurrentPattern() {
        if (this.host.isClientSide()) {
            return null;
        } else {
            return myPlan;
        }
    }

    private int userPower(int ticksPassed, int bonusValue, double acceleratorTax) {
        var grid = this.girdHost.getMainNode().getGrid();
        if (grid != null) {
            var safePower = Math.min(ticksPassed * bonusValue * acceleratorTax, 5000);
            return (int) (grid.getEnergyService().extractAEPower(safePower, Actionable.MODULATE, PowerMultiplier.CONFIG) / acceleratorTax);
        } else {
            return 0;
        }
    }

    private void ejectHeldItems() {
        if (this.gridInv.getStackInSlot(9).isEmpty()) {
            for (int x = 0; x < 9; x++) {
                final ItemStack is = this.gridInv.getStackInSlot(x);
                if (!is.isEmpty() && (this.myPlan == null || !this.myPlan.isItemValid(x, AEItemKey.of(is), this.host.getLevel()))) {
                    this.gridInv.setItemDirect(9, is);
                    this.gridInv.setItemDirect(x, ItemStack.EMPTY);
                    this.saveChanges();
                    return;
                }
            }
        }
    }

    private void pushOut(ItemStack output) {
        if (this.pushDirection == null) {
            for (Direction d : Direction.values()) {
                output = this.pusher.push(output, d);
            }
        } else {
            output = this.pusher.push(output, this.pushDirection);
        }
        if (output.isEmpty() && this.forcePlan) {
            this.forcePlan = false;
            this.recalculatePlan();
        }
        this.gridInv.setItemDirect(9, output);
    }

    private void saveChanges() {
        this.host.saveChanges();
    }

    private ItemStack pushTo(ItemStack output, Direction d) {
        if (output.isEmpty()) {
            return output;
        }
        final BlockEntity te = this.host.getLevel().getBlockEntity(this.host.getBlockPos().relative(d));
        if (te == null) {
            return output;
        }
        var adaptor = InternalInventory.wrapExternal(te, d.getOpposite());
        if (adaptor == null) {
            return output;
        }
        final int size = output.getCount();
        output = adaptor.addItems(output);
        final int newSize = output.isEmpty() ? 0 : output.getCount();
        if (size != newSize) {
            this.saveChanges();
        }
        return output;
    }


    private void fillGrid(KeyCounter[] table, IMolecularAssemblerSupportedPattern adapter) {
        adapter.fillCraftingGrid(table, this.gridInv::setItemDirect);
        // Sanity check
        for (var list : table) {
            list.removeZeros();
            if (!list.isEmpty()) {
                throw new RuntimeException("Could not fill grid with some items, including " + list.iterator().next());
            }
        }
    }

    public void updateSleepiness() {
        final boolean wasEnabled = this.isAwake;
        this.isAwake = this.myPlan != null && this.hasMats() || this.canPush();
        if (wasEnabled != this.isAwake) {
            this.girdHost.getMainNode().ifPresent((grid, node) -> {
                if (this.isAwake) {
                    grid.getTickManager().wakeDevice(node);
                } else {
                    grid.getTickManager().sleepDevice(node);
                }
            });
        }
    }

    private boolean hasMats() {
        if (this.myPlan == null) {
            return false;
        }
        for (int x = 0; x < this.craftingInv.getContainerSize(); x++) {
            this.craftingInv.setItem(x, this.gridInv.getStackInSlot(x));
        }
        return !this.myPlan.assemble(this.craftingInv, this.host.getLevel()).isEmpty();
    }

    private boolean canPush() {
        return !this.gridInv.getStackInSlot(9).isEmpty();
    }

    public ItemStack getOutput() {
        return this.output;
    }

    private static class CraftingGridFilter implements IAEItemFilter {
        @Override
        public boolean allowExtract(InternalInventory inv, int slot, int amount) {
            return slot == 9;
        }

        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return false;
        }
    }

    public interface Pusher {

        ItemStack push(ItemStack stack, Direction d);

    }

}
