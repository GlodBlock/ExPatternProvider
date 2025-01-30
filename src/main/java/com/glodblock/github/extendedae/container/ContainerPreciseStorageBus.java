package com.glodblock.github.extendedae.container;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Settings;
import appeng.api.config.StorageFilter;
import appeng.api.config.YesNo;
import appeng.api.stacks.GenericStack;
import appeng.api.util.IConfigManager;
import appeng.core.definitions.AEItems;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.parts.PartPreciseStorageBus;
import com.google.common.collect.Iterators;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;

public class ContainerPreciseStorageBus extends UpgradeableMenu<PartPreciseStorageBus> {

    private static final String ACTION_CLEAR = "clear";
    private static final String ACTION_PARTITION = "partition";

    public static final MenuType<ContainerPreciseStorageBus> TYPE = MenuTypeBuilder
            .create(ContainerPreciseStorageBus::new, PartPreciseStorageBus.class)
            .buildUnregistered(ExtendedAE.id("precise_storage_bus"));

    @GuiSync(3)
    public AccessRestriction rwMode = AccessRestriction.READ_WRITE;

    @GuiSync(4)
    public StorageFilter storageFilter = StorageFilter.EXTRACTABLE_ONLY;

    @GuiSync(7)
    public YesNo filterOnExtract = YesNo.YES;

    @GuiSync(8)
    @Nullable
    public Component connectedTo;

    public ContainerPreciseStorageBus(int id, Inventory ip, PartPreciseStorageBus te) {
        super(TYPE, id, ip, te);

        registerClientAction(ACTION_CLEAR, this::clear);
        registerClientAction(ACTION_PARTITION, this::partition);

        this.connectedTo = te.getConnectedToDescription();
    }

    @Override
    protected void setupConfig() {
        addExpandableConfigSlots(getHost().getConfig(), 2, 9, 5);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        this.connectedTo = getHost().getConnectedToDescription();
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.setReadWriteMode(cm.getSetting(Settings.ACCESS));
        this.setStorageFilter(cm.getSetting(Settings.STORAGE_FILTER));
        this.setFilterOnExtract(cm.getSetting(Settings.FILTER_ON_EXTRACT));
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        final int upgrades = getUpgrades().getInstalledUpgrades(AEItems.CAPACITY_CARD);
        return upgrades > idx;
    }

    public void clear() {
        if (isClientSide()) {
            sendClientAction(ACTION_CLEAR);
            return;
        }
        getHost().getConfig().clear();
        this.broadcastChanges();
    }

    public void partition() {
        if (isClientSide()) {
            sendClientAction(ACTION_PARTITION);
            return;
        }

        var inv = getHost().getConfig();
        var cellInv = getHost().getInternalHandler();

        Iterator<GenericStack> i = Collections.emptyIterator();
        if (cellInv != null) {
            i = Iterators.transform(cellInv.getAvailableStacks().iterator(), e -> new GenericStack(e.getKey(), e.getLongValue()));
        }

        inv.beginBatch();
        try {
            for (int x = 0; x < inv.size(); x++) {
                if (i.hasNext() && this.isSlotEnabled(x / 9 - 2)) {
                    inv.setStack(x, i.next());
                } else {
                    inv.setStack(x, null);
                }
            }
        } finally {
            inv.endBatch();
        }

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

    public boolean isConfigSlot(Slot slot) {
        return this.getSlots(SlotSemantics.CONFIG).contains(slot);
    }

}
