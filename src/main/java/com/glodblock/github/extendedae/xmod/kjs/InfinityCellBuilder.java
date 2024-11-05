package com.glodblock.github.extendedae.xmod.kjs;

import appeng.api.client.StorageCellModels;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import com.glodblock.github.extendedae.common.items.ItemInfinityCell;
import com.glodblock.github.extendedae.util.LazyInits;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

@ReturnsSelf
public class InfinityCellBuilder extends ItemBuilder {

    private Supplier<AEKey> record;
    private ResourceLocation model;

    public InfinityCellBuilder(ResourceLocation id) {
        super(id);
    }

    @Info("Create an infinity cell with given AEKey.")
    public InfinityCellBuilder type(Supplier<AEKey> key) {
        this.record = key;
        return this;
    }

    @Info("Create an infinity cell with given item.")
    public InfinityCellBuilder itemType(ResourceLocation id) {
        this.record = () -> AEItemKey.of(BuiltInRegistries.ITEM.get(id));
        return this;
    }

    @Info("Create an infinity cell with given fluid.")
    public InfinityCellBuilder fluidType(ResourceLocation id) {
        this.record = () -> AEFluidKey.of(BuiltInRegistries.FLUID.get(id));
        return this;
    }

    @Info("Set infinity cell's model in ME drive.")
    public InfinityCellBuilder cellModel(ResourceLocation model) {
        this.model = model;
        return this;
    }

    @Override
    public Item createObject() {
        var cell = new ItemInfinityCell(this.record, this.createItemProperties().stacksTo(1));
        if (this.model != null) {
            LazyInits.addCommon(() -> StorageCellModels.registerModel(cell, this.model));
        }
        return cell;
    }

}
