package com.glodblock.github.extendedae.common.me.matrix;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.KeyCounter;
import appeng.me.cluster.IAECluster;
import appeng.me.cluster.MBCalculator;
import appeng.me.helpers.MachineSource;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixBase;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixCrafter;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixFunction;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixPattern;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ClusterAssemblerMatrix implements IAECluster {

    private final BlockPos boundsMin;
    private final BlockPos boundsMax;
    private boolean isDestroyed = false;
    private Component myName = null;
    private final List<TileAssemblerMatrixBase> tiles = new ArrayList<>();
    private MachineSource machineSrc = null;
    private final List<TileAssemblerMatrixPattern> patterns = new ArrayList<>();
    private final ReferenceSet<TileAssemblerMatrixCrafter> availableCrafters = new ReferenceOpenHashSet<>();
    private final ReferenceSet<TileAssemblerMatrixCrafter> busyCrafters = new ReferenceOpenHashSet<>();
    private final Reference2IntMap<TileAssemblerMatrixCrafter> crafterStatusCache = new Reference2IntOpenHashMap<>();
    private int speedCore = 0;

    public ClusterAssemblerMatrix(BlockPos boundsMin, BlockPos boundsMax) {
        this.boundsMin = boundsMin.immutable();
        this.boundsMax = boundsMax.immutable();
    }

    public void addCrafter(TileAssemblerMatrixCrafter crafter) {
        if (crafter.usedThread() < TileAssemblerMatrixCrafter.MAX_THREAD) {
            this.availableCrafters.add(crafter);
        } else {
            this.busyCrafters.add(crafter);
        }
    }

    public void addSpeedCore() {
        if (this.speedCore < 5) {
            this.speedCore++;
        }
    }

    public int getSpeedCore() {
        return this.speedCore;
    }

    public int getBusyCrafterAmount() {
        int cnt = this.busyCrafters.size() * TileAssemblerMatrixCrafter.MAX_THREAD;
        for (var crafter : this.availableCrafters) {
            cnt += crafter.usedThread();
        }
        return cnt;
    }

    public void updateCrafter(TileAssemblerMatrixCrafter crafter) {
        if (this.crafterStatusCache.containsKey(crafter)) {
            var previous = this.crafterStatusCache.getInt(crafter);
            if (previous == crafter.usedThread()) {
                return;
            }
        }
        this.crafterStatusCache.put(crafter, crafter.usedThread());
        this.availableCrafters.remove(crafter);
        this.busyCrafters.remove(crafter);
        this.addCrafter(crafter);
    }

    public void addPattern(TileAssemblerMatrixPattern pattern) {
        this.patterns.add(pattern);
    }

    @Override
    public BlockPos getBoundsMin() {
        return this.boundsMin;
    }

    @Override
    public BlockPos getBoundsMax() {
        return this.boundsMax;
    }

    @Override
    public void updateStatus(boolean updateGrid) {
        for (var r : this.tiles) {
            r.updateSubType(true);
        }
    }

    public List<TileAssemblerMatrixPattern> getPatterns() {
        return Collections.unmodifiableList(this.patterns);
    }

    public void done() {
        var core = this.getCore();
        core.setCore(true);
        if (core.getPreviousState() != null) {
            core.setPreviousState(null);
        }
        this.updateName();
    }

    @Nullable
    private TileAssemblerMatrixCrafter getAvailableCrafter() {
        if (this.availableCrafters.isEmpty()) {
            return null;
        }
        for (var c : this.availableCrafters) {
            return c;
        }
        return null;
    }

    @Override
    public void destroy() {
        if (this.isDestroyed) {
            return;
        }
        this.isDestroyed = true;
        boolean ownsModification = !MBCalculator.isModificationInProgress();
        if (ownsModification) {
            MBCalculator.setModificationInProgress(this);
        }
        try {
            for (var r : this.tiles) {
                r.updateStatus(null);
            }
        } finally {
            if (ownsModification) {
                MBCalculator.setModificationInProgress(null);
            }
        }
    }

    @Override
    public boolean isDestroyed() {
        return this.isDestroyed;
    }

    @Override
    public Iterator<TileAssemblerMatrixBase> getBlockEntities() {
        return this.tiles.iterator();
    }

    public void updateName() {
        this.myName = null;
        for (var te : this.tiles) {
            if (te.getCustomName() != null) {
                if (this.myName != null) {
                    this.myName.copy().append(" ").append(te.getCustomName());
                } else {
                    this.myName = te.getCustomName().copy();
                }
            }
        }
    }

    public void addTileEntity(TileAssemblerMatrixBase te) {
        if (this.machineSrc == null || te.isCore()) {
            this.machineSrc = new MachineSource(te);
        }
        te.setCore(false);
        te.saveChanges();
        this.tiles.add(te);
        if (te instanceof TileAssemblerMatrixFunction fun) {
            fun.add(this);
        }
    }

    public boolean isBusy() {
        return this.availableCrafters.isEmpty();
    }

    public boolean pushCraftingJob(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
        var crafter = this.getAvailableCrafter();
        if (crafter == null) {
            return false;
        }
        return crafter.pushJob(patternDetails, inputHolder);
    }

    public void breakCluster() {
        var t = this.getCore();
        if (t != null) {
            t.breakCluster();
        }
    }

    private TileAssemblerMatrixBase getCore() {
        if (this.machineSrc == null) {
            return null;
        }
        return (TileAssemblerMatrixBase) this.machineSrc.machine().orElse(null);
    }

    public IActionSource getSrc() {
        return Objects.requireNonNull(this.machineSrc);
    }

    public Component getName() {
        return this.myName;
    }

    @Nullable
    public IGridNode getNode() {
        var core = getCore();
        return core != null ? core.getActionableNode() : null;
    }

}
