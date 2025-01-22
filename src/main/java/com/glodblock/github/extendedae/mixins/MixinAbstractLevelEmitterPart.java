package com.glodblock.github.extendedae.mixins;

import appeng.parts.automation.AbstractLevelEmitterPart;
import com.glodblock.github.extendedae.common.parts.PartThresholdLevelEmitter;
import net.minecraft.core.particles.DustParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractLevelEmitterPart.class)
public abstract class MixinAbstractLevelEmitterPart {

    @Redirect(
            method = "animateTick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/core/particles/DustParticleOptions;REDSTONE:Lnet/minecraft/core/particles/DustParticleOptions;"),
            remap = false
    )
    private DustParticleOptions setBlueColor() {
        if (((Object) this) instanceof PartThresholdLevelEmitter) {
            return PartThresholdLevelEmitter.BLUE;
        }
        return DustParticleOptions.REDSTONE;
    }

}
