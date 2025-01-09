package com.glodblock.github.extendedae.xmod.wt;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.util.IConfigManager;
import appeng.menu.locator.ItemMenuHostLocator;
import de.mari_023.ae2wtlib.api.terminal.AE2wtlibConfigManager;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ItemWirelessExPAT extends ItemWT {

    public ItemWirelessExPAT() {
    }

    @Override
    public @NotNull MenuType<?> getMenuType(@NotNull ItemMenuHostLocator itemMenuHostLocator, @NotNull Player player) {
        return ContainerWirelessExPAT.TYPE;
    }

    @Override
    public @NotNull IConfigManager getConfigManager(@NotNull Supplier<ItemStack> target) {
        return AE2wtlibConfigManager.builder(target)
                .registerSetting(Settings.TERMINAL_SHOW_PATTERN_PROVIDERS, ShowPatternProviders.VISIBLE)
                .build();
    }
}
