package com.glodblock.github.extendedae.container;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.core.localization.Tooltips;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.OutputSlot;
import appeng.util.ConfigMenuInventory;
import com.glodblock.github.extendedae.common.tileentities.TileCircuitCutter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class ContainerCircuitCutter extends UpgradeableMenu<TileCircuitCutter> implements IProgressProvider {

    @GuiSync(3)
    public int processingTime = -1;

    @GuiSync(8)
    public YesNo autoExport = YesNo.NO;
    private AppEngSlot tank;

    public static final MenuType<ContainerCircuitCutter> TYPE = MenuTypeBuilder
            .create(ContainerCircuitCutter::new, TileCircuitCutter.class)
            .build("circuit_cutter");

    public ContainerCircuitCutter(int id, Inventory ip, TileCircuitCutter host) {
        super(TYPE, id, ip, host);
    }

    @Override
    protected void setupConfig() {
        this.addSlot(new AppEngSlot(this.getHost().getInput(), 0), SlotSemantics.MACHINE_INPUT);
        this.addSlot(tank = new AppEngSlot(new ConfigMenuInventory(this.getHost().getTank()), 0), SlotSemantics.STORAGE);
        this.addSlot(new OutputSlot(this.getHost().getOutput(), 0, null), SlotSemantics.MACHINE_OUTPUT);
        this.tank.setEmptyTooltip(() -> List.of(
                Component.translatable("gui.expatternprovider.tank_empty"),
                Component.translatable("gui.expatternprovider.tank_amount", 0, TileCircuitCutter.TANK_CAP).withStyle(Tooltips.NORMAL_TOOLTIP_TEXT)
        ));
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.autoExport = getHost().getConfigManager().getSetting(Settings.AUTO_EXPORT);
    }

    public boolean isTank(Slot slot) {
        return slot == this.tank;
    }

    @Override
    protected void standardDetectAndSendChanges() {
        if (isServerSide()) {
            this.processingTime = getHost().getProgress();
        }
        super.standardDetectAndSendChanges();
    }

    @Override
    public int getCurrentProgress() {
        return this.processingTime;
    }

    @Override
    public int getMaxProgress() {
        return TileCircuitCutter.MAX_PROGRESS;
    }

    public YesNo getAutoExport() {
        return autoExport;
    }
}
