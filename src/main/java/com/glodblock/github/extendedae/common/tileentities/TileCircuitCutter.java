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
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkPowerBlockEntity;
import appeng.capabilities.Capabilities;
import appeng.core.definitions.AEItems;
import appeng.core.settings.TickRates;
import appeng.helpers.externalstorage.GenericStackFluidStorage;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.util.ConfigManager;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.AEItemFilters;
import com.glodblock.github.extendedae.api.IRecipeMachine;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.common.inventory.FluidInventory;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipe;
import com.glodblock.github.extendedae.util.FCUtil;
import com.glodblock.github.extendedae.util.recipe.ContainerRecipeContext;
import com.glodblock.github.extendedae.util.recipe.RecipeExecutor;
import com.glodblock.github.extendedae.util.recipe.RecipeSearchContext;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class TileCircuitCutter extends AENetworkPowerBlockEntity implements IGridTickable, IUpgradeableObject, IConfigurableObject, IRecipeMachine<Container, CircuitCutterRecipe> {

    public static final int POWER_MAXIMUM_AMOUNT = 8000;
    public static final int MAX_PROGRESS = 200;
    public static final int TANK_CAP = 16000;
    private final AppEngInternalInventory input = new AppEngInternalInventory(this, 1, 64);
    private final AppEngInternalInventory output = new AppEngInternalInventory(this, 1, 64);
    private final CombinedInternalInventory inv = new CombinedInternalInventory(input, output);
    private final GenericStackInv tank = new FluidInventory(this::onChangeTank, GenericStackInv.Mode.STORAGE, 1, TANK_CAP);
    private final FilteredInternalInventory outputExposed = new FilteredInternalInventory(output, AEItemFilters.EXTRACT_ONLY);
    private final FilteredInternalInventory inputExposed = new FilteredInternalInventory(input, AEItemFilters.INSERT_ONLY);
    private final CombinedInternalInventory invExposed = new CombinedInternalInventory(inputExposed, outputExposed);
    private final ContainerRecipeContext<CircuitCutterRecipe> ctx = new CutterRecipeContext(this);
    private final RecipeExecutor<CircuitCutterRecipe> exec;
    private final IUpgradeInventory upgrades;
    private final ConfigManager configManager;
    private boolean isWorking = false;
    private int progress = 0;
    private ItemStack renderOutput = ItemStack.EMPTY;

    public TileCircuitCutter(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileCircuitCutter.class, TileCircuitCutter::new, EPPItemAndBlock.CIRCUIT_CUTTER), pos, blockState);
        this.getMainNode()
                .setFlags()
                .setIdlePowerUsage(0)
                .addService(IGridTickable.class, this);
        this.setInternalMaxPower(POWER_MAXIMUM_AMOUNT);
        this.setPowerSides(getGridConnectableSides(getOrientation()));
        this.upgrades = UpgradeInventories.forMachine(EPPItemAndBlock.CIRCUIT_CUTTER, 4, this::saveChanges);
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
    public RecipeSearchContext<Container, CircuitCutterRecipe> getContext() {
        return this.ctx;
    }

    @Override
    public void setWorking(boolean work) {
        this.isWorking = work;
    }

    public AppEngInternalInventory getInput() {
        return this.input;
    }

    public GenericStackInv getTank() {
        return this.tank;
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

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
        if (capability == Capabilities.GENERIC_INTERNAL_INV) {
            return LazyOptional.of(this::getTank).cast();
        } else if (capability == ForgeCapabilities.FLUID_HANDLER) {
            return LazyOptional.of(this::getTank).lazyMap(GenericStackFluidStorage::new).cast();
        }
        return super.getCapability(capability, facing);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(Capability<T> capability) {
        if (capability == Capabilities.GENERIC_INTERNAL_INV) {
            return LazyOptional.of(() -> this.tank).cast();
        } else if (capability == ForgeCapabilities.FLUID_HANDLER) {
            return LazyOptional.of(this::getTank).lazyMap(GenericStackFluidStorage::new).cast();
        }
        return super.getCapability(capability);
    }

    @Override
    protected boolean readFromStream(FriendlyByteBuf data) {
        super.readFromStream(data);
        this.isWorking = data.readBoolean();
        this.progress = data.readInt();
        this.input.setItemDirect(0, data.readItem());
        this.renderOutput = data.readItem();
        return true;
    }

    @Override
    protected void writeToStream(FriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeBoolean(this.isWorking);
        data.writeInt(this.progress);
        data.writeItem(this.input.getStackInSlot(0));
        this.renderOutput = this.ctx.currentRecipe == null ? ItemStack.EMPTY : this.ctx.currentRecipe.output;
        data.writeItem(this.renderOutput);
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var upgrade : upgrades) {
            drops.add(upgrade);
        }
        var fluid = this.tank.getStack(0);
        if (fluid != null) {
            fluid.what().addDrops(fluid.amount(), drops, level, pos);
        }
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        this.upgrades.writeToNBT(data, "upgrades");
        this.tank.writeToChildTag(data, "tank_in");
        this.configManager.writeToNBT(data);
        this.ctx.save(data);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.tank.clear();
        this.upgrades.clear();
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        this.upgrades.readFromNBT(data, "upgrades");
        this.tank.readFromChildTag(data, "tank_in");
        this.configManager.readFromNBT(data);
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
        return new TickingRequest(TickRates.Inscriber, !this.ctx.shouldTick() && !this.hasAutoExportWork(), true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (this.pushOutResult()) {
            return TickRateModulation.URGENT;
        }
        this.markForUpdate();
        return this.exec.execute(FCUtil.speedCardMap(this.getUpgrades().getInstalledUpgrades(AEItems.SPEED_CARD)), true);
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
    public void onChangeInventory(InternalInventory inv, int slot) {
        this.saveChanges();
        this.ctx.onInvChange();
    }

    public void onChangeTank() {
        this.saveChanges();
        this.ctx.onInvChange();
    }

    private static class CutterRecipeContext extends ContainerRecipeContext<CircuitCutterRecipe> {

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
        public void onFind(@Nullable CircuitCutterRecipe recipe) {
            super.onFind(recipe);
            this.host.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }

        @Override
        public boolean testRecipe(CircuitCutterRecipe recipe) {
            var output = recipe.output.copy();
            if (!this.host.output.insertItem(0, output, true).isEmpty()) {
                return false;
            }
            var sample = recipe.getSample();
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
                copyInv = copyInv.stream().filter(o -> {
                    if (o instanceof ItemStack s) {
                        return !s.isEmpty();
                    }
                    if (o instanceof FluidStack f) {
                        return !f.isEmpty();
                    }
                    return false;
                }).toList();
            }
            return true;
        }

        @Override
        public void runRecipe(CircuitCutterRecipe recipe) {
            var sample = recipe.getSample();
            var fluid = this.host.tank.getStack(0);
            FluidStack fluidStack = null;
            if (fluid != null && fluid.what() instanceof AEFluidKey key) {
                fluidStack = key.toStack((int) fluid.amount());
            }
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
                if (fluidStack != null && !tester.isEmpty() && tester.checkType(fluidStack)) {
                    tester.consume(fluidStack);
                }
            }
            if (fluidStack != null) {
                if (fluidStack.isEmpty()) {
                    this.host.tank.setStack(0, null);
                } else {
                    this.host.tank.setStack(0, new GenericStack(AEFluidKey.of(fluidStack), fluidStack.getAmount()));
                }
            }
        }

        public List<Object> copyHostInv() {
            List<Object> inv = new ArrayList<>();
            for (var item : this.host.input) {
                if (!item.isEmpty()) {
                    inv.add(item.copy());
                }
            }
            var fluid = this.host.tank.getStack(0);
            if (fluid != null && fluid.what() instanceof AEFluidKey key) {
                inv.add(key.toStack((int) fluid.amount()));
            }
            return inv;
        }

    }

}