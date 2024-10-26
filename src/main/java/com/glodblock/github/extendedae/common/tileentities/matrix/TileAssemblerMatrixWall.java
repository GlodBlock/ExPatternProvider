package com.glodblock.github.extendedae.common.tileentities.matrix;

import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileAssemblerMatrixWall extends TileAssemblerMatrixBase {

    public TileAssemblerMatrixWall(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileAssemblerMatrixWall.class, TileAssemblerMatrixWall::new, EPPItemAndBlock.ASSEMBLER_MATRIX_WALL), pos, blockState);
    }

}
