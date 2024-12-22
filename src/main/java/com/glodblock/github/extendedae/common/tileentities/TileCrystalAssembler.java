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
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.core.settings.TickRates;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.util.ConfigManager;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.AEItemFilters;
import com.glodblock.github.extendedae.api.IRecipeMachine;
import com.glodblock.github.extendedae.api.caps.IGenericInvHost;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipe;
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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class TileCrystalAssembler extends AENetworkedPoweredBlockEntity implements IGridTickable, IUpgradeableObject, IConfigurableObject, IGenericInvHost, IRecipeMachine<RecipeInput, CrystalAssemblerRecipe> {

    public static final int SLOTS = 9;
    public static final int TANK_CAP = 16000;
    public static final int POWER_MAXIMUM_AMOUNT = 8000;
    public static final int MAX_PROGRESS = 200;
    private final AppEngInternalInventory input = new AppEngInternalInventory(this, SLOTS, 64);
    private final AppEngInternalInventory output = new AppEngInternalInventory(this, 1, 64);
    private final CombinedInternalInventory inv = new CombinedInternalInventory(input, output);
    private final FilteredInternalInventory outputExposed = new FilteredInternalInventory(output, AEItemFilters.EXTRACT_ONLY);
    private final FilteredInternalInventory inputExposed = new FilteredInternalInventory(input, AEItemFilters.INSERT_ONLY);
    private final CombinedInternalInventory invExposed = new CombinedInternalInventory(inputExposed, outputExposed);
    private final IUpgradeInventory upgrades;
    private final ConfigManager configManager;
    private final GenericStackInv tank = new GenericStackInv(Set.of(AEKeyType.fluids()), this::onChangeTank, GenericStackInv.Mode.STORAGE, 1);
    private final CommonRecipeContext<CrystalAssemblerRecipe> ctx = new CrystalRecipeContext(this);
    private final RecipeExecutor<CrystalAssemblerRecipe> exec;
    private boolean isWorking = false;
    private int progress = 0;

    public TileCrystalAssembler(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileCrystalAssembler.class, TileCrystalAssembler::new, EAESingletons.CRYSTAL_ASSEMBLER), pos, blockState);
        this.getMainNode()
                .setFlags()
                .setIdlePowerUsage(0)
                .addService(IGridTickable.class, this);
        this.setInternalMaxPower(POWER_MAXIMUM_AMOUNT);
        this.setPowerSides(getGridConnectableSides(getOrientation()));
        this.upgrades = UpgradeInventories.forMachine(EAESingletons.CRYSTAL_ASSEMBLER, 4, this::saveChanges);
        this.configManager = new ConfigManager(this::onConfigChanged);
        this.configManager.registerSetting(Settings.AUTO_EXPORT, YesNo.NO);
        this.tank.setCapacity(AEKeyType.fluids(), TANK_CAP);
        this.exec = new RecipeExecutor<>(this, r -> r.output, MAX_PROGRESS);
    }

    @Override
    protected InternalInventory getExposedInventoryForSide(Direction facing) {
        return this.invExposed;
    }

    private void onConfigChanged(IConfigManager manager, Setting<?> setting) {
        if (setting == Settings.AUTO_EXPORT) {
            getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }
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
    public RecipeSearchContext<RecipeInput, CrystalAssemblerRecipe> getContext() {
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

    public GenericStackInv getTank() {
        return this.tank;
    }

    @Override
    public AppEngInternalInventory getOutput() {
        return this.output;
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return EnumSet.complementOf(EnumSet.of(orientation.getSide(RelativeSide.TOP)));
    }

    @Override
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        var changed = super.readFromStream(data);
        boolean newWork = data.readBoolean();
        if (newWork != this.isWorking) {
            this.isWorking = newWork;
            changed = true;
        }
        return changed;
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeBoolean(this.isWorking);
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
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.tank.writeToChildTag(data, "tank_in", registries);
        this.upgrades.writeToNBT(data, "upgrades", registries);
        this.configManager.writeToNBT(data, registries);
        this.ctx.save(data);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.tank.clear();
        this.upgrades.clear();
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.tank.readFromChildTag(data, "tank_in", registries);
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
    protected void onOrientationChanged(BlockOrientation orientation) {
        super.onOrientationChanged(orientation);
        this.setPowerSides(getGridConnectableSides(orientation));
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return upgrades;
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
        return this.exec.execute(FCUtil.speedCardMap(this.getUpgrades().getInstalledUpgrades(AEItems.SPEED_CARD)), true);
    }

    private boolean pushOutResult() {
        if (!this.hasAutoExportWork()) {
            return false;
        }
        return FCUtil.ejectInv(this.level, this.getBlockPos(), this.output, te -> te instanceof TileCrystalAssembler);
    }

    @Override
    public IManagedGridNode getNode() {
        return this.getMainNode();
    }

    @Override
    public IEnergySource getEnergy() {
        return this;
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

    public void onChangeTank() {
        this.saveChanges();
        this.ctx.onInvChange();
    }

    @Override
    public GenericStackInv getGenericInv() {
        return this.tank;
    }

    private static class CrystalRecipeContext extends CommonRecipeContext<CrystalAssemblerRecipe> {

        private final TileCrystalAssembler host;

        protected CrystalRecipeContext(TileCrystalAssembler host) {
            super(() -> host.level, CrystalAssemblerRecipe.TYPE);
            this.host = host;
        }

        @Override
        public void onInvChange() {
            super.onInvChange();
            this.host.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }

        @Override
        public void onFind(@Nullable RecipeHolder<CrystalAssemblerRecipe> recipe) {
            super.onFind(recipe);
            this.host.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }

        @Override
        public boolean testRecipe(RecipeHolder<CrystalAssemblerRecipe> recipe) {
            var output = recipe.value().output;
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
        public void runRecipe(RecipeHolder<CrystalAssemblerRecipe> recipe) {
            var sample = recipe.value().getSample();
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
