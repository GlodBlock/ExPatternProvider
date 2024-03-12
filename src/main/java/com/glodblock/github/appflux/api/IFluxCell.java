package com.glodblock.github.appflux.api;

import appeng.api.config.FuzzyMode;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.util.ConfigInventory;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public interface IFluxCell extends ICellWorkbenchItem {

    AEKeyType getKeyType();

    EnergyType getEnergyType();

    long getBytes(ItemStack cellItem);

    @Override
    default boolean isEditable(ItemStack is) {
        return true;
    }

    @Override
    default ConfigInventory getConfigInventory(ItemStack is) {
        return ConfigInventory.EMPTY_TYPES;
    }

    @Override
    default FuzzyMode getFuzzyMode(ItemStack is) {
        return null;
    }

    @Override
    default void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {
    }

    double getIdleDrain();

    void addCellInformationToTooltip(ItemStack is, List<Component> lines);

    Optional<TooltipComponent> getCellTooltipImage(ItemStack is);
}