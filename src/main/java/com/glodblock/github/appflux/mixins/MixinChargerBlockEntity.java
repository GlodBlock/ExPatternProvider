package com.glodblock.github.appflux.mixins;

import com.glodblock.github.appflux.xmod.wc.ChargerBlacklist;
import com.supermartijn642.wirelesschargers.ChargerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChargerBlockEntity.class)
public abstract class MixinChargerBlockEntity {

    @Redirect(
            method = "update",
            at = @At(value = "INVOKE", target = "net/minecraft/world/level/Level.getBlockEntity (Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;")
    )
    private BlockEntity blacklist(Level level, BlockPos pos) {
        var te = level.getBlockEntity(pos);
        // shortcut
        if (te == null) {
            return null;
        }
        for (var p : ChargerBlacklist.BLACKLIST) {
            if (p.test(te)) {
                return null;
            }
        }
        return te;
    }

}
