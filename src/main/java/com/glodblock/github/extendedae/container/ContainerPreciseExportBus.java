package com.glodblock.github.extendedae.container;

import appeng.core.definitions.AEItems;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.parts.PartPreciseExportBus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public class ContainerPreciseExportBus extends UpgradeableMenu<PartPreciseExportBus> {

    public static final MenuType<ContainerPreciseExportBus> TYPE = MenuTypeBuilder
            .create(ContainerPreciseExportBus::new, PartPreciseExportBus.class)
            .buildUnregistered(ExtendedAE.id("precise_export_bus"));

    public ContainerPreciseExportBus(int id, Inventory ip, PartPreciseExportBus host) {
        super(TYPE, id, ip, host);
    }

    @Override
    protected void setupConfig() {
        addExpandableConfigSlots(getHost().getConfig(), 2, 9, 5);
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        final int upgrades = getUpgrades().getInstalledUpgrades(AEItems.CAPACITY_CARD);
        return upgrades > idx;
    }

    public boolean isConfigSlot(Slot slot) {
        return this.getSlots(SlotSemantics.CONFIG).contains(slot);
    }

}