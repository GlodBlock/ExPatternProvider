package com.glodblock.github.extendedae.container;

import appeng.api.config.FullnessMode;
import appeng.api.config.OperationMode;
import appeng.api.config.Settings;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.util.IConfigManager;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.OutputSlot;
import appeng.menu.slot.RestrictedInputSlot;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.tileentities.TileExIOPort;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerExIOPort extends UpgradeableMenu<TileExIOPort> {

    public static final MenuType<ContainerExIOPort> TYPE = MenuTypeBuilder
            .create(ContainerExIOPort::new, TileExIOPort.class)
            .buildUnregistered(ExtendedAE.id("ex_ioport"));

    @GuiSync(2)
    public FullnessMode fMode = FullnessMode.EMPTY;
    @GuiSync(3)
    public OperationMode opMode = OperationMode.EMPTY;

    public ContainerExIOPort(int id, Inventory ip, TileExIOPort host) {
        super(TYPE, id, ip, host);
    }

    @Override
    protected void setupConfig() {
        var cells = this.getHost().getSubInventory(ISegmentedInventory.CELLS);

        for (int i = 0; i < 6; i++) {
            this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.STORAGE_CELLS, cells, i),
                    SlotSemantics.MACHINE_INPUT);
        }

        for (int i = 0; i < 6; i++) {
            this.addSlot(new OutputSlot(cells, 6 + i,
                    RestrictedInputSlot.PlacableItemType.STORAGE_CELLS.icon), SlotSemantics.MACHINE_OUTPUT);
        }
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.setOperationMode(cm.getSetting(Settings.OPERATION_MODE));
        this.setFullMode(cm.getSetting(Settings.FULLNESS_MODE));
        this.setRedStoneMode(cm.getSetting(Settings.REDSTONE_CONTROLLED));
    }

    public FullnessMode getFullMode() {
        return this.fMode;
    }

    private void setFullMode(FullnessMode fMode) {
        this.fMode = fMode;
    }

    public OperationMode getOperationMode() {
        return this.opMode;
    }

    private void setOperationMode(OperationMode opMode) {
        this.opMode = opMode;
    }
}
