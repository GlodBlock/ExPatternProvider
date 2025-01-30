package com.glodblock.github.extendedae.container;

import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.me.wireless.WirelessStatus;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessConnector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerWirelessConnector extends UpgradeableMenu<TileWirelessConnector> {

    public static final MenuType<ContainerWirelessConnector> TYPE = MenuTypeBuilder
            .create(ContainerWirelessConnector::new, TileWirelessConnector.class)
            .buildUnregistered(ExtendedAE.id("wireless_connector"));

    private final TileWirelessConnector connector;
    @GuiSync(7)
    public double powerUse;
    @GuiSync(8)
    public int usedChannel;
    @GuiSync(9)
    public int maxChannel;
    @GuiSync(10)
    public long otherSide;
    @GuiSync(11)
    public WirelessStatus status = WirelessStatus.REMOTE_ERROR;

    public ContainerWirelessConnector(int id, Inventory playerInventory, TileWirelessConnector host) {
        super(TYPE, id, playerInventory, host);
        this.connector = host;
    }

    @Override
    public void broadcastChanges() {
        this.powerUse = this.connector.getPowerUse();
        var node = this.connector.getMainNode().getNode();
        if (node != null) {
            this.usedChannel = node.getUsedChannels();
            this.maxChannel = node.getMaxChannels();
        } else {
            this.usedChannel = 0;
            this.maxChannel = 0;
        }
        var otherSide = this.connector.getOtherSide();
        if (otherSide == null) {
            this.otherSide = 0;
            this.status = this.connector.getFrequency() == 0 ? WirelessStatus.UNCONNECTED : WirelessStatus.REMOTE_ERROR;
        } else {
            this.otherSide = otherSide.asLong();
            this.status = WirelessStatus.WORKING;
        }
        if (!this.connector.getMainNode().isPowered() && this.status == WirelessStatus.WORKING) {
            this.status = WirelessStatus.NO_POWER;
        }
        super.broadcastChanges();
    }

}
