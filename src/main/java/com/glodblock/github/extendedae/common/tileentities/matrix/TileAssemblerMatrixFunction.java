package com.glodblock.github.extendedae.common.tileentities.matrix;

import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TileAssemblerMatrixFunction extends TileAssemblerMatrixBase {

    public TileAssemblerMatrixFunction(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.getMainNode().setIdlePowerUsage(1);
    }

    public abstract void add(ClusterAssemblerMatrix c);

}
