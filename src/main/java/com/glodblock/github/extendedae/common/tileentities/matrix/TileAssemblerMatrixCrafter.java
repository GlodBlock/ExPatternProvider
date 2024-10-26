package com.glodblock.github.extendedae.common.tileentities.matrix;

import appeng.api.config.Actionable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.storage.IStorageService;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.common.me.CraftingThread;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TileAssemblerMatrixCrafter extends TileAssemblerMatrixFunction implements InternalInventoryHost, IGridTickable {

    public static final int MAX_THREAD = 8;
    private final CraftingThread[] threads = new CraftingThread[MAX_THREAD];
    private final InternalInventory internalInv;

    public TileAssemblerMatrixCrafter(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileAssemblerMatrixCrafter.class, TileAssemblerMatrixCrafter::new, EPPItemAndBlock.ASSEMBLER_MATRIX_CRAFTER), pos, blockState);
        this.getMainNode().addService(IGridTickable.class, this);
        var invs = new InternalInventory[MAX_THREAD];
        for (int x = 0; x < MAX_THREAD; x ++) {
            this.threads[x] = new CraftingThread(this);
            this.threads[x].setPusher(this::pushResult);
            invs[x] = this.threads[x].getInternalInventory();
        }
        this.internalInv = new CombinedInternalInventory(invs);
    }

    public int usedThread() {
        int cnt = 0;
        for (var t : this.threads) {
            if (!t.getInternalInventory().isEmpty()) {
                cnt ++;
            }
        }
        return cnt;
    }

    public boolean pushJob(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
        for (var thread : this.threads) {
            if (thread.acceptJob(patternDetails, inputHolder, Direction.DOWN)) {
                this.cluster.updateCrafter(this);
                return true;
            }
        }
        return false;
    }

    public void stop() {
        for (var thread : this.threads) {
            thread.stop();
        }
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        for (int x = 0; x < MAX_THREAD; x ++) {
            var tag = this.threads[x].writeNBT();
            data.put("#ct" + x, tag);
        }
        final CompoundTag opt = new CompoundTag();
        for (int x = 0; x < this.internalInv.size(); x++) {
            var is = this.internalInv.getStackInSlot(x);
            opt.put("item" + x, is.save(new CompoundTag()));
        }
        data.put("inv", opt);
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        for (int x = 0; x < MAX_THREAD; x ++) {
            if (data.contains("#ct" + x)) {
                this.threads[x].readNBT(data.getCompound("#ct" + x));
            }
        }
        var opt = data.getCompound("inv");
        for (int x = 0; x < this.internalInv.size(); x++) {
            var item = opt.getCompound("item" + x);
            this.internalInv.setItemDirect(x, ItemStack.of(item));
        }
    }

    public ItemStack pushResult(ItemStack stack, Direction d) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        var grid = this.getMainNode().getGrid();
        if (grid != null) {
            var storage = grid.getService(IStorageService.class);
            var added = storage.getInventory().insert(AEItemKey.of(stack), stack.getCount(), Actionable.MODULATE, this.cluster.getSrc());
            if (added == 0) {
                return stack;
            }
            this.saveChanges();
            if (added != stack.getCount()) {
                return stack.copyWithCount((int) (stack.getCount() - added));
            } else {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    @Override
    public void add(ClusterAssemblerMatrix c) {
        c.addCrafter(this);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        var isAwake = false;
        for (var t : this.threads) {
            t.recalculatePlan();
            t.updateSleepiness();
            isAwake |= t.isAwake();
        }
        if (isAwake) {
            for (var t : this.threads) {
                t.forceAwake();
            }
        }
        return new TickingRequest(1, 1, !isAwake, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (this.cluster == null) {
            return TickRateModulation.SLEEP;
        }
        var rate = TickRateModulation.SLEEP;
        for (var t : this.threads) {
            if (t.isAwake()) {
                var tr = t.tick(this.cluster.getSpeedCore(), ticksSinceLastCall);
                if (tr.ordinal() > rate.ordinal()) {
                    rate = tr;
                }
            }
        }
        this.cluster.updateCrafter(this);
        return rate;
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        for (var t : this.threads) {
            if (inv == t.getInternalInventory()) {
                t.recalculatePlan();
                break;
            }
        }
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var stack: this.internalInv) {
            var genericStack = GenericStack.unwrapItemStack(stack);
            if (genericStack != null) {
                genericStack.what().addDrops(genericStack.amount(), drops, level, pos);
            } else {
                drops.add(stack);
            }
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.internalInv.clear();
    }

}
