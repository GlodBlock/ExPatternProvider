package com.glodblock.github.extendedae.common.parts;

import appeng.api.config.Settings;
import appeng.api.networking.IGrid;
import appeng.api.networking.storage.IStorageService;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.GenericStack;
import appeng.core.AppEngBase;
import appeng.parts.PartModel;
import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.StackWorldBehaviors;
import appeng.util.ConfigInventory;
import appeng.util.SettingsFrom;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.api.ThresholdMode;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.container.ContainerThresholdExportBus;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class PartThresholdExportBus extends ExportBusPart {

    public static List<ResourceLocation> MODELS = Arrays.asList(
            ResourceLocation.fromNamespaceAndPath(ExtendedAE.MODID, "part/threshold_export_bus_base"),
            ResourceLocation.fromNamespaceAndPath(AppEngBase.MOD_ID, "part/export_bus_on"),
            ResourceLocation.fromNamespaceAndPath(AppEngBase.MOD_ID, "part/export_bus_off"),
            ResourceLocation.fromNamespaceAndPath(AppEngBase.MOD_ID, "part/export_bus_has_channel")
    );

    public static final PartModel MODELS_OFF = new PartModel(MODELS.get(0), MODELS.get(2));
    public static final PartModel MODELS_ON = new PartModel(MODELS.get(0), MODELS.get(1));
    public static final PartModel MODELS_HAS_CHANNEL = new PartModel(MODELS.get(0), MODELS.get(3));
    private ConfigInventory config;
    private ThresholdMode mode = ThresholdMode.GREATER;

    public PartThresholdExportBus(IPartItem<?> partItem) {
        super(partItem);
    }

    public void setMode(ThresholdMode mode) {
        this.mode = mode;
    }

    public ThresholdMode getMode() {
        return this.mode;
    }

    @Override
    public void readFromNBT(CompoundTag extra, HolderLookup.Provider registries) {
        super.readFromNBT(extra, registries);
        this.config.readFromChildTag(extra, "config2", registries);
        this.mode = ThresholdMode.values()[extra.getByte("cmod")];
    }

    @Override
    public void writeToNBT(CompoundTag extra, HolderLookup.Provider registries) {
        super.writeToNBT(extra, registries);
        this.config.writeToChildTag(extra, "config2", registries);
        extra.putByte("cmod", (byte) this.mode.ordinal());
    }

    @Override
    public ConfigInventory getConfig() {
        if (this.config == null) {
            this.config = ConfigInventory.configStacks(63)
                    .supportedTypes(StackWorldBehaviors.withExportStrategy())
                    .changeListener(() -> Ae2Reflect.updatePartState(this))
                    .allowOverstacking(true)
                    .build();
        }
        return this.config;
    }

    @Override
    public void importSettings(SettingsFrom mode, DataComponentMap input, @Nullable Player player) {
        super.importSettings(mode, input, player);
        var tag = input.get(EAESingletons.EXTRA_SETTING);
        if (tag != null && tag.contains("threshold_mode")) {
            this.mode = ThresholdMode.values()[tag.getByte("threshold_mode")];
        }
    }

    @Override
    public void exportSettings(SettingsFrom mode, DataComponentMap.Builder output) {
        super.exportSettings(mode, output);
        if (mode == SettingsFrom.MEMORY_CARD) {
            var tag = new CompoundTag();
            tag.putByte("threshold_mode", (byte) this.mode.ordinal());
            output.set(EAESingletons.EXTRA_SETTING, tag);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected boolean doBusWork(IGrid grid) {
        var storageService = grid.getStorageService();
        var schedulingMode = this.getConfigManager().getSetting(Settings.SCHEDULING_MODE);

        var context = Ae2Reflect.getExportContext(this, storageService, grid.getEnergyService());

        int x;
        for (x = 0; x < this.availableSlots() && context.hasOperationsLeft(); x++) {
            final int slotToExport = this.getStartingSlot(schedulingMode, x);
            var stack = getConfig().getStack(slotToExport);
            if (stack == null || !checkAmount(stack, storageService)) {
                continue;
            }
            var what = stack.what();
            var transferFactor = what.getAmountPerOperation();
            long amount = (long) context.getOperationsRemaining() * transferFactor;
            amount = getExportStrategy().transfer(context, what, amount);
            if (amount > 0) {
                context.reduceOperationsRemaining(Math.max(1, amount / transferFactor));
            }
        }

        // Round-robin should only advance if something was actually exported
        if (context.hasDoneWork()) {
            this.updateSchedulingMode(schedulingMode, x);
        }

        return context.hasDoneWork();
    }

    private boolean checkAmount(@NotNull GenericStack stack, @NotNull IStorageService service) {
        long thr = stack.amount();
        long stored = service.getCachedInventory().get(stack.what());
        if (this.mode == ThresholdMode.GREATER) {
            return stored >= thr;
        } else if (this.mode == ThresholdMode.LOWER) {
            return stored <= thr;
        } else {
            return false;
        }
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }

    @Override
    protected MenuType<?> getMenuType() {
        return ContainerThresholdExportBus.TYPE;
    }

}