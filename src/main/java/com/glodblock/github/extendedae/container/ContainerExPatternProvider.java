package com.glodblock.github.extendedae.container;

import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.PatternProviderMenu;
import com.glodblock.github.extendedae.ExtendedAE;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerExPatternProvider extends PatternProviderMenu {

    public static final MenuType<ContainerExPatternProvider> TYPE = MenuTypeBuilder
            .create(ContainerExPatternProvider::new, PatternProviderLogicHost.class)
            .buildUnregistered(ExtendedAE.id("ex_pattern_provider"));

    protected ContainerExPatternProvider(int id, Inventory playerInventory, PatternProviderLogicHost host) {
        super(TYPE, id, playerInventory, host);
    }

}
