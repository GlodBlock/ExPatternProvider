package com.glodblock.github.extendedae.container;

import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.parts.PartTagExportBus;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.SEAEGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class ContainerTagExportBus extends UpgradeableMenu<PartTagExportBus> implements IActionHolder {
    private final ActionMap actions = ActionMap.create();

    public static final MenuType<ContainerTagExportBus> TYPE = MenuTypeBuilder
            .create(ContainerTagExportBus::new, PartTagExportBus.class)
            .buildUnregistered(ExtendedAE.id("tag_export_bus"));

    @GuiSync(9)
    public String exp = "";

    @GuiSync(10)
    public String exp2 = "";

    public ContainerTagExportBus(int id, Inventory ip, PartTagExportBus host) {
        super(TYPE, id, ip, host);
        this.actions.put("set", o -> this.setExp(o.get(0), o.get(1)));
        this.actions.put("update", o -> {
            if (this.getPlayer() instanceof ServerPlayer sp) {
                EAENetworkHandler.INSTANCE.sendTo(new SEAEGenericPacket("init", this.exp, this.exp2), sp);
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
        if (!this.exp.equals(getHost().getTagFilter(true))) {
            this.exp = getHost().getTagFilter(true);
        }
        if (!this.exp2.equals(getHost().getTagFilter(false))) {
            this.exp2 = getHost().getTagFilter(false);
        }
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        return false;
    }

    public void setExp(String exp, boolean isWhite) {
        getHost().setTagFilter(exp, isWhite);
        this.broadcastChanges();
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }

}
