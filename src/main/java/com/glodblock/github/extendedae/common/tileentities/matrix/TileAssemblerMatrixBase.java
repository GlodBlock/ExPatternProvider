package com.glodblock.github.extendedae.common.tileentities.matrix;

import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridMultiblock;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.orientation.BlockOrientation;
import appeng.blockentity.grid.AENetworkBlockEntity;
import appeng.me.cluster.IAEMultiBlock;
import appeng.util.iterators.ChainedIterator;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.common.blocks.matrix.BlockAssemblerMatrixBase;
import com.glodblock.github.extendedae.common.me.matrix.CalculatorAssemblerMatrix;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

public abstract class TileAssemblerMatrixBase extends AENetworkBlockEntity implements IAEMultiBlock<ClusterAssemblerMatrix>, IPowerChannelState {

    protected final CalculatorAssemblerMatrix calc = new CalculatorAssemblerMatrix(this);
    protected boolean isCore = false;
    protected CompoundTag previousState = null;
    protected ClusterAssemblerMatrix cluster;

    public TileAssemblerMatrixBase(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.getMainNode().setFlags(GridFlags.MULTIBLOCK, GridFlags.REQUIRE_CHANNEL).addService(IGridMultiblock.class, this::getMultiblockNodes);
        this.getMainNode().setIdlePowerUsage(0);
    }

    public CompoundTag getPreviousState() {
        return this.previousState;
    }

    public void setPreviousState(CompoundTag previousState) {
        this.previousState = previousState;
    }

    public boolean isCore() {
        return this.isCore;
    }

    public void setCore(boolean core) {
        this.isCore = core;
    }

    @Override
    protected Item getItemFromBlockEntity() {
        if (this.level == null) {
            return Items.AIR;
        }
        return getMatrixBlock().getPresentItem();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        if (this.cluster != null) {
            this.cluster.updateName();
        }
    }

    @Override
    public void onReady() {
        super.onReady();
        this.getMainNode().setVisualRepresentation(this.getItemFromBlockEntity());
        if (level instanceof ServerLevel serverLevel) {
            this.calc.calculateMultiblock(serverLevel, worldPosition);
        }
    }

    public void updateMultiBlock(BlockPos changedPos) {
        if (level instanceof ServerLevel serverLevel) {
            this.calc.updateMultiblockAfterNeighborUpdate(serverLevel, worldPosition, changedPos);
        }
    }

    public void breakCluster() {
        if (this.cluster != null) {
            var places = new ArrayList<BlockPos>();
            for (var blockEntity : (Iterable<? extends TileAssemblerMatrixBase>) this.cluster::getBlockEntities) {
                if (this == blockEntity) {
                    places.add(worldPosition);
                } else {
                    for (var d : Direction.values()) {
                        var p = blockEntity.worldPosition.relative(d);
                        if (this.level.isEmptyBlock(p)) {
                            places.add(p);
                        }
                    }
                }
            }
            if (places.isEmpty()) {
                throw new IllegalStateException(this.cluster + " does not contain any kind of blocks, which were destroyed.");
            }
            this.cluster.destroy();
        }
    }

    public boolean isFormed() {
        if (isClientSide()) {
            return getBlockState().getValue(BlockAssemblerMatrixBase.FORMED);
        }
        return this.cluster != null;
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        data.putBoolean("core", this.isCore);
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        this.setCore(data.getBoolean("core"));
        if (this.isCore) {
            this.setPreviousState(data.copy());
        }
    }

    @Override
    public void disconnect(boolean update) {
        if (this.cluster != null) {
            this.cluster.destroy();
            if (update) {
                this.updateSubType(true);
            }
        }
    }

    @Override
    public ClusterAssemblerMatrix getCluster() {
        return this.cluster;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        if (reason != IGridNodeListener.State.GRID_BOOT) {
            this.updateSubType(false);
        }
    }

    @Override
    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        requestModelDataUpdate();
    }

    @Override
    public boolean isActive() {
        if (!isClientSide()) {
            return this.getMainNode().isActive();
        }
        return this.isPowered() && this.isFormed();
    }

    public void updateStatus(ClusterAssemblerMatrix c) {
        if (this.cluster != null && this.cluster != c) {
            this.cluster.breakCluster();
        }

        this.cluster = c;
        this.updateSubType(true);
    }

    public void updateSubType(boolean updateFormed) {
        if (this.level == null || this.notLoaded() || this.isRemoved()) {
            return;
        }

        final boolean formed = this.isFormed();
        boolean power = this.getMainNode().isOnline();

        final BlockState current = this.level.getBlockState(this.worldPosition);

        if (current.getBlock() instanceof BlockAssemblerMatrixBase<?>) {
            final BlockState newState = current
                    .setValue(BlockAssemblerMatrixBase.POWERED, power)
                    .setValue(BlockAssemblerMatrixBase.FORMED, formed);
            if (current != newState) {
                this.level.setBlock(this.worldPosition, newState, Block.UPDATE_CLIENTS);
            }
        }

        if (updateFormed) {
            onGridConnectableSidesChanged();
        }
    }

    @Override
    public boolean isPowered() {
        if (isClientSide()) {
            return this.level.getBlockState(this.worldPosition).getValue(BlockAssemblerMatrixBase.POWERED);
        }
        return this.getMainNode().isActive();
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        if (isFormed()) {
            return EnumSet.allOf(Direction.class);
        } else {
            return EnumSet.noneOf(Direction.class);
        }
    }

    public BlockAssemblerMatrixBase<?> getMatrixBlock() {
        if (this.level == null || this.notLoaded() || this.isRemoved()) {
            return EPPItemAndBlock.ASSEMBLER_MATRIX_FRAME;
        }
        return (BlockAssemblerMatrixBase<?>) this.level.getBlockState(this.worldPosition).getBlock();
    }

    private Iterator<IGridNode> getMultiblockNodes() {
        if (this.getCluster() == null) {
            return new ChainedIterator<>();
        }
        var nodes = new ArrayList<IGridNode>();
        var it = this.getCluster().getBlockEntities();
        while (it.hasNext()) {
            var node = it.next().getGridNode();
            if (node != null) {
                nodes.add(node);
            }
        }
        return nodes.iterator();
    }

}
