package com.glodblock.github.extendedae.client.gui.widget;

import appeng.crafting.pattern.EncodedPatternItem;
import appeng.menu.slot.AppEngSlot;
import appeng.util.inv.AppEngInternalInventory;
import com.glodblock.github.extendedae.util.MutableSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AssemblerMatrixSlot extends AppEngSlot {

    private final AppEngInternalInventory machineInv;
    private final long id;
    private final int offset;

    public AssemblerMatrixSlot(AppEngInternalInventory machineInv, int machineInvSlot, int offset, long id, int x, int y) {
        super(machineInv, machineInvSlot);
        this.machineInv = machineInv;
        this.id = id;
        this.offset = offset;
        ((MutableSlot) this).setXPos(x);
        ((MutableSlot) this).setYPos(y);
    }

    public int getActuallySlot() {
        return this.getSlotIndex() + this.offset;
    }

    public long getID() {
        return this.id;
    }

    @Override
    public ItemStack getDisplayStack() {
        if (isRemote()) {
            final ItemStack is = super.getDisplayStack();
            if (!is.isEmpty() && is.getItem() instanceof EncodedPatternItem iep) {
                final ItemStack out = iep.getOutput(is);
                if (!out.isEmpty()) {
                    return out;
                }
            }
        }
        return super.getDisplayStack();
    }

    @Override
    public boolean hasItem() {
        return !this.getItem().isEmpty();
    }

    public AppEngInternalInventory getMachineInv() {
        return this.machineInv;
    }

    @Override
    public final boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public final void set(ItemStack stack) {
    }

    @Override
    public void initialize(ItemStack stack) {
    }

    @Override
    public final int getMaxStackSize() {
        return 0;
    }

    @Override
    public final ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public final boolean mayPickup(Player player) {
        return false;
    }
}
