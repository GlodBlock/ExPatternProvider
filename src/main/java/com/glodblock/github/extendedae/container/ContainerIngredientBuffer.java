package com.glodblock.github.extendedae.container;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;
import appeng.util.ConfigMenuInventory;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.tileentities.TileIngredientBuffer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerIngredientBuffer extends AEBaseMenu {

    public static final MenuType<ContainerIngredientBuffer> TYPE = MenuTypeBuilder
            .create(ContainerIngredientBuffer::new, TileIngredientBuffer.class)
            .buildUnregistered(ExtendedAE.id("ingredient_buffer"));

    public ContainerIngredientBuffer(int id, Inventory playerInventory, TileIngredientBuffer host) {
        super(TYPE, id, playerInventory, host);
        for (int index = 0; index < host.getGenericInv().size(); index ++) {
            this.addSlot(new AppEngSlot(new ConfigMenuInventory(host.getGenericInv()), index), SlotSemantics.STORAGE);
        }
        this.createPlayerInventorySlots(playerInventory);
    }

}
