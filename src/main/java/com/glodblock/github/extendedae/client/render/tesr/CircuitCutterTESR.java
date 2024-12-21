package com.glodblock.github.extendedae.client.render.tesr;

import appeng.client.render.renderable.ItemRenderable;
import appeng.client.render.tesr.ModularTESR;
import com.glodblock.github.extendedae.common.tileentities.TileCircuitCutter;
import com.glodblock.github.glodium.util.GlodUtil;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CircuitCutterTESR extends ModularTESR<TileCircuitCutter> {

    private static final float HALF_PI = (float) (Math.PI / 2f);

    public CircuitCutterTESR(BlockEntityRendererProvider.Context context) {
        super(new ItemRenderable<>(CircuitCutterTESR::renderItem));
    }

    private static Pair<ItemStack, Transformation> renderItem(TileCircuitCutter te) {
        float progress = (float) GlodUtil.clamp((double) te.getProgress() / TileCircuitCutter.MAX_PROGRESS, 0, 1);
        var stack = progress > 0.5 ? te.getRenderOutput() : te.getInput().getStackInSlot(0);
        return new ImmutablePair<>(stack, getTransformer(progress * 0.5f + 0.25f, stack.getItem() instanceof BlockItem));
    }

    private static Transformation getTransformer(float offset, boolean isBlock) {
        return isBlock ?
                new Transformation(
                        new Vector3f(offset, 0.5f, 0.66f),
                        new Quaternionf().rotateYXZ(0, -HALF_PI, 0),
                        null, null) :
                new Transformation(
                        new Vector3f(offset, 0.5f, 0.63f),
                        new Quaternionf().rotateYXZ(0, -HALF_PI, 0),
                        null, null);
    }

}
