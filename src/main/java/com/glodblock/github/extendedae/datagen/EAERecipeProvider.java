package com.glodblock.github.extendedae.datagen;

import appeng.api.util.AEColor;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.datagen.providers.tags.ConventionTags;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipeBuilder;
import appeng.recipes.transform.TransformCircumstance;
import appeng.recipes.transform.TransformRecipeBuilder;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.config.ConfigCondition;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipeBuilder;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipeBuilder;
import com.glodblock.github.extendedae.recipe.CrystalFixerRecipeBuilder;
import com.glodblock.github.extendedae.util.EAETags;
import com.glodblock.github.extendedae.xmod.ModConstants;
import com.glodblock.github.glodium.util.GlodUtil;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class EAERecipeProvider extends RecipeProvider {

    static String C = "has_item";

    public EAERecipeProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> provider) {
        super(p, provider);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput c) {
        var quartzBlock = Ingredient.of(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR);
        // Extended Pattern Provider
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_PATTERN_PROVIDER)
                .input(ConventionTags.PATTERN_PROVIDER)
                .input(AEItems.CAPACITY_CARD, 3)
                .input(Blocks.CRAFTING_TABLE, 3)
                .input(EAESingletons.CONCURRENT_PROCESSOR)
                .input(ConventionTags.GLASS_CABLE, 6)
                .save(c, ExtendedAE.id("assembler/ex_pattern_provider"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAESingletons.PATTERN_PROVIDER_UPGRADE)
                .requires(EAETags.EX_PATTERN_PROVIDER)
                .requires(Tags.Items.INGOTS)
                .unlockedBy(C, has(EAETags.EX_PATTERN_PROVIDER))
                .save(c, ExtendedAE.id("ex_pattern_provider_upgrade"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAESingletons.EX_PATTERN_PROVIDER_PART)
                .requires(EAESingletons.EX_PATTERN_PROVIDER)
                .unlockedBy(C, has(EAESingletons.EX_PATTERN_PROVIDER_PART))
                .save(c, ExtendedAE.id("ex_pattern_provider_part"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAESingletons.EX_PATTERN_PROVIDER)
                .requires(EAESingletons.EX_PATTERN_PROVIDER_PART)
                .unlockedBy(C, has(EAESingletons.EX_PATTERN_PROVIDER))
                .save(c, ExtendedAE.id("ex_pattern_provider_alt"));

        // Extended Interface
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_INTERFACE)
                .input(ConventionTags.INTERFACE)
                .input(AEItems.CAPACITY_CARD, 3)
                .input(ConventionTags.GLASS, 3)
                .input(EAESingletons.CONCURRENT_PROCESSOR)
                .input(ConventionTags.GLASS_CABLE, 6)
                .save(c, ExtendedAE.id("assembler/ex_interface"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAESingletons.INTERFACE_UPGRADE)
                .requires(EAETags.EX_INTERFACE)
                .requires(Tags.Items.INGOTS)
                .unlockedBy(C, has(EAETags.EX_INTERFACE))
                .save(c, ExtendedAE.id("ex_interface_upgrade"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAESingletons.EX_INTERFACE_PART)
                .requires(EAESingletons.EX_INTERFACE)
                .unlockedBy(C, has(EAESingletons.EX_INTERFACE_PART))
                .save(c, ExtendedAE.id("ex_interface_part"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAESingletons.EX_INTERFACE)
                .requires(EAESingletons.EX_INTERFACE_PART)
                .unlockedBy(C, has(EAESingletons.EX_INTERFACE_PART))
                .save(c, ExtendedAE.id("ex_interface_alt"));

        // Infinity Cell
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.INFINITY_WATER_CELL)
                .pattern("CWC")
                .pattern("WXW")
                .pattern("III")
                .define('C', AEBlocks.QUARTZ_GLASS)
                .define('W', Items.WATER_BUCKET)
                .define('X', AEItems.CELL_COMPONENT_16K)
                .define('I', ConventionTags.DIAMOND)
                .unlockedBy(C, has(EAESingletons.INFINITY_WATER_CELL))
                .save(c, ExtendedAE.id("water_cell"));
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.INFINITY_COBBLESTONE_CELL)
                .pattern("CLC")
                .pattern("WXW")
                .pattern("III")
                .define('C', AEBlocks.QUARTZ_GLASS)
                .define('L', Items.LAVA_BUCKET)
                .define('W', Items.WATER_BUCKET)
                .define('X', AEItems.CELL_COMPONENT_16K)
                .define('I', ConventionTags.DIAMOND)
                .unlockedBy(C, has(EAESingletons.INFINITY_COBBLESTONE_CELL))
                .save(c, ExtendedAE.id("cobblestone_cell"));

        // Extended IO Bus
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_EXPORT_BUS)
                .input(AEParts.EXPORT_BUS)
                .input(AEItems.SPEED_CARD, 3)
                .input(Blocks.PISTON, 2)
                .input(AEItems.FORMATION_CORE)
                .save(c, ExtendedAE.id("assembler/ex_export_bus"));
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_IMPORT_BUS)
                .input(AEParts.IMPORT_BUS)
                .input(AEItems.SPEED_CARD, 3)
                .input(Blocks.PISTON, 2)
                .input(AEItems.ANNIHILATION_CORE)
                .save(c, ExtendedAE.id("assembler/ex_import_bus"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAESingletons.IO_BUS_UPGRADE)
                .requires(EAESingletons.EX_IMPORT_BUS)
                .requires(Tags.Items.INGOTS)
                .unlockedBy(C, has(EAESingletons.EX_IMPORT_BUS))
                .save(c, ExtendedAE.id("ex_bus_upgrade"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAESingletons.IO_BUS_UPGRADE)
                .requires(EAESingletons.EX_EXPORT_BUS)
                .requires(Tags.Items.INGOTS)
                .unlockedBy(C, has(EAESingletons.EX_EXPORT_BUS))
                .save(c, ExtendedAE.id("ex_bus_upgrade_alt"));

        // Extended Pattern Access Terminal
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_PATTERN_TERMINAL)
                .input(AEParts.PATTERN_ACCESS_TERMINAL)
                .input(Blocks.REDSTONE_LAMP)
                .input(AEItems.LOGIC_PROCESSOR, 2)
                .input(AEBlocks.QUARTZ_FIXTURE, 4)
                .save(c, ExtendedAE.id("assembler/ex_pattern_access_terminal"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAESingletons.PATTERN_UPGRADE)
                .requires(EAESingletons.EX_PATTERN_TERMINAL)
                .requires(Tags.Items.INGOTS)
                .unlockedBy(C, has(EAESingletons.EX_PATTERN_TERMINAL))
                .save(c, ExtendedAE.id("ex_pattern_access_terminal_upgrade"));

        // ME Packing Tape
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.PACKING_TAPE)
                .pattern("FG ")
                .pattern("PIP")
                .pattern(" GF")
                .define('I', ConventionTags.IRON_INGOT)
                .define('P', Items.PAPER)
                .define('G', Items.SLIME_BALL)
                .define('F', ConventionTags.FLUIX_DUST)
                .unlockedBy(C, has(EAESingletons.PACKING_TAPE))
                .save(c, ExtendedAE.id("tape"));

        // Wireless Connector
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.WIRELESS_CONNECTOR, 2)
                .input(EAESingletons.MACHINE_FRAME)
                .input(ConventionTags.SMART_DENSE_CABLE, 2)
                .input(AEItems.WIRELESS_RECEIVER, 2)
                .input(AEItems.WIRELESS_BOOSTER, 3)
                .save(c, ExtendedAE.id("assembler/wireless_connector"));
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.WIRELESS_TOOL)
                .input(AEItems.WIRELESS_RECEIVER)
                .input(ConventionTags.IRON_INGOT, 2)
                .input(AEItems.CALCULATION_PROCESSOR)
                .input(AEItems.SINGULARITY)
                .save(c, ExtendedAE.id("assembler/wireless_kit"));

        // Ingredient Buffer
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.INGREDIENT_BUFFER)
                .pattern("IKI")
                .pattern("G G")
                .pattern("IKI")
                .define('I', ConventionTags.IRON_INGOT)
                .define('K', AEItems.CELL_COMPONENT_1K)
                .define('G', AEBlocks.QUARTZ_GLASS)
                .unlockedBy(C, has(EAESingletons.INGREDIENT_BUFFER))
                .save(c, ExtendedAE.id("ingredient_buffer"));

        // Extended ME Drive
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_DRIVE)
                .input(AEBlocks.DRIVE)
                .input(ConventionTags.GLASS_CABLE, 2)
                .input(AEItems.CAPACITY_CARD)
                .input(EAESingletons.CONCURRENT_PROCESSOR)
                .save(c, ExtendedAE.id("assembler/ex_drive"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAESingletons.DRIVE_UPGRADE)
                .requires(EAESingletons.EX_DRIVE)
                .requires(Tags.Items.INGOTS)
                .unlockedBy(C, has(EAESingletons.EX_DRIVE))
                .save(c, ExtendedAE.id("ex_drive_upgrade"));

        // Pattern Modifier
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.PATTERN_MODIFIER)
                .pattern("GPG")
                .pattern(" L ")
                .define('G', ConventionTags.dye(DyeColor.GREEN))
                .define('P', AEItems.BLANK_PATTERN)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(EAESingletons.PATTERN_MODIFIER))
                .save(c, ExtendedAE.id("pattern_modifier"));

        // Extended Molecular Assembler
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_ASSEMBLER)
                .input(AEBlocks.MOLECULAR_ASSEMBLER, 4)
                .input(EAESingletons.CONCURRENT_PROCESSOR, 4)
                .input(ConventionTags.FLUIX_DUST, 4)
                .input(AEItems.ENGINEERING_PROCESSOR, 3)
                .input(AEItems.SPEED_CARD)
                .save(c, ExtendedAE.id("assembler/ex_molecular_assembler"));

        // Extended Inscriber
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_INSCRIBER)
                .input(AEBlocks.INSCRIBER)
                .input(AEItems.CAPACITY_CARD, 3)
                .input(EAESingletons.CONCURRENT_PROCESSOR)
                .save(c, ExtendedAE.id("assembler/ex_inscriber"));

        // Extended Charger
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_CHARGER)
                .input(AEBlocks.CHARGER)
                .input(AEItems.CAPACITY_CARD)
                .input(EAESingletons.CONCURRENT_PROCESSOR)
                .save(c, ExtendedAE.id("assembler/ex_charger"));

        // Tag Storage Bus
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.TAG_STORAGE_BUS)
                .pattern(" L ")
                .pattern("RBR")
                .pattern(" K ")
                .define('L', AEItems.LOGIC_PROCESSOR)
                .define('R', ConventionTags.REDSTONE)
                .define('B', AEParts.STORAGE_BUS)
                .define('K', Items.BOOK)
                .unlockedBy(C, has(EAESingletons.TAG_STORAGE_BUS))
                .save(c, ExtendedAE.id("tag_storage_bus"));

        // Tag Export Bus
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.TAG_EXPORT_BUS)
                .pattern(" L ")
                .pattern("RBR")
                .pattern(" K ")
                .define('L', AEItems.LOGIC_PROCESSOR)
                .define('R', ConventionTags.REDSTONE)
                .define('B', AEParts.EXPORT_BUS)
                .define('K', Items.BOOK)
                .unlockedBy(C, has(EAESingletons.TAG_EXPORT_BUS))
                .save(c, ExtendedAE.id("tag_export_bus"));

        // Threshold Level Emitter
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.THRESHOLD_LEVEL_EMITTER)
                .pattern("RER")
                .pattern(" C ")
                .define('R', Items.REDSTONE_TORCH)
                .define('E', AEParts.LEVEL_EMITTER)
                .define('C', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EAESingletons.THRESHOLD_LEVEL_EMITTER))
                .save(c, ExtendedAE.id("threshold_level_emitter"));

        // Mod Storage Bus
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.MOD_STORAGE_BUS)
                .pattern(" C ")
                .pattern("RBR")
                .pattern(" K ")
                .define('C', AEItems.CALCULATION_PROCESSOR)
                .define('R', ConventionTags.REDSTONE)
                .define('B', AEParts.STORAGE_BUS)
                .define('K', Items.BOOK)
                .unlockedBy(C, has(EAESingletons.MOD_STORAGE_BUS))
                .save(c, ExtendedAE.id("mod_storage_bus"));

        // Mod Export Bus
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.MOD_EXPORT_BUS)
                .pattern(" C ")
                .pattern("RBR")
                .pattern(" K ")
                .define('C', AEItems.CALCULATION_PROCESSOR)
                .define('R', ConventionTags.REDSTONE)
                .define('B', AEParts.EXPORT_BUS)
                .define('K', Items.BOOK)
                .unlockedBy(C, has(EAESingletons.MOD_EXPORT_BUS))
                .save(c, ExtendedAE.id("mod_export_bus"));

        // Active Formation Plane
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.ACTIVE_FORMATION_PLANE)
                .input(AEParts.FORMATION_PLANE)
                .input(Blocks.PISTON, 3)
                .input(AEItems.FORMATION_CORE, 2)
                .input(EAESingletons.EX_EXPORT_BUS)
                .save(c, ExtendedAE.id("assembler/active_formation_plane"));

        // ME Caner
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.CANER)
                .input(EAESingletons.MACHINE_FRAME)
                .input(EAESingletons.INGREDIENT_BUFFER)
                .input(AEItems.FORMATION_CORE)
                .input(AEItems.ANNIHILATION_CORE)
                .save(c, ExtendedAE.id("assembler/caner"));

        // ME Precise Export Bus
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.PRECISE_EXPORT_BUS)
                .pattern("PBP")
                .define('B', EAESingletons.EX_EXPORT_BUS)
                .define('P', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EAESingletons.EX_EXPORT_BUS))
                .save(c, ExtendedAE.id("precise_export_bus"));

        // Wireless Ex PAT
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.WIRELESS_EX_PAT)
                .pattern("A")
                .pattern("B")
                .pattern("C")
                .define('A', AEItems.WIRELESS_RECEIVER)
                .define('B', EAESingletons.EX_PATTERN_TERMINAL)
                .define('C', AEBlocks.DENSE_ENERGY_CELL)
                .unlockedBy(C, has(EAESingletons.EX_PATTERN_TERMINAL))
                .save(c, ExtendedAE.id("wireless_ex_pat"));

        // Extended IO Port
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.EX_IO_PORT)
                .input(AEBlocks.IO_PORT)
                .input(AEItems.SPEED_CARD, 4)
                .input(AEItems.LOGIC_PROCESSOR, 3)
                .input(EAESingletons.CONCURRENT_PROCESSOR, 2)
                .save(c, ExtendedAE.id("assembler/ex_io_port"));

        // Crystal Fixer
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.CRYSTAL_FIXER)
                .input(EAESingletons.MACHINE_FRAME)
                .input(ConventionTags.ALL_CERTUS_QUARTZ, 4)
                .input(AEParts.QUARTZ_FIBER, 2)
                .save(c, ExtendedAE.id("assembler/crystal_fixer"));

        // Precise Storage Bus
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.PRECISE_STORAGE_BUS)
                .pattern("PBP")
                .define('B', AEParts.STORAGE_BUS)
                .define('P', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(AEParts.STORAGE_BUS))
                .save(c, ExtendedAE.id("precise_storage_bus"));

        // Threshold Export Bus
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.THRESHOLD_EXPORT_BUS)
                .pattern("LCE")
                .define('L', AEParts.LEVEL_EMITTER)
                .define('C', AEItems.LOGIC_PROCESSOR)
                .define('E', AEParts.EXPORT_BUS)
                .unlockedBy(C, has(AEParts.LEVEL_EMITTER))
                .save(c, ExtendedAE.id("threshold_export_bus"));

        // Fishbig
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.FISHBIG)
                .pattern("FFF")
                .pattern("F F")
                .pattern("FFF")
                .define('F', Items.PUFFERFISH)
                .unlockedBy(C, has(EAESingletons.FISHBIG))
                .save(c, ExtendedAE.id("fishbig"));

        // Entro Seed
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAESingletons.ENTRO_SEED, 2)
                .requires(Tags.Items.SANDS)
                .requires(ConventionTags.ENDER_PEARL_DUST)
                .requires(ConventionTags.ENDER_PEARL_DUST)
                .requires(ConventionTags.ENDER_PEARL_DUST)
                .requires(ConventionTags.REDSTONE)
                .requires(ConventionTags.REDSTONE)
                .requires(ConventionTags.GLOWSTONE)
                .requires(ConventionTags.GLOWSTONE)
                .requires(AEItems.SKY_DUST)
                .unlockedBy(C, has(ConventionTags.ENDER_PEARL_DUST))
                .save(c, ExtendedAE.id("entro_seed"));

        // Entro Dust
        InscriberRecipeBuilder
                .inscribe(EAETags.ENTRO_CRYSTAL, EAESingletons.ENTRO_DUST, 1)
                .setMode(InscriberProcessType.INSCRIBE)
                .save(c, ExtendedAE.id("inscriber/crush_entro"));

        // Entro Crystal
        TransformRecipeBuilder
                .transform(c,
                        ExtendedAE.id("transform/fix_entro"),
                        EAESingletons.ENTRO_CRYSTAL, 1,
                        TransformCircumstance.fluid(FluidTags.WATER),
                        Ingredient.of(EAETags.ENTRO_DUST),
                        Ingredient.of(AEItems.FLUIX_CRYSTAL)
                );

        // Entro Ingot
        TransformRecipeBuilder
                .transform(c,
                        ExtendedAE.id("transform/entro_ingot"),
                        EAESingletons.ENTRO_INGOT, 1,
                        TransformCircumstance.fluid(FluidTags.WATER),
                        Ingredient.of(EAETags.ENTRO_DUST),
                        Ingredient.of(ConventionTags.GOLD_INGOT),
                        Ingredient.of(Items.LAPIS_LAZULI)
                );

        // Machine Frame
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.MACHINE_FRAME)
                .pattern("ECE")
                .pattern("IGI")
                .pattern("ECE")
                .define('E', EAETags.ENTRO_INGOT)
                .define('C', ConventionTags.COPPER_INGOT)
                .define('I', ConventionTags.IRON_INGOT)
                .define('G', AEBlocks.QUARTZ_GLASS)
                .unlockedBy(C, has(EAETags.ENTRO_INGOT))
                .save(c, ExtendedAE.id("machine_frame"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.MACHINE_FRAME)
                .pattern("EIE")
                .pattern("CGC")
                .pattern("EIE")
                .define('E', EAETags.ENTRO_INGOT)
                .define('C', ConventionTags.COPPER_INGOT)
                .define('I', ConventionTags.IRON_INGOT)
                .define('G', AEBlocks.QUARTZ_GLASS)
                .unlockedBy(C, has(EAETags.ENTRO_INGOT))
                .save(c, ExtendedAE.id("machine_frame_mirror"));

        // Crystal Assembler
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.CRYSTAL_ASSEMBLER)
                .pattern(" T ")
                .pattern("LML")
                .pattern("CKC")
                .define('T', AEParts.CRAFTING_TERMINAL)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .define('M', EAESingletons.MACHINE_FRAME)
                .define('C', ConventionTags.GLASS_CABLE)
                .define('K', AEBlocks.SKY_STONE_TANK)
                .unlockedBy(C, has(EAESingletons.MACHINE_FRAME))
                .save(c, ExtendedAE.id("crystal_assembler"));

        // Concurrent Processor
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.CONCURRENT_PROCESSOR_PRESS)
                .input(Items.ENDER_EYE, 3)
                .input(EAETags.ENTRO_CRYSTAL, 4)
                .input(AEItems.SILICON_PRESS)
                .save(c, ExtendedAE.id("assembler/concurrent_press"));

        InscriberRecipeBuilder
                .inscribe(EAETags.ENTRO_CRYSTAL, EAESingletons.CONCURRENT_PROCESSOR_PRINT, 1)
                .setTop(Ingredient.of(EAESingletons.CONCURRENT_PROCESSOR_PRESS))
                .setMode(InscriberProcessType.INSCRIBE)
                .save(c, ExtendedAE.id("inscriber/concurrent_print"));

        InscriberRecipeBuilder.inscribe(ConventionTags.REDSTONE, EAESingletons.CONCURRENT_PROCESSOR, 1)
                .setTop(Ingredient.of(EAESingletons.CONCURRENT_PROCESSOR_PRINT))
                .setBottom(Ingredient.of(AEItems.SILICON_PRINT))
                .setMode(InscriberProcessType.PRESS)
                .save(c, ExtendedAE.id("inscriber/concurrent_process"));

        // Entro Block
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.ENTRO_BLOCK)
                .pattern("CC")
                .pattern("CC")
                .define('C', EAETags.ENTRO_CRYSTAL)
                .unlockedBy(C, has(EAETags.ENTRO_CRYSTAL))
                .save(c, ExtendedAE.id("entro_block"));

        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAESingletons.ENTRO_CRYSTAL, 4)
                .requires(EAETags.ENTRO_BLOCK)
                .unlockedBy(C, has(EAETags.ENTRO_BLOCK))
                .save(c, ExtendedAE.id("entro_block_unpack"));

        // Budding Block
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING)
                .input(EAESingletons.ENTRO_SEED)
                .input(AEBlocks.FLUIX_BLOCK)
                .fluid(Fluids.WATER, 200)
                .save(c, ExtendedAE.id("assembler/budding"));

        // Silicon BLock
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.SILICON_BLOCK)
                .pattern("SSS")
                .pattern("S#S")
                .pattern("SSS")
                .define('S', ConventionTags.SILICON)
                .define('#', AEItems.SILICON)
                .unlockedBy(C, has(AEItems.SILICON))
                .save(c, ExtendedAE.id("silicon_block"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, AEItems.SILICON, 9)
                .requires(EAESingletons.SILICON_BLOCK)
                .unlockedBy(C, has(EAESingletons.SILICON_BLOCK))
                .save(c, ExtendedAE.id("silicon_decompress"));

        // Circuit Cutter
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.CIRCUIT_CUTTER)
                .input(EAESingletons.MACHINE_FRAME)
                .input(AEItems.ENGINEERING_PROCESSOR, 8)
                .input(AEItems.CALCULATION_PROCESSOR_PRESS)
                .input(AEItems.ENGINEERING_PROCESSOR_PRESS)
                .input(AEItems.LOGIC_PROCESSOR_PRESS)
                .input(AEItems.SILICON_PRESS)
                .input(EAESingletons.CONCURRENT_PROCESSOR_PRESS)
                .input(Blocks.STONECUTTER)
                .save(c, ExtendedAE.id("assembler/circuit_cutter"));

        // Oversize Interface
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.OVERSIZE_INTERFACE)
                .input(EAETags.EX_INTERFACE)
                .input(EAESingletons.INGREDIENT_BUFFER)
                .input(EAESingletons.CONCURRENT_PROCESSOR, 2)
                .input(AEItems.ANNIHILATION_CORE, 2)
                .input(AEItems.FORMATION_CORE, 2)
                .save(c, ExtendedAE.id("assembler/oversize_interface"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAESingletons.OVERSIZE_INTERFACE_PART)
                .requires(EAESingletons.OVERSIZE_INTERFACE)
                .unlockedBy(C, has(EAESingletons.OVERSIZE_INTERFACE))
                .save(c, ExtendedAE.id("oversize_interface_part"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAESingletons.OVERSIZE_INTERFACE)
                .requires(EAESingletons.OVERSIZE_INTERFACE_PART)
                .unlockedBy(C, has(EAESingletons.OVERSIZE_INTERFACE_PART))
                .save(c, ExtendedAE.id("oversize_interface_alt"));

        // Entro Shard
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.ENTRO_CRYSTAL)
                .pattern("SSS")
                .pattern("S S")
                .pattern("SSS")
                .define('S', EAESingletons.ENTRO_SHARD)
                .unlockedBy(C, has(EAESingletons.ENTRO_SHARD))
                .save(c, ExtendedAE.id("entro_recycle"));

        // Assembler Matrix
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.ASSEMBLER_MATRIX_FRAME)
                .pattern("QLQ")
                .pattern("LML")
                .pattern("QLQ")
                .define('Q', ConventionTags.NETHER_QUARTZ)
                .define('L', Items.LAPIS_LAZULI)
                .define('M', EAESingletons.MACHINE_FRAME)
                .unlockedBy(C, has(EAESingletons.MACHINE_FRAME))
                .save(c, ExtendedAE.id("assembler_matrix_frame"));
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.ASSEMBLER_MATRIX_WALL, 2)
                .pattern("BFB")
                .pattern("IQI")
                .pattern("BFB")
                .define('B', Items.IRON_BARS)
                .define('F', ConventionTags.FLUIX_CRYSTAL)
                .define('I', EAETags.ENTRO_INGOT)
                .define('Q', quartzBlock)
                .unlockedBy(C, has(EAESingletons.ENTRO_INGOT))
                .save(c, ExtendedAE.id("assembler_matrix_wall"));
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.ASSEMBLER_MATRIX_GLASS, 2)
                .pattern("BFB")
                .pattern("IQI")
                .pattern("BFB")
                .define('B', AEBlocks.QUARTZ_GLASS)
                .define('F', ConventionTags.FLUIX_CRYSTAL)
                .define('I', EAETags.ENTRO_INGOT)
                .define('Q', quartzBlock)
                .unlockedBy(C, has(EAESingletons.ENTRO_INGOT))
                .save(c, ExtendedAE.id("assembler_matrix_glass"));
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.ASSEMBLER_MATRIX_CRAFTER)
                .input(EAESingletons.ASSEMBLER_MATRIX_WALL)
                .input(EAESingletons.EX_ASSEMBLER)
                .input(AEItems.COLORED_LUMEN_PAINT_BALL.item(AEColor.PURPLE), 6)
                .input(AEItems.LOGIC_PROCESSOR)
                .save(c, ExtendedAE.id("assembler/assembler_matrix_crafter"));
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.ASSEMBLER_MATRIX_PATTERN)
                .input(EAESingletons.ASSEMBLER_MATRIX_WALL)
                .input(EAETags.EX_PATTERN_PROVIDER)
                .input(AEItems.COLORED_LUMEN_PAINT_BALL.item(AEColor.BLUE), 6)
                .input(AEItems.ENGINEERING_PROCESSOR)
                .save(c, ExtendedAE.id("assembler/assembler_matrix_pattern"));
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.ASSEMBLER_MATRIX_SPEED)
                .input(EAESingletons.ASSEMBLER_MATRIX_WALL)
                .input(AEItems.SPEED_CARD, 8)
                .input(AEItems.COLORED_LUMEN_PAINT_BALL.item(AEColor.RED), 6)
                .input(EAESingletons.CONCURRENT_PROCESSOR)
                .save(c, ExtendedAE.id("assembler/assembler_matrix_speed"));

        // Void Cell
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAESingletons.VOID_CELL)
                .pattern("CEC")
                .pattern("KXK")
                .pattern("III")
                .define('C', AEBlocks.QUARTZ_GLASS)
                .define('E', AEBlocks.CONDENSER)
                .define('K', AEItems.VOID_CARD)
                .define('X', AEItems.CELL_COMPONENT_16K)
                .define('I', Items.AMETHYST_SHARD)
                .unlockedBy(C, has(AEItems.VOID_CARD))
                .save(c, ExtendedAE.id("void_cell"));

        transformation(c);
        circuit(c);
        assemblerCircuit(c);
        fixer(c);

        if (GlodUtil.checkMod(ModConstants.MEGA)) {
            maga(c);
        }
        if (GlodUtil.checkMod(ModConstants.APPFLUX)) {
            appflux(c);
        }

    }

    private void transformation(@NotNull RecipeOutput c) {
        // Fluix
        CrystalAssemblerRecipeBuilder
                .assemble(AEItems.FLUIX_CRYSTAL, 8)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 4)
                .input(ConventionTags.REDSTONE, 4)
                .input(ConventionTags.NETHER_QUARTZ, 4)
                .fluid(Fluids.WATER, 100)
                .save(c, ExtendedAE.id("assembler/fluix_transformation"));
        // Entro Infused Ingot
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.ENTRO_INGOT, 4)
                .input(EAETags.ENTRO_DUST, 4)
                .input(ConventionTags.GOLD_INGOT, 4)
                .input(Items.LAPIS_LAZULI, 4)
                .fluid(Fluids.WATER, 100)
                .save(c, ExtendedAE.id("assembler/entro_ingot_transformation"));
    }

    private void circuit(@NotNull RecipeOutput c) {
        // Four AE Processors
        CircuitCutterRecipeBuilder
                .cut(AEItems.ENGINEERING_PROCESSOR_PRINT, 9)
                .input(Tags.Items.STORAGE_BLOCKS_DIAMOND)
                .save(c, ExtendedAE.id("cutter/engineering_processor"));
        CircuitCutterRecipeBuilder
                .cut(AEItems.LOGIC_PROCESSOR_PRINT, 9)
                .input(Tags.Items.STORAGE_BLOCKS_GOLD)
                .save(c, ExtendedAE.id("cutter/logic_processor"));
        CircuitCutterRecipeBuilder
                .cut(AEItems.CALCULATION_PROCESSOR_PRINT, 4)
                .input(AEBlocks.QUARTZ_BLOCK)
                .save(c, ExtendedAE.id("cutter/calculation_processor"));
        CircuitCutterRecipeBuilder
                .cut(AEItems.SILICON_PRINT, 9)
                .input(EAETags.SILICON_BLOCK)
                .save(c, ExtendedAE.id("cutter/silicon_print"));

        // Entro Processor
        CircuitCutterRecipeBuilder
                .cut(EAESingletons.CONCURRENT_PROCESSOR_PRINT, 4)
                .input(EAETags.ENTRO_BLOCK)
                .save(c, ExtendedAE.id("cutter/concurrent_processor"));

        // Troll
        CircuitCutterRecipeBuilder
                .cut(Items.PUFFERFISH, 8)
                .input(EAESingletons.FISHBIG)
                .save(c, ExtendedAE.id("cutter/fishbig_destroy"));

    }

    private void appflux(@NotNull RecipeOutput c) {
        // Redstone Crystal
        CrystalAssemblerRecipeBuilder
                .assemble(AFSingletons.REDSTONE_CRYSTAL, 8)
                .input(Tags.Items.STORAGE_BLOCKS_REDSTONE, 4)
                .input(ConventionTags.FLUIX_CRYSTAL, 4)
                .input(ConventionTags.GLOWSTONE, 4)
                .fluid(Fluids.WATER, 100)
                .save(c.withConditions(mod(ModConstants.APPFLUX)), ExtendedAE.id("assembler/redstone_crystal"));
        // Energy Processor
        CircuitCutterRecipeBuilder
                .cut(AFSingletons.ENERGY_PROCESSOR_PRINT, 9)
                .input(AFSingletons.CHARGED_REDSTONE_BLOCK)
                .save(c.withConditions(mod(ModConstants.APPFLUX)), ExtendedAE.id("cutter/energy_processor"));
    }

    private void maga(@NotNull RecipeOutput c) {
        // Sky steel
        CrystalAssemblerRecipeBuilder
                .assemble(MEGAItems.SKY_STEEL_INGOT, 8)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 4)
                .input(ConventionTags.IRON_INGOT, 4)
                .input(AEBlocks.SKY_STONE_BLOCK, 4)
                .fluid(Fluids.LAVA, 100)
                .save(c.withConditions(mod(ModConstants.MEGA)), ExtendedAE.id("assembler/sky_steel"));

        // Sky bronze
        CrystalAssemblerRecipeBuilder
                .assemble(MEGAItems.SKY_BRONZE_INGOT, 8)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 4)
                .input(ConventionTags.COPPER_INGOT, 4)
                .input(AEBlocks.SKY_STONE_BLOCK, 4)
                .fluid(Fluids.LAVA, 100)
                .save(c.withConditions(mod(ModConstants.MEGA)), ExtendedAE.id("assembler/sky_bronze"));

        // Sky osmium
        TagKey<Item> osmium = TagKey.create(Registries.ITEM,(ResourceLocation.fromNamespaceAndPath("c", "ingots/osmium")));
        CrystalAssemblerRecipeBuilder
                .assemble(MEGAItems.SKY_OSMIUM_INGOT, 8)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 4)
                .input(osmium, 4)
                .input(AEBlocks.SKY_STONE_BLOCK, 4)
                .fluid(Fluids.LAVA, 100)
                .save(c.withConditions(mod(ModConstants.MEGA), mod(ModConstants.APPMEK)), ExtendedAE.id("assembler/sky_osmium"));

        // Accumulation Processor
        CircuitCutterRecipeBuilder
                .cut(MEGAItems.ACCUMULATION_PROCESSOR_PRINT, 9)
                .input(MEGABlocks.SKY_STEEL_BLOCK)
                .save(c.withConditions(mod(ModConstants.MEGA)), ExtendedAE.id("cutter/accumulation_processor"));
    }

    private void assemblerCircuit(@NotNull RecipeOutput c) {
        var cond = new ConfigCondition(ConfigCondition.IDs.ASSEMBLER_CIRCUIT);
        CrystalAssemblerRecipeBuilder
                .assemble(AEItems.CALCULATION_PROCESSOR, 4)
                .input(AEItems.CALCULATION_PROCESSOR_PRINT, 4)
                .input(AEItems.SILICON_PRINT, 4)
                .input(ConventionTags.REDSTONE, 4)
                .save(c.withConditions(cond), ExtendedAE.id("assembler/calculation_processor"));
        CrystalAssemblerRecipeBuilder
                .assemble(AEItems.ENGINEERING_PROCESSOR, 4)
                .input(AEItems.ENGINEERING_PROCESSOR_PRINT, 4)
                .input(AEItems.SILICON_PRINT, 4)
                .input(ConventionTags.REDSTONE, 4)
                .save(c.withConditions(cond), ExtendedAE.id("assembler/engineering_processor"));
        CrystalAssemblerRecipeBuilder
                .assemble(AEItems.LOGIC_PROCESSOR, 4)
                .input(AEItems.LOGIC_PROCESSOR_PRINT, 4)
                .input(AEItems.SILICON_PRINT, 4)
                .input(ConventionTags.REDSTONE, 4)
                .save(c.withConditions(cond), ExtendedAE.id("assembler/logic_processor"));
        CrystalAssemblerRecipeBuilder
                .assemble(EAESingletons.CONCURRENT_PROCESSOR, 4)
                .input(EAESingletons.CONCURRENT_PROCESSOR_PRINT, 4)
                .input(AEItems.SILICON_PRINT, 4)
                .input(ConventionTags.REDSTONE, 4)
                .save(c.withConditions(cond), ExtendedAE.id("assembler/concurrent_processor"));
        // AppFlux
        CrystalAssemblerRecipeBuilder
                .assemble(AFSingletons.ENERGY_PROCESSOR, 4)
                .input(AFSingletons.ENERGY_PROCESSOR_PRINT, 4)
                .input(AEItems.SILICON_PRINT, 4)
                .input(ConventionTags.REDSTONE, 4)
                .save(c.withConditions(cond, mod(ModConstants.APPFLUX)), ExtendedAE.id("assembler/energy_processor"));
        // MEGA
        CrystalAssemblerRecipeBuilder
                .assemble(MEGAItems.ACCUMULATION_PROCESSOR, 4)
                .input(MEGAItems.ACCUMULATION_PROCESSOR_PRINT, 4)
                .input(AEItems.SILICON_PRINT, 4)
                .input(ConventionTags.FLUIX_DUST, 4)
                .save(c.withConditions(cond, mod(ModConstants.MEGA)), ExtendedAE.id("assembler/accumulation_processor"));
    }

    private void fixer(@NotNull RecipeOutput c) {
        CrystalFixerRecipeBuilder
                .fixer(AEBlocks.DAMAGED_BUDDING_QUARTZ.block(), AEBlocks.CHIPPED_BUDDING_QUARTZ.block())
                .fuel(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED)
                .chance(0.8)
                .save(c, ExtendedAE.id("fixer/certus_damage"));
        CrystalFixerRecipeBuilder
                .fixer(AEBlocks.CHIPPED_BUDDING_QUARTZ.block(), AEBlocks.FLAWED_BUDDING_QUARTZ.block())
                .fuel(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED)
                .chance(0.8)
                .save(c, ExtendedAE.id("fixer/certus_chipped"));
        CrystalFixerRecipeBuilder
                .fixer(AEBlocks.FLAWED_BUDDING_QUARTZ.block(), AEBlocks.FLAWLESS_BUDDING_QUARTZ.block())
                .fuel(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED)
                .chance(0.05)
                .save(c, ExtendedAE.id("fixer/certus_flawed"));
        CrystalFixerRecipeBuilder
                .fixer(EAESingletons.HARDLY_ENTROIZED_FLUIX_BUDDING, EAESingletons.HALF_ENTROIZED_FLUIX_BUDDING)
                .fuel(ConventionTags.ENDER_PEARL_DUST)
                .chance(0.7)
                .save(c, ExtendedAE.id("fixer/entro_hardly"));
        CrystalFixerRecipeBuilder
                .fixer(EAESingletons.HALF_ENTROIZED_FLUIX_BUDDING, EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING)
                .fuel(ConventionTags.ENDER_PEARL_DUST)
                .chance(0.7)
                .save(c, ExtendedAE.id("fixer/entro_half"));
        CrystalFixerRecipeBuilder
                .fixer(EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING, EAESingletons.FULLY_ENTROIZED_FLUIX_BUDDING)
                .fuel(ConventionTags.ENDER_PEARL_DUST)
                .chance(0.7)
                .save(c, ExtendedAE.id("fixer/entro_mostly"));
    }

    private ICondition mod(String modid) {
        return new ModLoadedCondition(modid);
    }

}
