package com.glodblock.github.extendedae.mixins;

import appeng.api.networking.IManagedGridNode;
import appeng.parts.AEBasePart;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AEBasePart.class)
public abstract class MixinAEBasePart {

    @Final
    @Shadow
    private IManagedGridNode mainNode;

    @Shadow
    public abstract Level getLevel();

    @Shadow
    private BlockEntity blockEntity;

    @Redirect(
            method = "readFromNBT",
            at = @At(value = "INVOKE", target = "Lappeng/api/networking/IManagedGridNode;loadFromNBT(Lnet/minecraft/nbt/CompoundTag;)V"),
            remap = false
    )
    private void bypassNodeLoad(IManagedGridNode node, CompoundTag tag) {
        if (!tag.contains("BYPASS_EXTENDEDAE")) {
            node.loadFromNBT(tag);
        }
    }

    /**
     * @author GlodBlock
     * @reason Stop throwing Exception
     */
    @Overwrite(remap = false)
    public void addToWorld() {
        try {
            this.mainNode.create(this.getLevel(), this.blockEntity.getBlockPos());
        } catch (IllegalStateException ignored) {
            // NO-OP
        }
    }

}
