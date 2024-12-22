package com.glodblock.github.extendedae.common.tileentities;

import appeng.api.config.Setting;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.core.settings.TickRates;
import appeng.util.ConfigManager;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.AEItemFilters;
import com.glodblock.github.extendedae.api.IRecipeMachine;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipe;
import com.glodblock.github.extendedae.util.FCUtil;
import com.glodblock.github.extendedae.util.RecipeExecutor;
import com.glodblock.github.glodium.recipe.CommonRecipeContext;
import com.glodblock.github.glodium.recipe.RecipeSearchContext;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class TileCircuitCutter extends AENetworkedPoweredBlockEntity implements IGridTickable, IUpgradeableObject, IConfigurableObject, IRecipeMachine<RecipeInput, CircuitCutterRecipe> {

    public static final int POWER_MAXIMUM_AMOUNT = 8000;
    public static final int MAX_PROGRESS = 200;
    private final AppEngInternalInventory input = new AppEngInternalInventory(this, 1, 64);
    private final AppEngInternalInventory output = new AppEngInternalInventory(this, 1, 64);
    private final CombinedInternalInventory inv = new CombinedInternalInventory(input, output);
    private final FilteredInternalInventory outputExposed = new FilteredInternalInventory(output, AEItemFilters.EXTRACT_ONLY);
    private final FilteredInternalInventory inputExposed = new FilteredInternalInventory(input, AEItemFilters.INSERT_ONLY);
    private final CombinedInternalInventory invExposed = new CombinedInternalInventory(inputExposed, outputExposed);
    private final CommonRecipeContext<CircuitCutterRecipe> ctx = new CutterRecipeContext(this);
    private final RecipeExecutor<CircuitCutterRecipe> exec;
    private final IUpgradeInventory upgrades;
    private final ConfigManager configManager;
    private boolean isWorking = false;
    private int progress = 0;
    private ItemStack renderOutput = ItemStack.EMPTY;

    public TileCircuitCutter(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileCircuitCutter.class, TileCircuitCutter::new, EAESingletons.CIRCUIT_CUTTER), pos, blockState);
        this.getMainNode()
                .setFlags()
                .setIdlePowerUsage(0)
                .addService(IGridTickable.class, this);
        this.setInternalMaxPower(POWER_MAXIMUM_AMOUNT);
        this.setPowerSides(getGridConnectableSides(getOrientation()));
        this.upgrades = UpgradeInventories.forMachine(EAESingletons.CIRCUIT_CUTTER, 4, this::saveChanges);
        this.configManager = new ConfigManager(this::onConfigChanged);
        this.configManager.registerSetting(Settings.AUTO_EXPORT, YesNo.NO);
        this.exec = new RecipeExecutor<>(this, r -> r.output, MAX_PROGRESS);
    }

    @Override
    protected InternalInventory getExposedInventoryForSide(Direction facing) {
        return this.invExposed;
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    public boolean isWorking() {
        return this.isWorking;
    }

    @Override
    public int getProgress() {
        return this.progress;
    }

    @Override
    public void addProgress(int delta) {
        this.progress += delta;
    }

    @Override
    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public RecipeSearchContext<RecipeInput, CircuitCutterRecipe> getContext() {
        return this.ctx;
    }

    @Override
    public void setWorking(boolean work) {
        boolean oldVal = this.isWorking;
        this.isWorking = work;
        if (oldVal != work) {
            this.markForUpdate();
        }
    }

    public AppEngInternalInventory getInput() {
        return this.input;
    }

    @Override
    public AppEngInternalInventory getOutput() {
        return this.output;
    }

    public ItemStack getRenderOutput() {
        return this.renderOutput;
    }

    @Override
    public @Nullable IManagedGridNode getNode() {
        return this.getMainNode();
    }

    @Override
    public @Nullable IEnergySource getEnergy() {
        return this;
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return EnumSet.complementOf(EnumSet.of(orientation.getSide(RelativeSide.FRONT), orientation.getSide(RelativeSide.BACK)));
    }

    private void onConfigChanged(IConfigManager manager, Setting<?> setting) {
        if (setting == Settings.AUTO_EXPORT) {
            getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }
    }

    @Override
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        var changed = super.readFromStream(data);
        boolean newIsWorking = data.readBoolean();
        if (this.isWorking != newIsWorking) {
            this.isWorking = newIsWorking;
            changed = true;
        }
        int newProgress = data.readInt();
        if (this.progress != newProgress) {
            this.progress = newProgress;
            changed = true;
        }
        var inputStack = ItemStack.OPTIONAL_STREAM_CODEC.decode(data);
        if (!ItemStack.isSameItem(inputStack, this.input.getStackInSlot(0))) {
            this.input.setItemDirect(0, inputStack);
            changed = true;
        }
        var outputStack = ItemStack.OPTIONAL_STREAM_CODEC.decode(data);
        if (!ItemStack.isSameItem(outputStack, this.renderOutput)) {
            this.renderOutput = outputStack;
            changed = true;
        }
        return changed;
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeBoolean(this.isWorking);
        data.writeInt(this.progress);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(data, this.input.getStackInSlot(0));
        this.renderOutput = this.ctx.currentRecipe == null ? ItemStack.EMPTY : this.ctx.currentRecipe.value().output;
        ItemStack.OPTIONAL_STREAM_CODEC.encode(data, this.renderOutput);
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var upgrade : upgrades) {
            drops.add(upgrade);
        }
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.upgrades.writeToNBT(data, "upgrades", registries);
        this.configManager.writeToNBT(data, registries);
        this.ctx.save(data);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.upgrades.clear();
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data,registries);
        this.upgrades.readFromNBT(data, "upgrades", registries);
        this.configManager.readFromNBT(data, registries);
        this.ctx.load(data);
    }

    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

    private boolean hasAutoExportWork() {
        return !this.output.getStackInSlot(0).isEmpty() && configManager.getSetting(Settings.AUTO_EXPORT) == YesNo.YES;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(TickRates.Inscriber, !this.ctx.shouldTick() && !this.hasAutoExportWork());
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (this.pushOutResult()) {
            return TickRateModulation.URGENT;
        }
        this.fastUpdate();
        return this.exec.execute(FCUtil.speedCardMap(this.upgrades.getInstalledUpgrades(AEItems.SPEED_CARD)), true);
    }

    private void fastUpdate() {
        if (this.level != null && !this.isRemoved() && !notLoaded()) {
            BlockState currentState = getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, currentState, currentState, Block.UPDATE_NEIGHBORS);
        }
    }

    private boolean pushOutResult() {
        if (!this.hasAutoExportWork()) {
            return false;
        }
        return FCUtil.ejectInv(this.level, this.getBlockPos(), this.output, te -> te instanceof TileCircuitCutter);
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.inv;
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        this.saveChanges();
        this.ctx.onInvChange();
    }

    private static class CutterRecipeContext extends CommonRecipeContext<CircuitCutterRecipe> {

        private final TileCircuitCutter host;

        protected CutterRecipeContext(TileCircuitCutter host) {
            super(() -> host.level, CircuitCutterRecipe.TYPE);
            this.host = host;
        }

        @Override
        public void onInvChange() {
            super.onInvChange();
            this.host.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }

        @Override
        public void onFind(@Nullable RecipeHolder<CircuitCutterRecipe> recipe) {
            super.onFind(recipe);
            this.host.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }

        @Override
        public boolean testRecipe(RecipeHolder<CircuitCutterRecipe> recipe) {
            var output = recipe.value().output.copy();
            if (!this.host.output.insertItem(0, output, true).isEmpty()) {
                return false;
            }
            var sample = recipe.value().getSample();
            var copyInv = this.copyHostInv();
            for (var tester : sample) {
                for (var stack : copyInv) {
                    if (tester.checkType(stack)) {
                        tester.consume(stack);
                    }
                    if (tester.isEmpty()) {
                        break;
                    }
                }
                if (!tester.isEmpty()) {
                    return false;
                }
                copyInv = copyInv.stream().filter(o -> !o.isEmpty()).toList();
            }
            return true;
        }

        @Override
        public void runRecipe(RecipeHolder<CircuitCutterRecipe> recipe) {
            var sample = recipe.value().getSample();
            for (var tester : sample) {
                for (int x = 0; x < this.host.input.size(); x ++) {
                    var item = this.host.input.getStackInSlot(x);
                    if (tester.checkType(item)) {
                        tester.consume(item);
                        this.host.input.setItemDirect(x, item);
                    }
                    if (tester.isEmpty()) {
                        break;
                    }
                }
            }
        }

        public List<ItemStack> copyHostInv() {
            List<ItemStack> inv = new ArrayList<>();
            for (var item : this.host.input) {
                if (!item.isEmpty()) {
                    inv.add(item.copy());
                }
            }
            return inv;
        }

    }

}
