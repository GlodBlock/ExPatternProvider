package com.glodblock.github.extendedae.common.blocks.matrix;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.common.blocks.BlockBaseGui;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixBase;
import com.glodblock.github.extendedae.container.ContainerAssemblerMatrix;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public abstract class BlockAssemblerMatrixBase<M extends TileAssemblerMatrixBase> extends BlockBaseGui<M> {

    public static final BooleanProperty FORMED = BooleanProperty.create("formed");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public BlockAssemblerMatrixBase() {
        this.registerDefaultState(defaultBlockState().setValue(FORMED, false).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
        builder.add(FORMED);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        var te = level.getBlockEntity(currentPos);
        if (te != null) {
            te.requestModelDataUpdate();
        }
        return super.updateShape(stateIn, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos, boolean isMoving) {
        var te = this.getBlockEntity(level, pos);
        if (te != null) {
            te.updateMultiBlock(fromPos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() == state.getBlock()) {
            return;
        }
        var cp = this.getBlockEntity(level, pos);
        if (cp != null) {
            cp.breakCluster();
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void openGui(M tile, Player p) {
        if (tile.isActive() && tile.isFormed()) {
            MenuOpener.open(ContainerAssemblerMatrix.TYPE, p, MenuLocators.forBlockEntity(tile));
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        var tile = this.getBlockEntity(level, pos);
        if (tile != null && !tile.isFormed()) {
            return InteractionResult.PASS;
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public InteractionResult check(M tile, ItemStack stack, Level world, BlockPos pos, BlockHitResult hit, Player p) {
        if (!(tile.isActive() || tile.isFormed())) {
            return InteractionResult.PASS;
        }
        return null;
    }

    public abstract Item getPresentItem();

}