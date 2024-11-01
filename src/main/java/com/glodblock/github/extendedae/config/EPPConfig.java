package com.glodblock.github.extendedae.config;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.util.FCUtil;
import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = ExtendedAE.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EPPConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.IntValue EX_BUS_SPEED = BUILDER
            .comment("ME Extend Import/Export Bus speed multiplier")
            .defineInRange("exBusMultiplier", 8, 2, 128);

    private static final ForgeConfigSpec.DoubleValue INFINITY_CELL_ENERGY = BUILDER
            .comment("ME Infinity Cell idle energy cost (unit: AE/t)")
            .defineInRange("cost", 8.0, 0.1, 64.0);

    private static final ForgeConfigSpec.DoubleValue WIRELESS_CONNECTOR_RANGE = BUILDER
            .comment("The max range between two wireless connector")
            .defineInRange("range", 1000.0, 10.0, 10000.0);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> INFINITY_CELL_TYPES = BUILDER
            .comment("ME Infinity Cell types (item or fluid's id)")
            .defineList("types", Lists.newArrayList("minecraft:water", "minecraft:cobblestone"), EPPConfig::checkRL);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> PACKABLE_AE_DEVICE = BUILDER
            .comment("The AE device/part that can be packed by ME Packing Tape")
            .defineList("whitelist", Lists.newArrayList(
                    "expatternprovider:ex_interface_part",
                    "expatternprovider:ex_pattern_provider_part",
                    "expatternprovider:ex_interface",
                    "expatternprovider:ex_pattern_provider",
                    "expatternprovider:ex_drive",
                    "ae2:cable_interface",
                    "ae2:cable_pattern_provider",
                    "ae2:interface",
                    "ae2:pattern_provider",
                    "ae2:drive"
            ), o -> true);

    private static final ForgeConfigSpec.BooleanValue INSCRIBER_RENDER = BUILDER
            .comment("Disable Extended Inscriber's item render, it only works in client side.")
            .define("disableItemRender", false);

    private static final ForgeConfigSpec.IntValue OVERSIZE_MULTIPLIER = BUILDER
            .comment("Size multiplier of oversize interface")
            .defineInRange("device.oversize_interface_multiplier", 16, 2, 4096);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private static boolean checkRL(Object o) {
        return o instanceof String s && (FCUtil.checkInvalidRL(s, ForgeRegistries.ITEMS) || FCUtil.checkInvalidRL(s, ForgeRegistries.FLUIDS));
    }

    public static int busSpeed;
    public static double infCellCost;
    public static double wirelessMaxRange;
    public static List<Fluid> infCellFluid;
    public static List<Item> infCellItem;
    public static List<ResourceLocation> tapeWhitelist;
    public static boolean disableInscriberRender;
    public static int oversizeMultiplier;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        busSpeed = EX_BUS_SPEED.get();
        infCellCost = INFINITY_CELL_ENERGY.get();
        wirelessMaxRange = WIRELESS_CONNECTOR_RANGE.get();
        infCellFluid = new ArrayList<>();
        infCellItem = new ArrayList<>();
        INFINITY_CELL_TYPES.get()
                .forEach(s -> {
                    if (FCUtil.checkInvalidRL(s, ForgeRegistries.ITEMS)) {
                        infCellItem.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(s)));
                    }
                    if (FCUtil.checkInvalidRL(s, ForgeRegistries.FLUIDS)) {
                        infCellFluid.add(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(s)));
                    }
                });
        tapeWhitelist = PACKABLE_AE_DEVICE.get().stream().map(ResourceLocation::new).collect(Collectors.toList());
        disableInscriberRender = INSCRIBER_RENDER.get();
        oversizeMultiplier = OVERSIZE_MULTIPLIER.get();
    }

}
