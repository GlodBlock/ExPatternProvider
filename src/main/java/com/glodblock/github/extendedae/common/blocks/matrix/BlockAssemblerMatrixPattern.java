package com.glodblock.github.extendedae.common.blocks.matrix;

import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixPattern;
import net.minecraft.world.item.Item;

public class BlockAssemblerMatrixPattern extends BlockAssemblerMatrixBase<TileAssemblerMatrixPattern> {

    @Override
    public Item getPresentItem() {
        return EAESingletons.ASSEMBLER_MATRIX_PATTERN.asItem();
    }

}