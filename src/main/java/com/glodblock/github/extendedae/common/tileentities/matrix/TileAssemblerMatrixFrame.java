package com.glodblock.github.extendedae.common.tileentities.matrix;

import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileAssemblerMatrixFrame extends TileAssemblerMatrixBase {

    public TileAssemblerMatrixFrame(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileAssemblerMatrixFrame.class, TileAssemblerMatrixFrame::new, EPPItemAndBlock.ASSEMBLER_MATRIX_FRAME), pos, blockState);
    }

}
