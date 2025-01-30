package com.glodblock.github.extendedae.container;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.OutputSlot;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.tileentities.TileCircuitCutter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerCircuitCutter extends UpgradeableMenu<TileCircuitCutter> implements IProgressProvider {

    @GuiSync(3)
    public int processingTime = -1;

    @GuiSync(8)
    public YesNo autoExport = YesNo.NO;

    public static final MenuType<ContainerCircuitCutter> TYPE = MenuTypeBuilder
            .create(ContainerCircuitCutter::new, TileCircuitCutter.class)
            .buildUnregistered(ExtendedAE.id("circuit_cutter"));

    public ContainerCircuitCutter(int id, Inventory ip, TileCircuitCutter host) {
        super(TYPE, id, ip, host);
        this.addSlot(new AppEngSlot(host.getInput(), 0), SlotSemantics.MACHINE_INPUT);
        this.addSlot(new OutputSlot(host.getOutput(), 0, null), SlotSemantics.MACHINE_OUTPUT);
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.autoExport = getHost().getConfigManager().getSetting(Settings.AUTO_EXPORT);
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
