package com.glodblock.github.extendedae.datagen;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.util.AEColor;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.datagen.providers.tags.ConventionTags;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipeBuilder;
import com.glodblock.github.extendedae.util.EPPTags;
import com.glodblock.github.glodium.datagen.NBTRecipeBuilder;
import gripe._90.appliede.AppliedE;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGATags;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EPPRecipeProvider extends RecipeProvider {

    static String C = "has_item";

    public EPPRecipeProvider(PackOutput p) {
        super(p);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> c) {
        // Extended Pattern Provider
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.EX_PATTERN_PROVIDER)
                .pattern("PC")
                .pattern("CZ")
                .define('P', ConventionTags.PATTERN_PROVIDER)
                .define('C', AEItems.CAPACITY_CARD)
                .define('Z', AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.EX_PATTERN_PROVIDER))
                .save(c, ExtendedAE.id("epp"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EPPItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .requires(EPPItemAndBlock.EX_PATTERN_PROVIDER)
                .unlockedBy(C, has(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART))
                .save(c, ExtendedAE.id("epp_part"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EPPItemAndBlock.EX_PATTERN_PROVIDER)
                .requires(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .unlockedBy(C, has(EPPItemAndBlock.EX_PATTERN_PROVIDER))
                .save(c, ExtendedAE.id("epp_alt"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EPPItemAndBlock.PATTERN_PROVIDER_UPGRADE)
                .requires(AEItems.CAPACITY_CARD, 2)
                .requires(AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.PATTERN_PROVIDER_UPGRADE))
                .save(c, ExtendedAE.id("epp_upgrade"));

        // Extended Interface
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.EX_INTERFACE)
                .pattern("PC")
                .pattern("CZ")
                .define('P', ConventionTags.INTERFACE)
                .define('C', AEItems.CAPACITY_CARD)
                .define('Z', AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.EX_INTERFACE))
                .save(c, ExtendedAE.id("ei"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EPPItemAndBlock.EX_INTERFACE_PART)
                .requires(EPPItemAndBlock.EX_INTERFACE)
                .unlockedBy(C, has(EPPItemAndBlock.EX_INTERFACE_PART))
                .save(c, ExtendedAE.id("ei_part"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EPPItemAndBlock.EX_INTERFACE)
                .requires(EPPItemAndBlock.EX_INTERFACE_PART)
                .unlockedBy(C, has(EPPItemAndBlock.EX_INTERFACE))
                .save(c, ExtendedAE.id("ei_alt"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EPPItemAndBlock.INTERFACE_UPGRADE)
                .requires(AEItems.CAPACITY_CARD, 2)
                .requires(AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.INTERFACE_UPGRADE))
                .save(c, ExtendedAE.id("ei_upgrade"));

        // Infinity Cell
        NBTRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.INFINITY_CELL.getRecordCell(AEFluidKey.of(Fluids.WATER)))
                .pattern("CWC")
                .pattern("WXW")
                .pattern("III")
                .define('C', AEBlocks.QUARTZ_GLASS)
                .define('W', Items.WATER_BUCKET)
                .define('X', AEItems.CELL_COMPONENT_16K)
                .define('I', ConventionTags.DIAMOND)
                .unlockedBy(C, has(EPPItemAndBlock.INFINITY_CELL))
                .save(c, ExtendedAE.id("water_cell"));
        NBTRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.INFINITY_CELL.getRecordCell(AEItemKey.of(Blocks.COBBLESTONE)))
                .pattern("CLC")
                .pattern("WXW")
                .pattern("III")
                .define('C', AEBlocks.QUARTZ_GLASS)
                .define('L', Items.LAVA_BUCKET)
                .define('W', Items.WATER_BUCKET)
                .define('X', AEItems.CELL_COMPONENT_16K)
                .define('I', ConventionTags.DIAMOND)
                .unlockedBy(C, has(EPPItemAndBlock.INFINITY_CELL))
                .save(c, ExtendedAE.id("cobblestone_cell"));

        // Extended IO Bus
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.EX_EXPORT_BUS)
                .pattern("PS")
                .pattern("SZ")
                .define('P', AEParts.EXPORT_BUS)
                .define('S', AEItems.SPEED_CARD)
                .define('Z', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.EX_EXPORT_BUS))
                .save(c, ExtendedAE.id("ebus_out"));
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.EX_IMPORT_BUS)
                .pattern("PS")
                .pattern("SZ")
                .define('P', AEParts.IMPORT_BUS)
                .define('S', AEItems.SPEED_CARD)
                .define('Z', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.EX_IMPORT_BUS))
                .save(c, ExtendedAE.id("ebus_in"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EPPItemAndBlock.IO_BUS_UPGRADE)
                .requires(AEItems.SPEED_CARD, 2)
                .requires(AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.IO_BUS_UPGRADE))
                .save(c, ExtendedAE.id("ebus_upgrade"));

        // Extended Pattern Access Terminal
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.EX_PATTERN_TERMINAL)
                .pattern(" L ")
                .pattern("CPC")
                .pattern(" C ")
                .define('L', Blocks.REDSTONE_LAMP)
                .define('P', AEParts.PATTERN_ACCESS_TERMINAL)
                .define('C', AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.EX_PATTERN_TERMINAL))
                .save(c, ExtendedAE.id("epa"));
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.PATTERN_UPGRADE)
                .pattern(" L ")
                .pattern("CCC")
                .define('L', Blocks.REDSTONE_LAMP)
                .define('C', AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.PATTERN_UPGRADE))
                .save(c, ExtendedAE.id("epa_upgrade"));

        // ME Packing Tape
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.PACKING_TAPE)
                .pattern("FG ")
                .pattern("PIP")
                .pattern(" GF")
                .define('I', ConventionTags.IRON_INGOT)
                .define('P', Items.PAPER)
                .define('G', Items.SLIME_BALL)
                .define('F', ConventionTags.FLUIX_DUST)
                .unlockedBy(C, has(EPPItemAndBlock.PACKING_TAPE))
                .save(c, ExtendedAE.id("tape"));

        // Wireless Connector
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.WIRELESS_CONNECTOR, 2)
                .pattern("DWD")
                .pattern("CEC")
                .pattern("DWD")
                .define('D', AEItems.SKY_DUST)
                .define('W', AEItems.WIRELESS_RECEIVER)
                .define('C', ConventionTags.SMART_CABLE)
                .define('E', AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.WIRELESS_CONNECTOR))
                .save(c, ExtendedAE.id("wireless_connector"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.WIRELESS_TOOL)
                .pattern(" W ")
                .pattern("ICI")
                .pattern(" I ")
                .define('I', ConventionTags.IRON_INGOT)
                .define('W', AEItems.WIRELESS_RECEIVER)
                .define('C', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.WIRELESS_TOOL))
                .save(c, ExtendedAE.id("wireless_tool"));

        // Ingredient Buffer
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.INGREDIENT_BUFFER)
                .pattern("IKI")
                .pattern("G G")
                .pattern("IKI")
                .define('I', ConventionTags.IRON_INGOT)
                .define('K', AEItems.CELL_COMPONENT_1K)
                .define('G', AEBlocks.QUARTZ_GLASS)
                .unlockedBy(C, has(EPPItemAndBlock.INGREDIENT_BUFFER))
                .save(c, ExtendedAE.id("ingredient_buffer"));

        // Extended ME Drive
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.EX_DRIVE)
                .pattern("CDC")
                .pattern("FEF")
                .define('C', ConventionTags.GLASS_CABLE)
                .define('D', AEBlocks.DRIVE)
                .define('F', ConventionTags.FLUIX_DUST)
                .define('E', AEItems.CAPACITY_CARD)
                .unlockedBy(C, has(EPPItemAndBlock.EX_DRIVE))
                .save(c, ExtendedAE.id("ex_drive"));

        // Drive Upgrade
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.DRIVE_UPGRADE)
                .pattern("C C")
                .pattern("FEF")
                .define('C', ConventionTags.GLASS_CABLE)
                .define('F', ConventionTags.FLUIX_DUST)
                .define('E', AEItems.CAPACITY_CARD)
                .unlockedBy(C, has(EPPItemAndBlock.DRIVE_UPGRADE))
                .save(c, ExtendedAE.id("ex_drive_upgrade"));

        // Pattern Modifier
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.PATTERN_MODIFIER)
                .pattern("GPG")
                .pattern(" L ")
                .define('G', ConventionTags.dye(DyeColor.GREEN))
                .define('P', AEItems.BLANK_PATTERN)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.PATTERN_MODIFIER))
                .save(c, ExtendedAE.id("pattern_modifier"));

        // Extended Molecular Assembler
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.EX_ASSEMBLER)
                .pattern("FAF")
                .pattern("AEA")
                .pattern("FAF")
                .define('F', ConventionTags.FLUIX_CRYSTAL)
                .define('A', AEBlocks.MOLECULAR_ASSEMBLER)
                .define('E', AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.EX_ASSEMBLER))
                .save(c, ExtendedAE.id("ex_molecular_assembler"));

        // Extended Inscriber
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.EX_INSCRIBER)
                .pattern("FAF")
                .pattern("AEA")
                .pattern("FAF")
                .define('F', AEBlocks.INSCRIBER)
                .define('A', AEParts.STORAGE_BUS)
                .define('E', ConventionTags.INTERFACE)
                .unlockedBy(C, has(EPPItemAndBlock.EX_INSCRIBER))
                .save(c, ExtendedAE.id("ex_inscriber"));

        // Extended Charger
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.EX_CHARGER)
                .pattern("FAF")
                .pattern("AEA")
                .pattern("FAF")
                .define('F', AEBlocks.CHARGER)
                .define('A', AEParts.STORAGE_BUS)
                .define('E', ConventionTags.INTERFACE)
                .unlockedBy(C, has(EPPItemAndBlock.EX_CHARGER))
                .save(c, ExtendedAE.id("ex_charger"));

        // Tag Storage Bus
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.TAG_STORAGE_BUS)
                .pattern(" L ")
                .pattern("RBR")
                .pattern(" K ")
                .define('L', AEItems.LOGIC_PROCESSOR)
                .define('R', ConventionTags.REDSTONE)
                .define('B', AEParts.STORAGE_BUS)
                .define('K', Items.BOOK)
                .unlockedBy(C, has(EPPItemAndBlock.TAG_STORAGE_BUS))
                .save(c, ExtendedAE.id("tag_storage_bus"));

        // Tag Export Bus
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.TAG_EXPORT_BUS)
                .pattern(" L ")
                .pattern("RBR")
                .pattern(" K ")
                .define('L', AEItems.LOGIC_PROCESSOR)
                .define('R', ConventionTags.REDSTONE)
                .define('B', AEParts.EXPORT_BUS)
                .define('K', Items.BOOK)
                .unlockedBy(C, has(EPPItemAndBlock.TAG_EXPORT_BUS))
                .save(c, ExtendedAE.id("tag_export_bus"));

        // Threshold Level Emitter
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.THRESHOLD_LEVEL_EMITTER)
                .pattern("RER")
                .pattern(" C ")
                .define('R', Items.REDSTONE_TORCH)
                .define('E', AEParts.LEVEL_EMITTER)
                .define('C', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.THRESHOLD_LEVEL_EMITTER))
                .save(c, ExtendedAE.id("threshold_level_emitter"));

        // Mod Storage Bus
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.MOD_STORAGE_BUS)
                .pattern(" C ")
                .pattern("RBR")
                .pattern(" K ")
                .define('C', AEItems.CALCULATION_PROCESSOR)
                .define('R', ConventionTags.REDSTONE)
                .define('B', AEParts.STORAGE_BUS)
                .define('K', Items.BOOK)
                .unlockedBy(C, has(EPPItemAndBlock.MOD_STORAGE_BUS))
                .save(c, ExtendedAE.id("mod_storage_bus"));

        // Mod Export Bus
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.MOD_EXPORT_BUS)
                .pattern(" C ")
                .pattern("RBR")
                .pattern(" K ")
                .define('C', AEItems.CALCULATION_PROCESSOR)
                .define('R', ConventionTags.REDSTONE)
                .define('B', AEParts.EXPORT_BUS)
                .define('K', Items.BOOK)
                .unlockedBy(C, has(EPPItemAndBlock.MOD_EXPORT_BUS))
                .save(c, ExtendedAE.id("mod_export_bus"));

        // Active Formation Plane
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EPPItemAndBlock.ACTIVE_FORMATION_PLANE)
                .requires(AEParts.FORMATION_PLANE)
                .requires(AEParts.EXPORT_BUS)
                .requires(AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.ACTIVE_FORMATION_PLANE))
                .save(c, ExtendedAE.id("active_formation_plane"));

        // ME Caner
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.CANER)
                .pattern("IBE")
                .pattern(" P ")
                .define('I', AEParts.IMPORT_BUS)
                .define('E', AEParts.EXPORT_BUS)
                .define('B', EPPItemAndBlock.INGREDIENT_BUFFER)
                .define('P', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.INGREDIENT_BUFFER))
                .save(c, ExtendedAE.id("caner"));

        // ME Precise Export Bus
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.PRECISE_EXPORT_BUS)
                .pattern("PBP")
                .define('B', EPPItemAndBlock.EX_EXPORT_BUS)
                .define('P', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.EX_EXPORT_BUS))
                .save(c, ExtendedAE.id("pre_bus"));

        // Wireless Ex PAT
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.WIRELESS_EX_PAT)
                .pattern("A")
                .pattern("B")
                .pattern("C")
                .define('A', AEItems.WIRELESS_RECEIVER)
                .define('B', EPPItemAndBlock.EX_PATTERN_TERMINAL)
                .define('C', AEBlocks.DENSE_ENERGY_CELL)
                .unlockedBy(C, has(EPPItemAndBlock.EX_PATTERN_TERMINAL))
                .save(c, ExtendedAE.id("wireless_ex_pat"));

        // Extended IO Port
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.EX_IO_PORT)
                .pattern("ACB")
                .pattern("CMC")
                .pattern("BCA")
                .define('A', AEItems.LOGIC_PROCESSOR)
                .define('B', AEItems.ENGINEERING_PROCESSOR)
                .define('C', AEItems.SPEED_CARD)
                .define('M', AEBlocks.IO_PORT)
                .unlockedBy(C, has(AEBlocks.IO_PORT))
                .save(c, ExtendedAE.id("ex_io_port"));

        // Crystal Fixer
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.CRYSTAL_FIXER)
                .pattern("C C")
                .pattern("I I")
                .pattern("IFI")
                .define('C', ConventionTags.CERTUS_QUARTZ)
                .define('I', ConventionTags.IRON_INGOT)
                .define('F', ConventionTags.FLUIX_CRYSTAL)
                .unlockedBy(C, has(AEItems.FLUIX_CRYSTAL))
                .save(c, ExtendedAE.id("crystal_fixer"));

        // Precise Storage Bus
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.PRECISE_STORAGE_BUS)
                .pattern("PBP")
                .define('B', AEParts.STORAGE_BUS)
                .define('P', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(AEParts.STORAGE_BUS))
                .save(c, ExtendedAE.id("precise_storage_bus"));

        // Threshold Export Bus
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.THRESHOLD_EXPORT_BUS)
                .pattern("LCE")
                .define('L', AEParts.LEVEL_EMITTER)
                .define('C', AEItems.LOGIC_PROCESSOR)
                .define('E', AEParts.EXPORT_BUS)
                .unlockedBy(C, has(AEParts.LEVEL_EMITTER))
                .save(c, ExtendedAE.id("threshold_export_bus"));

        // Assembler Matrix
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.ASSEMBLER_MATRIX_FRAME)
                .pattern("BIB")
                .pattern("IGI")
                .pattern("BIB")
                .define('B', Items.LAPIS_LAZULI)
                .define('I', ConventionTags.IRON_INGOT)
                .define('G', AEBlocks.QUARTZ_GLASS)
                .unlockedBy(C, has(AEBlocks.QUARTZ_GLASS))
                .save(c, ExtendedAE.id("assembler_matrix_frame"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.ASSEMBLER_MATRIX_WALL)
                .pattern("BIB")
                .pattern("IGI")
                .pattern("BIB")
                .define('B', ConventionTags.SMART_CABLE)
                .define('I', ConventionTags.NETHER_QUARTZ)
                .define('G', AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(AEItems.LOGIC_PROCESSOR))
                .save(c, ExtendedAE.id("assembler_matrix_wall"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.ASSEMBLER_MATRIX_SPEED, 2)
                .pattern("BIB")
                .pattern("BGB")
                .pattern("BIB")
                .define('B', AEItems.COLORED_LUMEN_PAINT_BALL.item(AEColor.RED))
                .define('I', AEItems.SPEED_CARD)
                .define('G', EPPItemAndBlock.ASSEMBLER_MATRIX_WALL)
                .unlockedBy(C, has(EPPItemAndBlock.ASSEMBLER_MATRIX_WALL))
                .save(c, ExtendedAE.id("assembler_matrix_speed"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.ASSEMBLER_MATRIX_CRAFTER, 2)
                .pattern("BIB")
                .pattern("BGB")
                .pattern("BIB")
                .define('B', AEItems.COLORED_LUMEN_PAINT_BALL.item(AEColor.PURPLE))
                .define('I', EPPItemAndBlock.EX_ASSEMBLER)
                .define('G', EPPItemAndBlock.ASSEMBLER_MATRIX_WALL)
                .unlockedBy(C, has(EPPItemAndBlock.ASSEMBLER_MATRIX_WALL))
                .save(c, ExtendedAE.id("assembler_matrix_crafter"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.ASSEMBLER_MATRIX_PATTERN, 2)
                .pattern("BIB")
                .pattern("BGB")
                .pattern("BIB")
                .define('B', AEItems.COLORED_LUMEN_PAINT_BALL.item(AEColor.BLUE))
                .define('I', EPPTags.EX_PATTERN_PROVIDER)
                .define('G', EPPItemAndBlock.ASSEMBLER_MATRIX_WALL)
                .unlockedBy(C, has(EPPItemAndBlock.ASSEMBLER_MATRIX_WALL))
                .save(c, ExtendedAE.id("assembler_matrix_pattern"));

        // Silicon Block
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.SILICON_BLOCK)
                .pattern("###")
                .pattern("#S#")
                .pattern("###")
                .define('#', ConventionTags.SILICON)
                .define('S', AEItems.SILICON)
                .unlockedBy(C, has(AEItems.SILICON))
                .save(c, ExtendedAE.id("silicon_block"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, AEItems.SILICON, 9)
                .requires(EPPItemAndBlock.SILICON_BLOCK)
                .unlockedBy(C, has(EPPItemAndBlock.SILICON_BLOCK))
                .save(c, ExtendedAE.id("silicon_block_disassembler"));

        // Circuit Slicer
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.CIRCUIT_CUTTER)
                .pattern("IGI")
                .pattern("1C2")
                .pattern("3T4")
                .define('I', ConventionTags.IRON_INGOT)
                .define('G', AEBlocks.QUARTZ_GLASS)
                .define('C', Blocks.STONECUTTER)
                .define('T', AEBlocks.SKY_STONE_TANK)
                .define('1', AEItems.CALCULATION_PROCESSOR_PRESS)
                .define('2', AEItems.ENGINEERING_PROCESSOR_PRESS)
                .define('3', AEItems.LOGIC_PROCESSOR_PRESS)
                .define('4', AEItems.SILICON_PRESS)
                .unlockedBy(C, has(Blocks.STONECUTTER))
                .save(c, ExtendedAE.id("circuit_cutter"));
        cutter(c);

        // Fishbig
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.FISHBIG)
                .pattern("FFF")
                .pattern("F F")
                .pattern("FFF")
                .define('F', Items.PUFFERFISH)
                .unlockedBy(C, has(EPPItemAndBlock.FISHBIG))
                .save(c, ExtendedAE.id("fishbig"));

        // Oversize Interface
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.OVERSIZE_INTERFACE)
                .pattern("ABF")
                .pattern("LCE")
                .pattern("AIF")
                .define('A', AEItems.ANNIHILATION_CORE)
                .define('B', EPPItemAndBlock.INGREDIENT_BUFFER)
                .define('F', AEItems.FORMATION_CORE)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .define('C', AEItems.CALCULATION_PROCESSOR)
                .define('E', AEItems.ENGINEERING_PROCESSOR)
                .define('I', EPPTags.EX_INTERFACE)
                .unlockedBy(C, has(EPPItemAndBlock.OVERSIZE_INTERFACE))
                .save(c, ExtendedAE.id("oversize_interface"));

        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EPPItemAndBlock.OVERSIZE_INTERFACE_PART)
                .requires(EPPItemAndBlock.OVERSIZE_INTERFACE)
                .unlockedBy(C, has(EPPItemAndBlock.OVERSIZE_INTERFACE))
                .save(c, ExtendedAE.id("oversize_interface_part"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EPPItemAndBlock.OVERSIZE_INTERFACE)
                .requires(EPPItemAndBlock.OVERSIZE_INTERFACE_PART)
                .unlockedBy(C, has(EPPItemAndBlock.OVERSIZE_INTERFACE_PART))
                .save(c, ExtendedAE.id("oversize_interface_alt"));

        if (ModList.get().isLoaded("appliede")) {
            var interfaceTag = TagKey.create(Registries.ITEM, new ResourceLocation("appliede", "emc_interface"));
            // Extended EMC Interface
            ConditionalRecipe.builder()
                    .addCondition(new ModLoadedCondition("appliede"))
                    .addRecipe(
                            ShapedRecipeBuilder
                                    .shaped(RecipeCategory.MISC, EPPItemAndBlock.EX_EMC_INTERFACE)
                                    .pattern("PC")
                                    .pattern("CZ")
                                    .define('P', interfaceTag)
                                    .define('C', AEItems.CAPACITY_CARD)
                                    .define('Z', PEItems.RED_MATTER)
                                    .unlockedBy(C, has(EPPItemAndBlock.EX_EMC_INTERFACE))::save)
                    .build(c, ExtendedAE.id("ex_emc_interface"));
            ConditionalRecipe.builder()
                    .addCondition(new ModLoadedCondition("appliede"))
                    .addRecipe(
                            ShapelessRecipeBuilder
                                    .shapeless(RecipeCategory.MISC, EPPItemAndBlock.EX_EMC_INTERFACE_PART)
                                    .requires(EPPItemAndBlock.EX_EMC_INTERFACE)
                                    .unlockedBy(C, has(EPPItemAndBlock.EX_EMC_INTERFACE_PART))::save)
                    .build(c, ExtendedAE.id("ex_emc_interface_part"));
            ConditionalRecipe.builder()
                    .addCondition(new ModLoadedCondition("appliede"))
                    .addRecipe(
                            ShapelessRecipeBuilder
                                    .shapeless(RecipeCategory.MISC, EPPItemAndBlock.EX_EMC_INTERFACE)
                                    .requires(EPPItemAndBlock.EX_EMC_INTERFACE_PART)
                                    .unlockedBy(C, has(EPPItemAndBlock.EX_EMC_INTERFACE))::save)
                    .build(c, ExtendedAE.id("ex_emc_interface_alt"));
            ConditionalRecipe.builder()
                    .addCondition(new ModLoadedCondition("appliede"))
                    .addRecipe(
                            ShapelessRecipeBuilder
                                    .shapeless(RecipeCategory.MISC, EPPItemAndBlock.EMC_INTERFACE_UPGRADE)
                                    .requires(AEItems.CAPACITY_CARD, 2)
                                    .requires(PEItems.RED_MATTER)
                                    .unlockedBy(C, has(EPPItemAndBlock.EMC_INTERFACE_UPGRADE))::save)
                    .build(c, ExtendedAE.id("ex_emc_interface_upgrade"));

            // Extended EMC IO Bus
            ConditionalRecipe.builder()
                    .addCondition(new ModLoadedCondition("appliede"))
                    .addRecipe(
                            ShapedRecipeBuilder
                                    .shaped(RecipeCategory.MISC, EPPItemAndBlock.EX_EMC_EXPORT_BUS)
                                    .pattern("PC")
                                    .pattern("CZ")
                                    .define('P', AppliedE.EMC_EXPORT_BUS.get())
                                    .define('C', AEItems.SPEED_CARD)
                                    .define('Z', PEItems.RED_MATTER)
                                    .unlockedBy(C, has(EPPItemAndBlock.EX_EMC_EXPORT_BUS))::save)
                    .build(c, ExtendedAE.id("ex_emc_export_bus"));
            ConditionalRecipe.builder()
                    .addCondition(new ModLoadedCondition("appliede"))
                    .addRecipe(
                            ShapedRecipeBuilder
                                    .shaped(RecipeCategory.MISC, EPPItemAndBlock.EX_EMC_IMPORT_BUS)
                                    .pattern("PC")
                                    .pattern("CZ")
                                    .define('P', AppliedE.EMC_IMPORT_BUS.get())
                                    .define('C', AEItems.SPEED_CARD)
                                    .define('Z', PEItems.RED_MATTER)
                                    .unlockedBy(C, has(EPPItemAndBlock.EX_EMC_IMPORT_BUS))::save)
                    .build(c, ExtendedAE.id("ex_emc_import_bus"));
            ConditionalRecipe.builder()
                    .addCondition(new ModLoadedCondition("appliede"))
                    .addRecipe(
                            ShapelessRecipeBuilder
                                    .shapeless(RecipeCategory.MISC, EPPItemAndBlock.EMC_IO_BUS_UPGRADE)
                                    .requires(AEItems.SPEED_CARD, 2)
                                    .requires(PEItems.RED_MATTER)
                                    .unlockedBy(C, has(EPPItemAndBlock.EMC_IO_BUS_UPGRADE))::save)
                    .build(c, ExtendedAE.id("ex_emc_import_bus_upgrade"));
        }

    }

    private void cutter(@NotNull Consumer<FinishedRecipe> c) {
        CircuitCutterRecipeBuilder.cut(AEItems.CALCULATION_PROCESSOR_PRINT, 4)
                .input(EPPTags.CERTUS_QUARTZ_STORAGE_BLOCK)
                .fluid(Fluids.WATER, 100)
                .save(c, ExtendedAE.id("cutter/calculation"));
        CircuitCutterRecipeBuilder.cut(AEItems.LOGIC_PROCESSOR_PRINT, 9)
                .input(Tags.Items.STORAGE_BLOCKS_GOLD)
                .fluid(Fluids.WATER, 100)
                .save(c, ExtendedAE.id("cutter/logic"));
        CircuitCutterRecipeBuilder.cut(AEItems.ENGINEERING_PROCESSOR_PRINT, 9)
                .input(Tags.Items.STORAGE_BLOCKS_DIAMOND)
                .fluid(Fluids.WATER, 100)
                .save(c, ExtendedAE.id("cutter/engineering"));
        CircuitCutterRecipeBuilder.cut(AEItems.SILICON_PRINT, 9)
                .input(EPPTags.SILICON_BLOCK)
                .fluid(Fluids.WATER, 100)
                .save(c, ExtendedAE.id("cutter/silicon"));
        if (ModList.get().isLoaded("megacells")) {
            ConditionalRecipe.builder()
                    .addCondition(new ModLoadedCondition("megacells"))
                    .addRecipe(
                            CircuitCutterRecipeBuilder.cut(MEGAItems.ACCUMULATION_PROCESSOR_PRINT, 9)
                                    .input(MEGATags.SKY_STEEL_BLOCK_ITEM)
                                    .fluid(Fluids.LAVA, 100)::save)
                    .build(c, ExtendedAE.id("cutter/accumulation"));
        }
    }

}
