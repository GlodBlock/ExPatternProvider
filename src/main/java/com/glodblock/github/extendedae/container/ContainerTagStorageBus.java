package com.glodblock.github.extendedae.container;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Settings;
import appeng.api.config.StorageFilter;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.parts.PartTagStorageBus;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.SEAEGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContainerTagStorageBus extends UpgradeableMenu<PartTagStorageBus> implements IActionHolder {

    private final ActionMap actions = ActionMap.create();
    private static final String ACTION_PARTITION = "partition";

    public static final MenuType<ContainerTagStorageBus> TYPE = MenuTypeBuilder
            .create(ContainerTagStorageBus::new, PartTagStorageBus.class)
            .buildUnregistered(ExtendedAE.id("tag_storage_bus"));

    @GuiSync(3)
    public AccessRestriction rwMode = AccessRestriction.READ_WRITE;

    @GuiSync(4)
    public StorageFilter storageFilter = StorageFilter.EXTRACTABLE_ONLY;

    @GuiSync(7)
    public YesNo filterOnExtract = YesNo.YES;

    @GuiSync(8)
    @Nullable
    public Component connectedTo;

    @GuiSync(9)
    public String exp = "";

    @GuiSync(10)
    public String exp2 = "";

    public ContainerTagStorageBus(int id, Inventory ip, PartTagStorageBus te) {
        super(TYPE, id, ip, te);

        registerClientAction(ACTION_PARTITION, this::partition);
        this.actions.put("set", o -> this.setExp(o.get(0), o.get(1)));
        this.actions.put("update", o -> {
            if (this.getPlayer() instanceof ServerPlayer sp) {
                EAENetworkHandler.INSTANCE.sendTo(new SEAEGenericPacket("init", this.exp, this.exp2), sp);
            }
        });
        this.connectedTo = te.getConnectedToDescription();
    }

    @Override
    protected void setupConfig() {
        // NO-OP
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        this.connectedTo = getHost().getConnectedToDescription();
        if (!this.exp.equals(getHost().getTagFilter(true))) {
            this.exp = getHost().getTagFilter(true);
        }
        if (!this.exp2.equals(getHost().getTagFilter(false))) {
            this.exp2 = getHost().getTagFilter(false);
        }
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.setReadWriteMode(cm.getSetting(Settings.ACCESS));
        this.setStorageFilter(cm.getSetting(Settings.STORAGE_FILTER));
        this.setFilterOnExtract(cm.getSetting(Settings.FILTER_ON_EXTRACT));
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        return false;
    }

    public void partition() {
        if (isClientSide()) {
            sendClientAction(ACTION_PARTITION);
            return;
        }
        this.broadcastChanges();
    }

    public void setExp(String exp, boolean isWhite) {
        getHost().setTagFilter(exp, isWhite);
        this.broadcastChanges();
    }

    public AccessRestriction getReadWriteMode() {
        return this.rwMode;
    }

    private void setReadWriteMode(AccessRestriction rwMode) {
        this.rwMode = rwMode;
    }

    public StorageFilter getStorageFilter() {
        return this.storageFilter;
    }

    private void setStorageFilter(StorageFilter storageFilter) {
        this.storageFilter = storageFilter;
    }

    public YesNo getFilterOnExtract() {
        return this.filterOnExtract;
    }

    public void setFilterOnExtract(YesNo filterOnExtract) {
        this.filterOnExtract = filterOnExtract;
    }

    @Nullable
    public Component getConnectedTo() {
        return connectedTo;
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }
}
