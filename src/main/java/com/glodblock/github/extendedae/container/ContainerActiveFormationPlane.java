package com.glodblock.github.extendedae.container;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.stacks.AEKey;
import appeng.api.util.IConfigManager;
import appeng.core.definitions.AEItems;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.parts.PartActiveFormationPlane;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerActiveFormationPlane extends UpgradeableMenu<PartActiveFormationPlane> {

    public static final MenuType<ContainerActiveFormationPlane> TYPE = MenuTypeBuilder
            .create(ContainerActiveFormationPlane::new, PartActiveFormationPlane.class)
            .buildUnregistered(ExtendedAE.id("active_formation_plane"));

    @GuiSync(7)
    public YesNo placeMode;

    public ContainerActiveFormationPlane(int id, Inventory ip, PartActiveFormationPlane host) {
        super(TYPE, id, ip, host);
    }

    @Override
    protected void setupConfig() {
        addExpandableConfigSlots(getHost().getConfig(), 2, 9, 5);
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        if (supportsFuzzyRangeSearch()) {
            this.setFuzzyMode(cm.getSetting(Settings.FUZZY_MODE));
        }
        this.setPlaceMode(cm.getSetting(Settings.PLACE_BLOCK));
        this.setCraftingMode(cm.getSetting(Settings.CRAFT_ONLY));
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        final int upgrades = getUpgrades().getInstalledUpgrades(AEItems.CAPACITY_CARD);
        return upgrades > idx;
    }

    public YesNo getPlaceMode() {
        return this.placeMode;
    }

    private void setPlaceMode(YesNo placeMode) {
        this.placeMode = placeMode;
    }

    public boolean supportsFuzzyMode() {
        return hasUpgrade(AEItems.FUZZY_CARD) && supportsFuzzyRangeSearch();
    }

    private boolean supportsFuzzyRangeSearch() {
        for (AEKey key : this.getHost().getConfig().keySet()) {
            if (key.supportsFuzzyRangeSearch()) {
                return true;
            }
        }
        return false;
    }
}
