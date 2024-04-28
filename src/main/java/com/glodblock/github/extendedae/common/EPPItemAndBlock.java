package com.glodblock.github.extendedae.common;

import appeng.items.parts.PartItem;
import appeng.items.tools.powered.WirelessTerminalItem;
import com.glodblock.github.extendedae.common.blocks.BlockCaner;
import com.glodblock.github.extendedae.common.blocks.BlockCrystalFixer;
import com.glodblock.github.extendedae.common.blocks.BlockExCharger;
import com.glodblock.github.extendedae.common.blocks.BlockExDrive;
import com.glodblock.github.extendedae.common.blocks.BlockExIOPort;
import com.glodblock.github.extendedae.common.blocks.BlockExInscriber;
import com.glodblock.github.extendedae.common.blocks.BlockExInterface;
import com.glodblock.github.extendedae.common.blocks.BlockExMolecularAssembler;
import com.glodblock.github.extendedae.common.blocks.BlockExPatternProvider;
import com.glodblock.github.extendedae.common.blocks.BlockFishbig;
import com.glodblock.github.extendedae.common.blocks.BlockIngredientBuffer;
import com.glodblock.github.extendedae.common.blocks.BlockWirelessConnector;
import com.glodblock.github.extendedae.common.items.InfinityCell;
import com.glodblock.github.extendedae.common.items.ItemDriveUpgrade;
import com.glodblock.github.extendedae.common.items.ItemIOBusUpgrade;
import com.glodblock.github.extendedae.common.items.ItemInterfaceUpgrade;
import com.glodblock.github.extendedae.common.items.ItemMEPackingTape;
import com.glodblock.github.extendedae.common.items.ItemPackedDevice;
import com.glodblock.github.extendedae.common.items.ItemPatternAccessTerminalUpgrade;
import com.glodblock.github.extendedae.common.items.ItemPatternModifier;
import com.glodblock.github.extendedae.common.items.ItemPatternProviderUpgrade;
import com.glodblock.github.extendedae.common.items.ItemWirelessConnectTool;
import com.glodblock.github.extendedae.common.items.tools.ItemWirelessExPAT;
import com.glodblock.github.extendedae.common.parts.PartActiveFormationPlane;
import com.glodblock.github.extendedae.common.parts.PartExExportBus;
import com.glodblock.github.extendedae.common.parts.PartExImportBus;
import com.glodblock.github.extendedae.common.parts.PartExInterface;
import com.glodblock.github.extendedae.common.parts.PartExPatternAccessTerminal;
import com.glodblock.github.extendedae.common.parts.PartExPatternProvider;
import com.glodblock.github.extendedae.common.parts.PartModExportBus;
import com.glodblock.github.extendedae.common.parts.PartModStorageBus;
import com.glodblock.github.extendedae.common.parts.PartPreciseExportBus;
import com.glodblock.github.extendedae.common.parts.PartPreciseStorageBus;
import com.glodblock.github.extendedae.common.parts.PartTagExportBus;
import com.glodblock.github.extendedae.common.parts.PartTagStorageBus;
import com.glodblock.github.extendedae.common.parts.PartThresholdExportBus;
import com.glodblock.github.extendedae.common.parts.PartThresholdLevelEmitter;
import com.glodblock.github.extendedae.common.tileentities.TileCaner;
import com.glodblock.github.extendedae.common.tileentities.TileCrystalFixer;
import com.glodblock.github.extendedae.common.tileentities.TileExCharger;
import com.glodblock.github.extendedae.common.tileentities.TileExDrive;
import com.glodblock.github.extendedae.common.tileentities.TileExIOPort;
import com.glodblock.github.extendedae.common.tileentities.TileExInscriber;
import com.glodblock.github.extendedae.common.tileentities.TileExInterface;
import com.glodblock.github.extendedae.common.tileentities.TileExMolecularAssembler;
import com.glodblock.github.extendedae.common.tileentities.TileExPatternProvider;
import com.glodblock.github.extendedae.common.tileentities.TileIngredientBuffer;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessConnector;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;

public class EPPItemAndBlock {

    public static BlockExPatternProvider EX_PATTERN_PROVIDER;
    public static PartItem<PartExPatternProvider> EX_PATTERN_PROVIDER_PART;
    public static BlockExInterface EX_INTERFACE;
    public static PartItem<PartExInterface> EX_INTERFACE_PART;
    public static InfinityCell INFINITY_CELL;
    public static PartItem<PartExExportBus> EX_EXPORT_BUS;
    public static PartItem<PartExImportBus> EX_IMPORT_BUS;
    public static PartItem<PartExPatternAccessTerminal> EX_PATTERN_TERMINAL;
    public static ItemMEPackingTape PACKING_TAPE;
    public static ItemPackedDevice PACKAGE;
    public static ItemPatternProviderUpgrade PATTERN_PROVIDER_UPGRADE;
    public static ItemInterfaceUpgrade INTERFACE_UPGRADE;
    public static ItemIOBusUpgrade IO_BUS_UPGRADE;
    public static ItemPatternAccessTerminalUpgrade PATTERN_UPGRADE;
    public static BlockWirelessConnector WIRELESS_CONNECTOR;
    public static ItemWirelessConnectTool WIRELESS_TOOL;
    public static BlockIngredientBuffer INGREDIENT_BUFFER;
    public static BlockExDrive EX_DRIVE;
    public static ItemDriveUpgrade DRIVE_UPGRADE;
    public static ItemPatternModifier PATTERN_MODIFIER;
    public static BlockExMolecularAssembler EX_ASSEMBLER;
    public static BlockExInscriber EX_INSCRIBER;
    public static BlockExCharger EX_CHARGER;
    public static BlockCrystalFixer CRYSTAL_FIXER;
    public static PartItem<PartTagStorageBus> TAG_STORAGE_BUS;
    public static PartItem<PartTagExportBus> TAG_EXPORT_BUS;
    public static PartItem<PartThresholdLevelEmitter> THRESHOLD_LEVEL_EMITTER;
    public static PartItem<PartModStorageBus> MOD_STORAGE_BUS;
    public static PartItem<PartModExportBus> MOD_EXPORT_BUS;
    public static PartItem<PartActiveFormationPlane> ACTIVE_FORMATION_PLANE;
    public static BlockFishbig FISHBIG;
    public static BlockCaner CANER;
    public static BlockExIOPort EX_IO_PORT;
    public static PartItem<PartPreciseExportBus> PRECISE_EXPORT_BUS;
    public static WirelessTerminalItem WIRELESS_EX_PAT;
    public static PartItem<PartPreciseStorageBus> PRECISE_STORAGE_BUS;
    public static PartItem<PartThresholdExportBus> THRESHOLD_EXPORT_BUS;

    public static void init(EAERegistryHandler regHandler) {
        EX_PATTERN_PROVIDER = new BlockExPatternProvider();
        EX_PATTERN_PROVIDER_PART = new PartItem<>(new Item.Properties(), PartExPatternProvider.class, PartExPatternProvider::new);
        EX_INTERFACE = new BlockExInterface();
        EX_INTERFACE_PART = new PartItem<>(new Item.Properties(), PartExInterface.class, PartExInterface::new);
        INFINITY_CELL = new InfinityCell();
        EX_EXPORT_BUS = new PartItem<>(new Item.Properties(), PartExExportBus.class, PartExExportBus::new);
        EX_IMPORT_BUS = new PartItem<>(new Item.Properties(), PartExImportBus.class, PartExImportBus::new);
        EX_PATTERN_TERMINAL = new PartItem<>(new Item.Properties(), PartExPatternAccessTerminal.class, PartExPatternAccessTerminal::new);
        PATTERN_PROVIDER_UPGRADE = new ItemPatternProviderUpgrade();
        INTERFACE_UPGRADE = new ItemInterfaceUpgrade();
        IO_BUS_UPGRADE = new ItemIOBusUpgrade();
        PATTERN_UPGRADE = new ItemPatternAccessTerminalUpgrade();
        PACKING_TAPE = new ItemMEPackingTape();
        PACKAGE = new ItemPackedDevice();
        WIRELESS_CONNECTOR = new BlockWirelessConnector();
        WIRELESS_TOOL = new ItemWirelessConnectTool();
        INGREDIENT_BUFFER = new BlockIngredientBuffer();
        EX_DRIVE = new BlockExDrive();
        DRIVE_UPGRADE = new ItemDriveUpgrade();
        PATTERN_MODIFIER = new ItemPatternModifier();
        EX_ASSEMBLER = new BlockExMolecularAssembler();
        EX_INSCRIBER = new BlockExInscriber();
        EX_CHARGER = new BlockExCharger();
        CRYSTAL_FIXER = new BlockCrystalFixer();
        TAG_STORAGE_BUS = new PartItem<>(new Item.Properties(), PartTagStorageBus.class, PartTagStorageBus::new);
        TAG_EXPORT_BUS = new PartItem<>(new Item.Properties(), PartTagExportBus.class, PartTagExportBus::new);
        THRESHOLD_LEVEL_EMITTER = new PartItem<>(new Item.Properties(), PartThresholdLevelEmitter.class, PartThresholdLevelEmitter::new);
        MOD_STORAGE_BUS = new PartItem<>(new Item.Properties(), PartModStorageBus.class, PartModStorageBus::new);
        MOD_EXPORT_BUS = new PartItem<>(new Item.Properties(), PartModExportBus.class, PartModExportBus::new);
        ACTIVE_FORMATION_PLANE = new PartItem<>(new Item.Properties(), PartActiveFormationPlane.class, PartActiveFormationPlane::new);
        CANER = new BlockCaner();
        EX_IO_PORT = new BlockExIOPort();
        FISHBIG = new BlockFishbig();
        PRECISE_EXPORT_BUS = new PartItem<>(new Item.Properties(), PartPreciseExportBus.class, PartPreciseExportBus::new);
        PRECISE_STORAGE_BUS = new PartItem<>(new Item.Properties(), PartPreciseStorageBus.class, PartPreciseStorageBus::new);
        THRESHOLD_EXPORT_BUS = new PartItem<>(new Item.Properties(), PartThresholdExportBus.class, PartThresholdExportBus::new);
        if (ModList.get().isLoaded("ae2wtlib")) {
            try {
                //To prevent classloader issue
                WIRELESS_EX_PAT = (WirelessTerminalItem) Class.forName("com.glodblock.github.extendedae.xmod.wt.ItemUWirelessExPAT")
                        .getDeclaredConstructor()
                        .newInstance();
            } catch (Exception e) {
                WIRELESS_EX_PAT = new ItemWirelessExPAT();
            }
        } else {
            WIRELESS_EX_PAT = new ItemWirelessExPAT();
        }
        regHandler.block("ex_pattern_provider", EX_PATTERN_PROVIDER, TileExPatternProvider.class, TileExPatternProvider::new);
        regHandler.block("ex_interface", EX_INTERFACE, TileExInterface.class, TileExInterface::new);
        regHandler.block("wireless_connect", WIRELESS_CONNECTOR, TileWirelessConnector.class, TileWirelessConnector::new);
        regHandler.block("ingredient_buffer", INGREDIENT_BUFFER, TileIngredientBuffer.class, TileIngredientBuffer::new);
        regHandler.block("ex_drive", EX_DRIVE, TileExDrive.class, TileExDrive::new);
        regHandler.block("ex_molecular_assembler", EX_ASSEMBLER, TileExMolecularAssembler.class, TileExMolecularAssembler::new);
        regHandler.block("ex_inscriber", EX_INSCRIBER, TileExInscriber.class, TileExInscriber::new);
        regHandler.block("ex_charger", EX_CHARGER, TileExCharger.class, TileExCharger::new);
        regHandler.block("crystal_fixer", CRYSTAL_FIXER, TileCrystalFixer.class, TileCrystalFixer::new);
        regHandler.block("caner", CANER, TileCaner.class, TileCaner::new);
        regHandler.block("ex_io_port", EX_IO_PORT, TileExIOPort.class, TileExIOPort::new);
        regHandler.block("fishbig", FISHBIG);
        regHandler.item("ex_pattern_provider_part", EX_PATTERN_PROVIDER_PART);
        regHandler.item("ex_interface_part", EX_INTERFACE_PART);
        regHandler.item("infinity_cell", INFINITY_CELL);
        regHandler.item("ex_export_bus_part", EX_EXPORT_BUS);
        regHandler.item("ex_import_bus_part", EX_IMPORT_BUS);
        regHandler.item("ex_pattern_access_part", EX_PATTERN_TERMINAL);
        regHandler.item("pattern_provider_upgrade", PATTERN_PROVIDER_UPGRADE);
        regHandler.item("interface_upgrade", INTERFACE_UPGRADE);
        regHandler.item("io_bus_upgrade", IO_BUS_UPGRADE);
        regHandler.item("pattern_terminal_upgrade", PATTERN_UPGRADE);
        regHandler.item("me_packing_tape", PACKING_TAPE);
        regHandler.item("package", PACKAGE);
        regHandler.item("wireless_tool", WIRELESS_TOOL);
        regHandler.item("drive_upgrade", DRIVE_UPGRADE);
        regHandler.item("pattern_modifier", PATTERN_MODIFIER);
        regHandler.item("tag_storage_bus", TAG_STORAGE_BUS);
        regHandler.item("tag_export_bus", TAG_EXPORT_BUS);
        regHandler.item("threshold_level_emitter", THRESHOLD_LEVEL_EMITTER);
        regHandler.item("mod_storage_bus", MOD_STORAGE_BUS);
        regHandler.item("mod_export_bus", MOD_EXPORT_BUS);
        regHandler.item("active_formation_plane", ACTIVE_FORMATION_PLANE);
        regHandler.item("precise_export_bus", PRECISE_EXPORT_BUS);
        regHandler.item("wireless_ex_pat", WIRELESS_EX_PAT);
        regHandler.item("precise_storage_bus", PRECISE_STORAGE_BUS);
        regHandler.item("threshold_export_bus", THRESHOLD_EXPORT_BUS);
    }

}
