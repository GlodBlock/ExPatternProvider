package com.glodblock.github.extendedae.mixins;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.hooks.RenderBlockOutlineHook;
import appeng.parts.PartPlacement;
import com.glodblock.github.extendedae.common.items.ItemPackedDevice;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBlockOutlineHook.class)
public abstract class MixinRenderBlockOutlineHook {

    @Shadow
    private static void renderPart(PoseStack poseStack, MultiBufferSource buffers, Camera camera, BlockPos pos, IPart part, Direction side, boolean preview, boolean insideBlock) {
    }

    @Inject(
            method = "showPartPlacementPreview",
            at = @At("TAIL"),
            remap = false
    )
    private static void renderPackedDevicePreview(Player player, PoseStack poseStack, MultiBufferSource buffers, Camera camera, BlockHitResult blockHitResult, ItemStack itemInHand, boolean insideBlock, CallbackInfo ci) {
        if (itemInHand.getItem() instanceof ItemPackedDevice packed) {
            var ctx = itemInHand.getTag();
            if (ctx == null) {
                return;
            }
            if (packed.checkNBT(ctx) && ctx.getBoolean("part")) {
                var itemO = ForgeRegistries.ITEMS.getHolder(new ResourceLocation(ctx.getString("id")));
                if (itemO.isPresent()) {
                    var item = itemO.get().get();
                    if (item instanceof IPartItem<?> partItem) {
                        var placement = PartPlacement.getPartPlacement(player, player.level(), new ItemStack(partItem), blockHitResult.getBlockPos(), blockHitResult.getDirection(), blockHitResult.getLocation());
                        if (placement != null) {
                            var part = partItem.createPart();
                            renderPart(poseStack, buffers, camera, placement.pos(), part, placement.side(), true, insideBlock);
                        }
                    }
                }
            }
        }
    }

}
