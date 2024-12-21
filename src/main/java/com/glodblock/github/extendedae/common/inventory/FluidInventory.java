package com.glodblock.github.extendedae.common.inventory;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKeyType;
import appeng.helpers.externalstorage.GenericStackInv;
import org.jetbrains.annotations.Nullable;

public class FluidInventory extends GenericStackInv {

    public FluidInventory(@Nullable Runnable listener, int size, int capacity) {
        super(listener, size);
        this.setFilter(key -> key instanceof AEFluidKey);
        this.setCapacity(AEKeyType.fluids(), capacity);
    }

    public FluidInventory(@Nullable Runnable listener, Mode mode, int size, int capacity) {
        super(listener, mode, size);
        this.setFilter(key -> key instanceof AEFluidKey);
        this.setCapacity(AEKeyType.fluids(), capacity);
    }

}
