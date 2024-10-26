package com.glodblock.github.extendedae.common.blocks.matrix;

import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixFrame;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockAssemblerMatrixFrame extends BlockAssemblerMatrixBase<TileAssemblerMatrixFrame> {

    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

    public BlockAssemblerMatrixFrame() {
        this.registerDefaultState(defaultBlockState().setValue(SHAPE, Shape.block));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHAPE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return getShapeType(defaultBlockState(), context.getLevel(), context.getClickedPos());
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos facingPos) {
        return getShapeType(state, level, pos);
    }

    @Override
    public Item getPresentItem() {
        return EPPItemAndBlock.ASSEMBLER_MATRIX_FRAME.asItem();
    }

    private BlockState getShapeType(BlockState baseState, LevelAccessor level, BlockPos pos) {
        var type = Shape.block;
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        // Detect whether controllers are on both sides of the x, y, and z axes
        final boolean xx = isFrame(level, x - 1, y, z) && isFrame(level, x + 1, y, z);
        final boolean yy = isFrame(level, x, y - 1, z) && isFrame(level, x, y + 1, z);
        final boolean zz = isFrame(level, x, y, z - 1) && isFrame(level, x, y, z + 1);

        if (xx && !yy && !zz) {
            type = Shape.column_x;
        } else if (!xx && yy && !zz) {
            type = Shape.column_y;
        } else if (!xx && !yy && zz) {
            type = Shape.column_z;
        }
        return baseState.setValue(SHAPE, type);
    }

    private static boolean isFrame(LevelAccessor level, int x, int y, int z) {
        return level.getBlockState(new BlockPos(x, y, z)).is(EPPItemAndBlock.ASSEMBLER_MATRIX_FRAME);
    }

    public enum Shape implements StringRepresentable {
        block, column_x, column_y, column_z;

        @Override
        public @NotNull String getSerializedName() {
            return this.name();
        }

    }

}
