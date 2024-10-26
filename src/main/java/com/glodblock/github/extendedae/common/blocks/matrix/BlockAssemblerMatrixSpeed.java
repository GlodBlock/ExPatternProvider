package com.glodblock.github.extendedae.common.blocks.matrix;

import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixSpeed;
import net.minecraft.world.item.Item;

public class BlockAssemblerMatrixSpeed extends BlockAssemblerMatrixBase<TileAssemblerMatrixSpeed> {

    @Override
    public Item getPresentItem() {
        return EPPItemAndBlock.ASSEMBLER_MATRIX_SPEED.asItem();
    }

}