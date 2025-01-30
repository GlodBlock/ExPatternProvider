package com.glodblock.github.extendedae.container;

import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;
import appeng.util.ConfigMenuInventory;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.api.CanerMode;
import com.glodblock.github.extendedae.client.ExSemantics;
import com.glodblock.github.extendedae.common.tileentities.TileCaner;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.SEAEGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class ContainerCaner extends AEBaseMenu implements IActionHolder {

    public static final MenuType<ContainerCaner> TYPE = MenuTypeBuilder
            .create(ContainerCaner::new, TileCaner.class)
            .buildUnregistered(ExtendedAE.id("caner"));

    private final ActionMap actions = ActionMap.create();
    @GuiSync(0)
    private CanerMode mode = CanerMode.FILL;
    private final TileCaner host;

    public ContainerCaner(int id, Inventory playerInventory, TileCaner host) {
        super(TYPE, id, playerInventory, host);
        this.host = host;
        this.actions.put("set", o -> this.setMode(o.get(0)));
        this.actions.put("update", o -> {
            if (this.getPlayer() instanceof ServerPlayer sp) {
                EAENetworkHandler.INSTANCE.sendTo(new SEAEGenericPacket("init", this.mode.ordinal()), sp);
            }
        });
        this.addSlot(new AppEngSlot(new ConfigMenuInventory(host.getGenericInv(null)), 0), ExSemantics.EX_1);
        this.addSlot(new AppEngSlot(host.getContainer(), 0), ExSemantics.EX_2);
        this.createPlayerInventorySlots(playerInventory);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (this.mode != this.host.getMode()) {
            this.mode = this.host.getMode();
        }
    }

    public void setMode(int mode) {
        this.host.setMode(CanerMode.values()[mode]);
        this.broadcastChanges();
    }

    public CanerMode getMode() {
        return this.mode;
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }
}
