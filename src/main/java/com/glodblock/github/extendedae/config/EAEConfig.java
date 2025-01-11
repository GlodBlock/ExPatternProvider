package com.glodblock.github.extendedae.config;

import appeng.api.stacks.AEKey;
import com.glodblock.github.extendedae.ExtendedAE;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = ExtendedAE.MODID, bus = EventBusSubscriber.Bus.MOD)
public class EAEConfig {

    private static final IntList defaultModifierMultiplier = new IntImmutableList(new int[]{2, 3, 5, 7});

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.IntValue EX_BUS_SPEED = BUILDER
            .comment("ME Extend Import/Export Bus speed multiplier")
            .defineInRange("device.extended_io_bus_multiplier", 8, 2, 128);

    private static final ModConfigSpec.DoubleValue INFINITY_CELL_ENERGY = BUILDER
            .comment("ME Infinity Cell idle energy cost (unit: AE/t)")
            .defineInRange("item.infinity_cell_energy_cost", 8.0, 0.1, 64.0);

    private static final ModConfigSpec.DoubleValue WIRELESS_CONNECTOR_RANGE = BUILDER
            .comment("The max range between two wireless connector")
            .defineInRange("device.wireless_connector_max_range", 1000.0, 10.0, 10000.0);

    @SuppressWarnings("deprecation")
    private static final ModConfigSpec.ConfigValue<List<? extends Integer>> PATTERN_MODIFIER_NUMBER = BUILDER
            .comment("Pattern modifier multipliers")
            .defineList("item.pattern_modifier_multipliers", defaultModifierMultiplier, EAEConfig::checkPositive);

    @SuppressWarnings("deprecation")
    private static final ModConfigSpec.ConfigValue<List<? extends String>> PACKABLE_AE_DEVICE = BUILDER
            .comment("The AE device/part that can be packed by ME Packing Tape")
            .defineList("item.me_packing_tape_whitelist", Lists.newArrayList(
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
            .comment("Disable Extended Inscriber's item render, it only works in client side")
            .define("client.disable_inscriber_item_render", false);

    private static final ModConfigSpec.IntValue OVERSIZE_MULTIPLIER = BUILDER
            .comment("Size multiplier of oversize interface")
            .defineInRange("device.oversize_interface_multiplier", 16, 2, 4096);

    @SuppressWarnings("deprecation")
    private static final ModConfigSpec.ConfigValue<List<? extends String>> CUSTOM_OVERSIZE_MULTIPLIER = BUILDER
            .comment("Set multiplier for specific AEKeyType in oversize interface")
            .defineList("device.custom_oversize_interface_multiplier", Lists.newArrayList(
                    "appbot:mana 2",
                    "appflux:flux 4"
            ), o -> true);

    private static final ModConfigSpec.BooleanValue CRYSTAL_INSCRIBER = BUILDER
            .comment("Allow Crystal Assembler to do processor inscriber recipes")
            .define("device.enable_crystal_assembler_inscribe_processors", true);

    private static final ModConfigSpec.IntValue ASSEMBLER_MATRIX_SIZE = BUILDER
            .comment("The max size of Assembler Matrix")
            .defineInRange("device.assembler_matrix_max_size", 6, 3, 16);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean checkPositive(Object o) {
        return o instanceof Integer && (int) o > 0;
    }

    public static int busSpeed;
    public static double infCellCost;
    public static double wirelessMaxRange;
    public static List<ResourceLocation> tapeWhitelist;
    public static boolean disableInscriberRender;
    private static int oversizeMultiplier;
    private static Map<ResourceLocation, Integer> customOversizeMultiplier;
    private static List<? extends Integer> modifierMultiplier;
    public static boolean allowAssemblerCircuits;
    public static int assemblerMatrixSize;

    public static int getPatternModifierNumber(int index) {
        if (index >= modifierMultiplier.size()) {
            return defaultModifierMultiplier.getInt(index);
        } else {
            return modifierMultiplier.get(index);
        }
    }

    public static int getOversizeMultiplier(AEKey key) {
        return customOversizeMultiplier.getOrDefault(key.getType().getId(), oversizeMultiplier);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            busSpeed = EX_BUS_SPEED.get();
            infCellCost = INFINITY_CELL_ENERGY.get();
            wirelessMaxRange = WIRELESS_CONNECTOR_RANGE.get();
            tapeWhitelist = PACKABLE_AE_DEVICE.get().stream().map(ResourceLocation::parse).collect(Collectors.toList());
            disableInscriberRender = INSCRIBER_RENDER.get();
            oversizeMultiplier = OVERSIZE_MULTIPLIER.get();
            customOversizeMultiplier = new Object2IntOpenHashMap<>();
            CUSTOM_OVERSIZE_MULTIPLIER.get().stream()
                    .map(EAEConfig::parseOversizeMultiplier)
                    .filter(Objects::nonNull)
                    .forEach(p -> customOversizeMultiplier.put(p.getKey(), p.getValue()));
            modifierMultiplier = PATTERN_MODIFIER_NUMBER.get();
            allowAssemblerCircuits = CRYSTAL_INSCRIBER.get();
            assemblerMatrixSize = ASSEMBLER_MATRIX_SIZE.get();
        }
    }

    private static Pair<ResourceLocation, Integer> parseOversizeMultiplier(String s) {
        try {
            var pair = s.split(" ");
            return Pair.of(ResourceLocation.parse(pair[0]), Integer.decode(pair[1]));
        } catch (Throwable t) {
            return null;
        }
    }

}
