package com.glodblock.github.extendedae.common.parts;

import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.networking.IGrid;
import appeng.api.networking.IStackWatcher;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingWatcherNode;
import appeng.api.networking.storage.IStorageWatcherNode;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AEKey;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.helpers.IConfigInvHost;
import appeng.hooks.ticking.TickHandler;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.PartModel;
import appeng.parts.automation.AbstractLevelEmitterPart;
import appeng.util.ConfigInventory;
import appeng.util.SettingsFrom;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.container.ContainerThresholdLevelEmitter;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class PartThresholdLevelEmitter extends AbstractLevelEmitterPart implements IConfigInvHost {

    public static List<ResourceLocation> MODELS = Arrays.asList(
            new ResourceLocation(ExtendedAE.MODID, "part/threshold_level_emitter_base_off"),
            new ResourceLocation(ExtendedAE.MODID, "part/threshold_level_emitter_base_on"),
            new ResourceLocation(AppEng.MOD_ID, "part/level_emitter_status_off"),
            new ResourceLocation(AppEng.MOD_ID, "part/level_emitter_status_on"),
            new ResourceLocation(AppEng.MOD_ID, "part/level_emitter_status_has_channel")
    );
    public static final DustParticleOptions BLUE = new DustParticleOptions(Vec3.fromRGB24(0x0000ff).toVector3f(), 1.0F);

    public static final PartModel MODEL_OFF_OFF = new PartModel(MODELS.get(0), MODELS.get(2));
    public static final PartModel MODEL_OFF_ON = new PartModel(MODELS.get(0), MODELS.get(3));
    public static final PartModel MODEL_OFF_HAS_CHANNEL = new PartModel(MODELS.get(0), MODELS.get(4));
    public static final PartModel MODEL_ON_OFF = new PartModel(MODELS.get(1), MODELS.get(2));
    public static final PartModel MODEL_ON_ON = new PartModel(MODELS.get(1), MODELS.get(3));
    public static final PartModel MODEL_ON_HAS_CHANNEL = new PartModel(MODELS.get(1), MODELS.get(4));

    private final ConfigInventory config = ConfigInventory.configTypes(1, this::configureWatchers);
    private IStackWatcher storageWatcher;
    private IStackWatcher craftingWatcher;
    private long lastUpdateTick = -1;
    private long upperValue;
    private long lowerValue;
    private boolean currentState = false;

    public PartThresholdLevelEmitter(IPartItem<?> partItem) {
        super(partItem);
        getMainNode().addService(IStorageWatcherNode.class, new IStorageWatcherNode() {
            @Override
            public void updateWatcher(IStackWatcher newWatcher) {
                storageWatcher = newWatcher;
                configureWatchers();
            }

            @Override
            public void onStackChange(AEKey what, long amount) {
                if (what.equals(getConfiguredKey()) && !isUpgradedWith(AEItems.FUZZY_CARD)) {
                    lastReportedValue = amount;
                    updateState();
                } else {
                    long currentTick = TickHandler.instance().getCurrentTick();
                    if (currentTick != lastUpdateTick) {
                        lastUpdateTick = currentTick;
                        updateReportingValue(getGridNode().getGrid());
                    }
                }
            }
        });
        getMainNode().addService(ICraftingWatcherNode.class, new ICraftingWatcherNode() {
            @Override
            public void updateWatcher(IStackWatcher newWatcher) {
                craftingWatcher = newWatcher;
                configureWatchers();
            }

            @Override
            public void onRequestChange(AEKey what) {
                updateState();
            }

            @Override
            public void onCraftableChange(AEKey what) {
            }
        });
        getConfigManager().registerSetting(Settings.CRAFT_VIA_REDSTONE, YesNo.NO);
        getConfigManager().registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
    }

    @Nullable
    private AEKey getConfiguredKey() {
        return config.getKey(0);
    }

    @Override
    protected final int getUpgradeSlots() {
        return 1;
    }

    @Override
    protected boolean hasDirectOutput() {
        return false;
    }

    @Override
    protected boolean getDirectOutput() {
        return false;
    }

    @Override
    protected void onReportingValueChanged() {
        getMainNode().ifPresent(this::updateReportingValue);
    }

    @Override
    protected void configureWatchers() {
        var myStack = getConfiguredKey();

        if (this.storageWatcher != null) {
            this.storageWatcher.reset();
        }

        if (this.craftingWatcher != null) {
            this.craftingWatcher.reset();
        }

        ICraftingProvider.requestUpdate(getMainNode());

        if (isUpgradedWith(AEItems.CRAFTING_CARD)) {
            if (this.craftingWatcher != null) {
                if (myStack == null) {
                    this.craftingWatcher.setWatchAll(true);
                } else {
                    this.craftingWatcher.add(myStack);
                }
            }
        } else {
            if (this.storageWatcher != null) {
                if (isUpgradedWith(AEItems.FUZZY_CARD) || myStack == null) {
                    this.storageWatcher.setWatchAll(true);
                } else {
                    this.storageWatcher.add(myStack);
                }
            }
            getMainNode().ifPresent(this::updateReportingValue);
        }

        updateState();
    }

    public boolean checkSanity() {
        return this.lowerValue <= this.upperValue;
    }

    public boolean checkState(long value, boolean invert) {
        boolean current = invert != this.currentState;
        if (current) {
            return value >= this.lowerValue;
        } else {
            return value >= this.upperValue;
        }
    }

    @Override
    protected boolean isLevelEmitterOn() {
        if (isClientSide()) {
            return super.isLevelEmitterOn();
        }

        if (!this.getMainNode().isActive()) {
            return false;
        }

        if (!checkSanity()) {
            return false;
        }

        final boolean flipState = this.getConfigManager().getSetting(Settings.REDSTONE_EMITTER) == RedstoneMode.LOW_SIGNAL;
        final boolean active = this.checkState(this.lastReportedValue, flipState);
        boolean newState = flipState != active;
        if (newState != this.currentState) {
            this.currentState = newState;
            this.getHost().markForSave();
        }
        return this.currentState;
    }

    private void updateReportingValue(IGrid grid) {
        var stacks = grid.getStorageService().getCachedInventory();
        var myStack = getConfiguredKey();

        if (myStack == null) {
            this.lastReportedValue = 0;
            for (var st : stacks) {
                this.lastReportedValue += st.getLongValue();
                if (this.lastReportedValue > this.getUpperValue()) {
                    // Stop here, we have enough info! This prevents blank emitter spam from causing lots of lag.
                    break;
                }
            }
        } else if (isUpgradedWith(AEItems.FUZZY_CARD)) {
            this.lastReportedValue = 0;
            var fzMode = this.getConfigManager().getSetting(Settings.FUZZY_MODE);
            var fuzzyList = stacks.findFuzzy(myStack, fzMode);
            for (var st : fuzzyList) {
                this.lastReportedValue += st.getLongValue();
                if (this.lastReportedValue > this.getUpperValue()) {
                    // Stop here, we have enough info!
                    break;
                }
            }
        } else {
            this.lastReportedValue = stacks.get(myStack);
        }

        this.updateState();
    }

    @Override
    public boolean onPartActivate(Player player, InteractionHand hand, Vec3 pos) {
        if (!isClientSide()) {
            MenuOpener.open(ContainerThresholdLevelEmitter.TYPE, player, MenuLocators.forPart(this));
        }
        return true;
    }

    public ConfigInventory getConfig() {
        return config;
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return this.isLevelEmitterOn() ? MODEL_ON_HAS_CHANNEL : MODEL_OFF_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return this.isLevelEmitterOn() ? MODEL_ON_ON : MODEL_OFF_ON;
        } else {
            return this.isLevelEmitterOn() ? MODEL_ON_OFF : MODEL_OFF_OFF;
        }
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.upperValue = data.getLong("upperValue");
        this.lowerValue = data.getLong("lowerValue");
        this.currentState = data.getBoolean("currentState");
        this.config.readFromChildTag(data, "config");
    }

    @Override
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);
        data.putLong("upperValue", this.upperValue);
        data.putLong("lowerValue", this.lowerValue);
        data.putBoolean("currentState", this.currentState);
        this.config.writeToChildTag(data, "config");
    }

    public void setUpperValue(long value) {
        this.upperValue = value;
        this.onReportingValueChanged();
        this.updateState();
    }

    public void setLowerValue(long value) {
        this.lowerValue = value;
        this.onReportingValueChanged();
        this.updateState();
    }

    public long getUpperValue() {
        return this.upperValue;
    }

    public long getLowerValue() {
        return this.lowerValue;
    }

    @Override
    public void importSettings(SettingsFrom mode, CompoundTag input, @Nullable Player player) {
        super.importSettings(mode, input, player);
        this.setUpperValue(input.getLong("upperValue"));
        this.setLowerValue(input.getLong("lowerValue"));
    }

    @Override
    public void exportSettings(SettingsFrom mode, CompoundTag output) {
        super.exportSettings(mode, output);
        if (mode == SettingsFrom.MEMORY_CARD) {
            output.putLong("upperValue", this.upperValue);
            output.putLong("lowerValue", this.lowerValue);
        }
    }

}
