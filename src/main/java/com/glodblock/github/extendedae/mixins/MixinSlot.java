package com.glodblock.github.extendedae.mixins;

import com.glodblock.github.extendedae.util.MutableSlot;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Slot.class)
public abstract class MixinSlot implements MutableSlot {

    @Mutable
    @Shadow
    @Final
    public int x;

    @Mutable
    @Shadow
    @Final
    public int y;

    @Override
    public void setXPos(int x) {
        this.x = x;
    }

    @Override
    public void setYPos(int y) {
        this.y = y;
    }

}
