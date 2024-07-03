package com.glodblock.github.extendedae.config;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.util.FCUtil;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = ExtendedAE.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EAEConfig {

    private static IntList defaultModifierMultiplier = new IntImmutableList(new int[]{2, 3, 5, 7});

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.IntValue EX_BUS_SPEED = BUILDER
            .comment("ME Extend Import/Export Bus speed multiplier")
            .defineInRange("exBusMultiplier", 8, 2, 128);

    private static final ModConfigSpec.DoubleValue INFINITY_CELL_ENERGY = BUILDER
            .comment("ME Infinity Cell idle energy cost (unit: AE/t)")
            .defineInRange("cost", 8.0, 0.1, 64.0);

    private static final ModConfigSpec.DoubleValue WIRELESS_CONNECTOR_RANGE = BUILDER
            .comment("The max range between two wireless connector")
            .defineInRange("range", 1000.0, 10.0, 10000.0);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> INFINITY_CELL_TYPES = BUILDER
            .comment("ME Infinity Cell types (item or fluid's id)")
            .defineList("types", Lists.newArrayList("minecraft:water", "minecraft:cobblestone"), EAEConfig::checkRL);

    private static final ModConfigSpec.ConfigValue<List<? extends Integer>> PATTERN_MODIFIER_NUMBER = BUILDER
            .comment("Pattern modifier multipliers")
            .defineList("modifierMultipliers", defaultModifierMultiplier, EAEConfig::checkPositive);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> PACKABLE_AE_DEVICE = BUILDER
            .comment("The AE device/part that can be packed by ME Packing Tape")
            .defineList("whitelist", Lists.newArrayList(
                    "extendedae:ex_interface_part",
                    "extendedae:ex_pattern_provider_part",
                    "extendedae:ex_interface",
                    "extendedae:ex_pattern_provider",
                    "extendedae:ex_drive",
                    "ae2:cable_interface",
                    "ae2:cable_pattern_provider",
                    "ae2:interface",
                    "ae2:pattern_provider",
                    "ae2:drive"
            ), o -> true);

    private static final ModConfigSpec.BooleanValue INSCRIBER_RENDER = BUILDER
            .comment("Disable Extended Inscriber's item render, it only works in client side.")
            .define("disableItemRender", false);

    private static final ModConfigSpec.IntValue OVERSIZE_MULTIPLIER = BUILDER
            .comment("Size multiplier of oversize interface")
            .defineInRange("oversizeMultiplier", 16, 2, 4096);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean checkRL(Object o) {
        return o instanceof String s && (FCUtil.checkInvalidRL(s, BuiltInRegistries.ITEM) || FCUtil.checkInvalidRL(s, BuiltInRegistries.FLUID));
    }

    private static boolean checkPositive(Object o) {
        return o instanceof Integer && (int) o > 0;
    }

    public static int busSpeed;
    public static double infCellCost;
    public static double wirelessMaxRange;
    public static List<Fluid> infCellFluid;
    public static List<Item> infCellItem;
    public static List<ResourceLocation> tapeWhitelist;
    public static boolean disableInscriberRender;
    public static int oversizeMultiplier;
    private static List<? extends Integer> modifierMultiplier;

    public static int getPatternModifierNumber(int index) {
        if (index >= modifierMultiplier.size()) {
            return defaultModifierMultiplier.getInt(index);
        } else {
            return modifierMultiplier.get(index);
        }
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        busSpeed = EX_BUS_SPEED.get();
        infCellCost = INFINITY_CELL_ENERGY.get();
        wirelessMaxRange = WIRELESS_CONNECTOR_RANGE.get();
        infCellFluid = new ArrayList<>();
        infCellItem = new ArrayList<>();
        INFINITY_CELL_TYPES.get()
                .forEach(s -> {
                    if (FCUtil.checkInvalidRL(s, BuiltInRegistries.ITEM)) {
                        infCellItem.add(BuiltInRegistries.ITEM.get(new ResourceLocation(s)));
                    }
                    if (FCUtil.checkInvalidRL(s, BuiltInRegistries.FLUID)) {
                        infCellFluid.add(BuiltInRegistries.FLUID.get(new ResourceLocation(s)));
                    }
                });
        tapeWhitelist = PACKABLE_AE_DEVICE.get().stream().map(ResourceLocation::new).collect(Collectors.toList());
        disableInscriberRender = INSCRIBER_RENDER.get();
        oversizeMultiplier = OVERSIZE_MULTIPLIER.get();
        modifierMultiplier = PATTERN_MODIFIER_NUMBER.get();
    }

}
