package com.glodblock.github.extendedae.common.blocks;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.common.tileentities.TileCircuitCutter;
import com.glodblock.github.extendedae.container.ContainerCircuitCutter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BlockCircuitCutter extends BlockBaseGui<TileCircuitCutter> {

    public final static BooleanProperty WORKING = BooleanProperty.create("working");

    public BlockCircuitCutter() {
        super(metalProps().noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(WORKING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WORKING);
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.full();
    }

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, TileCircuitCutter be) {
        return currentState.setValue(WORKING, be.isWorking());
    }

    @Override
    public void openGui(TileCircuitCutter tile, Player p) {
        MenuOpener.open(ContainerCircuitCutter.TYPE, p, MenuLocators.forBlockEntity(tile));
    }

}
