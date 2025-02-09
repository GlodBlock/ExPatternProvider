package com.glodblock.github.extendedae.common.tileentities.matrix;

import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileAssemblerMatrixWall extends TileAssemblerMatrixBase {

    public TileAssemblerMatrixWall(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileAssemblerMatrixWall.class, TileAssemblerMatrixWall::new, EAESingletons.ASSEMBLER_MATRIX_WALL), pos, blockState);
    }

    public TileAssemblerMatrixWall(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

}
