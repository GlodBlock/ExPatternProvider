package com.glodblock.github.extendedae.util;

import com.glodblock.github.extendedae.ExtendedAE;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class EAETags {

    public static final TagKey<Item> EX_PATTERN_PROVIDER = TagKey.create(Registries.ITEM, ExtendedAE.id("extended_pattern_provider"));
    public static final TagKey<Item> EX_INTERFACE = TagKey.create(Registries.ITEM, ExtendedAE.id("extended_interface"));
    public static final TagKey<Item> ENTRO_DUST = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/entro"));
    public static final TagKey<Item> ENTRO_CRYSTAL = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "gems/entro"));
    public static final TagKey<Item> ENTRO_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ingots/infused_entro"));
    public static final TagKey<Item> ENTRO_BLOCK = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/entro"));
    public static final TagKey<Item> SILICON_BLOCK = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/silicon"));
    public static final TagKey<Item> OVERSIZE_INTERFACE = TagKey.create(Registries.ITEM, ExtendedAE.id("oversize_interface"));
    public static final TagKey<Item> QUARTZ_DUST = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/quartz"));

    public static final TagKey<Block> ENTRO_BLOCK_BLOCK = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/entro"));
    public static final TagKey<Block> SILICON_BLOCK_BLOCK = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/silicon"));

}
