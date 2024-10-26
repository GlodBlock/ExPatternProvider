package com.glodblock.github.extendedae.common.tileentities.matrix;

import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileAssemblerMatrixSpeed extends TileAssemblerMatrixFunction {

    public TileAssemblerMatrixSpeed(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileAssemblerMatrixSpeed.class, TileAssemblerMatrixSpeed::new, EPPItemAndBlock.ASSEMBLER_MATRIX_SPEED), pos, blockState);
    }

    @Override
    public void add(ClusterAssemblerMatrix c) {
        c.addSpeedCore();
    }

}
