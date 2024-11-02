package com.glodblock.github.extendedae.util;

import com.glodblock.github.extendedae.ExtendedAE;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class EPPTags {

    public static final TagKey<Item> EX_PATTERN_PROVIDER = TagKey.create(Registries.ITEM, ExtendedAE.id("extended_pattern_provider"));
    public static final TagKey<Item> EX_INTERFACE = TagKey.create(Registries.ITEM, ExtendedAE.id("extended_interface"));
    public static final TagKey<Item> EX_EMC_INTERFACE = TagKey.create(Registries.ITEM, ExtendedAE.id("extended_emc_interface"));
    public static final TagKey<Item> OVERSIZE_INTERFACE = TagKey.create(Registries.ITEM, ExtendedAE.id("oversize_interface"));

}
