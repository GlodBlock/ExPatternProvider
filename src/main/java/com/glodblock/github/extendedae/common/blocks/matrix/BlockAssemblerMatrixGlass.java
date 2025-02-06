package com.glodblock.github.extendedae.common.blocks.matrix;

import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixGlass;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockAssemblerMatrixGlass extends BlockAssemblerMatrixBase<TileAssemblerMatrixGlass> {

    public BlockAssemblerMatrixGlass() {
        super(glassProps().noOcclusion().isViewBlocking((a, b, c) -> false));
    }

    @Override
    public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter blockGetter, @NotNull BlockPos pos) {
        return 0.5f;
    }

    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter blockGetter, @NotNull BlockPos p4) {
        return true;
    }

    @Override
    public Item getPresentItem() {
        return EAESingletons.ASSEMBLER_MATRIX_GLASS.asItem();
    }

}