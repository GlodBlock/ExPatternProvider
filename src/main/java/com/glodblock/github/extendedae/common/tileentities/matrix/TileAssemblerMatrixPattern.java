package com.glodblock.github.extendedae.common.tileentities.matrix;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.blockentity.crafting.IMolecularAssemblerSupportedPattern;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.filter.AEItemFilters;
import appeng.util.inv.filter.IAEItemFilter;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TileAssemblerMatrixPattern extends TileAssemblerMatrixFunction implements InternalInventoryHost, ICraftingProvider, PatternContainer {

    public final static int INV_SIZE = 36;
    private final AppEngInternalInventory patternInventory;
    private final FilteredInternalInventory exposedInventory;
    private final List<IPatternDetails> patterns = new ArrayList<>();

    public TileAssemblerMatrixPattern(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileAssemblerMatrixPattern.class, TileAssemblerMatrixPattern::new, EAESingletons.ASSEMBLER_MATRIX_PATTERN), pos, blockState);
        this.patternInventory = new AppEngInternalInventory(this, INV_SIZE, 1);
        this.patternInventory.setFilter(new Filter(this::getLevel));
        this.exposedInventory = new FilteredInternalInventory(this.patternInventory, AEItemFilters.INSERT_ONLY);
        this.getMainNode().addService(ICraftingProvider.class, this);
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.patternInventory.writeToNBT(data, "pattern", registries);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.patternInventory.readFromNBT(data, "pattern", registries);
    }

    @Override
    public boolean isVisibleInTerminal() {
        return this.manager.getSetting(Settings.PATTERN_ACCESS_TERMINAL) == YesNo.YES;
    }

    public AppEngInternalInventory getPatternInventory() {
        return this.patternInventory;
    }

    public FilteredInternalInventory getExposedInventory() {
        return this.exposedInventory;
    }

    public long getLocateID() {
        return this.worldPosition.asLong();
    }

    public void updatePatterns() {
        this.patterns.clear();
        for (var stack : this.patternInventory) {
            var details = PatternDetailsHelper.decodePattern(stack, this.getLevel());
            if (details != null) {
                patterns.add(details);
            }
        }
        ICraftingProvider.requestUpdate(this.getMainNode());
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var pattern : this.patternInventory) {
            drops.add(pattern);
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.patternInventory.clear();
    }

    @Override
    public void add(ClusterAssemblerMatrix c) {
        c.addPattern(this);
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory inv) {
        this.saveChanges();
        this.updatePatterns();
    }

    @Override
    public void onReady() {
        super.onReady();
        this.updatePatterns();
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        return this.patterns;
    }

    @Override
    public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
        if (!isFormed() || !this.getMainNode().isActive() || !this.patterns.contains(patternDetails)) {
            return false;
        }
        return this.cluster.pushCraftingJob(patternDetails, inputHolder);
    }

    @Override
    public boolean isBusy() {
        return this.cluster == null || this.cluster.isBusy();
    }

    @Override
    public @Nullable IGrid getGrid() {
        return this.getMainNode().getGrid();
    }

    @Override
    public InternalInventory getTerminalPatternInventory() {
        return this.patternInventory;
    }

    @Override
    public long getTerminalSortOrder() {
        return this.getLocateID();
    }

    @Override
    public PatternContainerGroup getTerminalGroup() {
        var icon = AEItemKey.of(EAESingletons.ASSEMBLER_MATRIX_PATTERN);
        return new PatternContainerGroup(icon, icon.getDisplayName(), List.of(Component.translatable("gui.extendedae.assembler_matrix.pattern")));
    }

    public record Filter(Supplier<Level> world) implements IAEItemFilter {

        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return PatternDetailsHelper.decodePattern(stack, world.get()) instanceof IMolecularAssemblerSupportedPattern;
        }

    }

}
