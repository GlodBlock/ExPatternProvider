package com.glodblock.github.extendedae.common.me.matrix;

import appeng.me.cluster.IAEMultiBlock;
import appeng.me.cluster.MBCalculator;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixBase;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixCrafter;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixFrame;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixFunction;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixPattern;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixWall;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CalculatorAssemblerMatrix extends MBCalculator<TileAssemblerMatrixBase, ClusterAssemblerMatrix> {

    private static final int MAX_SIZE = 6;
    private static final int MIN_SIZE = 2;

    public CalculatorAssemblerMatrix(TileAssemblerMatrixBase t) {
        super(t);
    }

    @Override
    public boolean checkMultiblockScale(BlockPos min, BlockPos max) {
        if (max.getX() - min.getX() > MAX_SIZE) {
            return false;
        }
        if (max.getY() - min.getY() > MAX_SIZE) {
            return false;
        }
        if (max.getZ() - min.getZ() > MAX_SIZE) {
            return false;
        }
        if (max.getX() - min.getX() < MIN_SIZE) {
            return false;
        }
        if (max.getY() - min.getY() < MIN_SIZE) {
            return false;
        }
        return max.getZ() - min.getZ() >= MIN_SIZE;
    }

    @Override
    public ClusterAssemblerMatrix createCluster(ServerLevel level, BlockPos min, BlockPos max) {
        return new ClusterAssemblerMatrix(min, max);
    }

    @Override
    public boolean verifyInternalStructure(ServerLevel level, BlockPos min, BlockPos max) {
        boolean anyPattern = false;
        boolean anyCrafter = false;
        for (var pos : BlockPos.betweenClosed(min, max)) {
            var te = (IAEMultiBlock<?>) level.getBlockEntity(pos);
            if (te == null || !te.isValid()) {
                return false;
            }
            if (anyPattern || te instanceof TileAssemblerMatrixPattern) {
                anyPattern = true;
            }
            if (anyCrafter || te instanceof TileAssemblerMatrixCrafter) {
                anyCrafter = true;
            }
            if (isInternal(pos, min, max)) {
                if (!(te instanceof TileAssemblerMatrixFunction)) {
                    return false;
                }
            } else if (isEdge(pos, min, max)) {
                if (!(te instanceof TileAssemblerMatrixFrame)) {
                    return false;
                }
            } else {
                if (!(te instanceof TileAssemblerMatrixWall)) {
                    return false;
                }
            }
        }
        return anyCrafter && anyPattern;
    }

    @Override
    public void updateBlockEntities(ClusterAssemblerMatrix c, ServerLevel level, BlockPos min, BlockPos max) {
        for (var pos : BlockPos.betweenClosed(min, max)) {
            var te = (TileAssemblerMatrixBase) level.getBlockEntity(pos);
            if (te != null) {
                te.updateStatus(c);
                c.addTileEntity(te);
            }
        }
        c.done();
    }

    @Override
    public boolean isValidBlockEntity(BlockEntity te) {
        return te instanceof TileAssemblerMatrixBase;
    }

    private boolean isInternal(BlockPos pos, BlockPos min, BlockPos max) {
        return pos.getX() < max.getX() && pos.getX() > min.getX() &&
                pos.getY() < max.getY() && pos.getY() > min.getY() &&
                pos.getZ() < max.getZ() && pos.getZ() > min.getZ();
    }

    private boolean isEdge(BlockPos pos, BlockPos min, BlockPos max) {
        return (min.getX() == pos.getX() && min.getY() == pos.getY()) ||
                (min.getX() == pos.getX() && min.getZ() == pos.getZ()) ||
                (min.getY() == pos.getY() && min.getZ() == pos.getZ()) ||
                (max.getX() == pos.getX() && max.getY() == pos.getY()) ||
                (max.getX() == pos.getX() && max.getZ() == pos.getZ()) ||
                (max.getY() == pos.getY() && max.getZ() == pos.getZ()) ||
                (min.getX() == pos.getX() && max.getY() == pos.getY()) ||
                (min.getX() == pos.getX() && max.getZ() == pos.getZ()) ||
                (min.getY() == pos.getY() && max.getX() == pos.getX()) ||
                (min.getY() == pos.getY() && max.getZ() == pos.getZ()) ||
                (min.getZ() == pos.getZ() && max.getX() == pos.getX()) ||
                (min.getZ() == pos.getZ() && max.getY() == pos.getY());
    }

}
