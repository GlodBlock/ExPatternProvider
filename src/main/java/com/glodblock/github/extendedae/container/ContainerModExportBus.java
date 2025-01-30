package com.glodblock.github.extendedae.container;

import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.parts.PartModExportBus;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.SEAEGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class ContainerModExportBus extends UpgradeableMenu<PartModExportBus> implements IActionHolder {
    private final ActionMap actions = ActionMap.create();

    public static final MenuType<ContainerModExportBus> TYPE = MenuTypeBuilder
            .create(ContainerModExportBus::new, PartModExportBus.class)
            .buildUnregistered(ExtendedAE.id("mod_export_bus"));

    @GuiSync(9)
    public String exp = "";

    public ContainerModExportBus(int id, Inventory ip, PartModExportBus host) {
        super(TYPE, id, ip, host);
        this.actions.put("set", o -> this.setExp(o.get(0)));
        this.actions.put("update", o -> {
            if (this.getPlayer() instanceof ServerPlayer sp) {
                EAENetworkHandler.INSTANCE.sendTo(new SEAEGenericPacket("init", this.exp), sp);
            }
        });
    }

    @Override
    protected void setupConfig() {
        // NO-OP
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (!this.exp.equals(getHost().getModNameFilter())) {
            this.exp = getHost().getModNameFilter();
        }
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        return false;
    }

    public void setExp(String exp) {
        getHost().setModNameFilter(exp);
        this.broadcastChanges();
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }


}
