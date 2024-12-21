package com.glodblock.github.extendedae.common;

import appeng.api.client.StorageCellModels;
import appeng.api.features.GridLinkables;
import appeng.api.parts.PartModels;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.block.AEBaseBlockItem;
import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.hotkeys.HotkeyActions;
import appeng.hotkeys.InventoryHotkeyAction;
import appeng.items.AEBaseItem;
import appeng.items.tools.powered.WirelessTerminalItem;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.inventory.InfinityCellInventory;
import com.glodblock.github.extendedae.common.items.ItemMEPackingTape;
import com.glodblock.github.extendedae.common.parts.PartActiveFormationPlane;
import com.glodblock.github.extendedae.common.parts.PartExExportBus;
import com.glodblock.github.extendedae.common.parts.PartExImportBus;
import com.glodblock.github.extendedae.common.parts.PartExInterface;
import com.glodblock.github.extendedae.common.parts.PartExPatternAccessTerminal;
import com.glodblock.github.extendedae.common.parts.PartExPatternProvider;
import com.glodblock.github.extendedae.common.parts.PartModExportBus;
import com.glodblock.github.extendedae.common.parts.PartModStorageBus;
import com.glodblock.github.extendedae.common.parts.PartOversizeInterface;
import com.glodblock.github.extendedae.common.parts.PartPreciseExportBus;
import com.glodblock.github.extendedae.common.parts.PartPreciseStorageBus;
import com.glodblock.github.extendedae.common.parts.PartTagExportBus;
import com.glodblock.github.extendedae.common.parts.PartTagStorageBus;
import com.glodblock.github.extendedae.common.parts.PartThresholdExportBus;
import com.glodblock.github.extendedae.common.parts.PartThresholdLevelEmitter;
import com.glodblock.github.extendedae.config.EPPConfig;
import com.glodblock.github.extendedae.container.ContainerActiveFormationPlane;
import com.glodblock.github.extendedae.container.ContainerAssemblerMatrix;
import com.glodblock.github.extendedae.container.ContainerCaner;
import com.glodblock.github.extendedae.container.ContainerCircuitCutter;
import com.glodblock.github.extendedae.container.ContainerExDrive;
import com.glodblock.github.extendedae.container.ContainerExIOBus;
import com.glodblock.github.extendedae.container.ContainerExIOPort;
import com.glodblock.github.extendedae.container.ContainerExInscriber;
import com.glodblock.github.extendedae.container.ContainerExInterface;
import com.glodblock.github.extendedae.container.ContainerExMolecularAssembler;
import com.glodblock.github.extendedae.container.ContainerExPatternProvider;
import com.glodblock.github.extendedae.container.ContainerExPatternTerminal;
import com.glodblock.github.extendedae.container.ContainerIngredientBuffer;
import com.glodblock.github.extendedae.container.ContainerModExportBus;
import com.glodblock.github.extendedae.container.ContainerModStorageBus;
import com.glodblock.github.extendedae.container.ContainerPatternModifier;
import com.glodblock.github.extendedae.container.ContainerPreciseExportBus;
import com.glodblock.github.extendedae.container.ContainerPreciseStorageBus;
import com.glodblock.github.extendedae.container.ContainerRenamer;
import com.glodblock.github.extendedae.container.ContainerTagExportBus;
import com.glodblock.github.extendedae.container.ContainerTagStorageBus;
import com.glodblock.github.extendedae.container.ContainerThresholdExportBus;
import com.glodblock.github.extendedae.container.ContainerThresholdLevelEmitter;
import com.glodblock.github.extendedae.container.ContainerWirelessConnector;
import com.glodblock.github.extendedae.container.ContainerWirelessExPAT;
import com.glodblock.github.extendedae.container.pattern.ContainerCraftingPattern;
import com.glodblock.github.extendedae.container.pattern.ContainerProcessingPattern;
import com.glodblock.github.extendedae.container.pattern.ContainerSmithingTablePattern;
import com.glodblock.github.extendedae.container.pattern.ContainerStonecuttingPattern;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipe;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipeSerializer;
import com.glodblock.github.extendedae.xmod.appflux.AFCommonLoad;
import com.glodblock.github.extendedae.xmod.appliede.APECommonLoad;
import com.glodblock.github.extendedae.xmod.wt.WTCommonLoad;
import com.glodblock.github.glodium.registry.RegistryHandler;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;

import static appeng.api.features.HotkeyAction.WIRELESS_TERMINAL;

public class EAERegistryHandler extends RegistryHandler {

    public static final EAERegistryHandler INSTANCE = new EAERegistryHandler();

    public EAERegistryHandler() {
        super(ExtendedAE.MODID);
    }

    public <T extends AEBaseBlockEntity> void block(String name, AEBaseEntityBlock<T> block, Class<T> clazz, BlockEntityType.BlockEntitySupplier<? extends T> supplier) {
        bindTileEntity(clazz, block, supplier);
        block(name, block, b -> new AEBaseBlockItem(b, new Item.Properties()));
        tile(name, block.getBlockEntityType());
    }

    @Override
    public void register(RegisterEvent event) {
        super.register(event);
        this.onRegisterContainer();
        this.onRegisterModels();
        this.onRegisterRecipe();
    }

    public Collection<Block> getBlocks() {
        return this.blocks.stream().map(Pair::getRight).toList();
    }

    private void onRegisterRecipe() {
        ForgeRegistries.RECIPE_TYPES.register(CircuitCutterRecipe.TYPE_ID, CircuitCutterRecipe.TYPE);
        ForgeRegistries.RECIPE_SERIALIZERS.register(CircuitCutterRecipe.TYPE_ID, CircuitCutterRecipeSerializer.INSTANCE);
    }

    private void onRegisterContainer() {
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_pattern_provider"), ContainerExPatternProvider.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_interface"), ContainerExInterface.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_export_bus"), ContainerExIOBus.EXPORT_TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_import_bus"), ContainerExIOBus.IMPORT_TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_pattern_access_terminal"), ContainerExPatternTerminal.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("wireless_connector"), ContainerWirelessConnector.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ingredient_buffer"), ContainerIngredientBuffer.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_drive"), ContainerExDrive.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("pattern_modifier"), ContainerPatternModifier.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_molecular_assembler"), ContainerExMolecularAssembler.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_inscriber"), ContainerExInscriber.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("tag_storage_bus"), ContainerTagStorageBus.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("tag_export_bus"), ContainerTagExportBus.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("threshold_level_emitter"), ContainerThresholdLevelEmitter.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("renamer"), ContainerRenamer.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("mod_storage_bus"), ContainerModStorageBus.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("mod_export_bus"), ContainerModExportBus.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("active_formation_plane"), ContainerActiveFormationPlane.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("caner"), ContainerCaner.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("precise_export_bus"), ContainerPreciseExportBus.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("wireless_ex_pat"), ContainerWirelessExPAT.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_ioport"), ContainerExIOPort.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("precise_storage_bus"), ContainerPreciseStorageBus.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("threshold_export_bus"), ContainerThresholdExportBus.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("oversize_interface"), ContainerExInterface.TYPE_OVERSIZE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("assembler_matrix"), ContainerAssemblerMatrix.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("circuit_cutter"), ContainerCircuitCutter.TYPE);
        ForgeRegistries.MENU_TYPES.register(ContainerProcessingPattern.ID, ContainerProcessingPattern.TYPE);
        ForgeRegistries.MENU_TYPES.register(ContainerCraftingPattern.ID, ContainerCraftingPattern.TYPE);
        ForgeRegistries.MENU_TYPES.register(ContainerStonecuttingPattern.ID, ContainerStonecuttingPattern.TYPE);
        ForgeRegistries.MENU_TYPES.register(ContainerSmithingTablePattern.ID, ContainerSmithingTablePattern.TYPE);
        if (ModList.get().isLoaded("ae2wtlib")) {
            WTCommonLoad.container();
        }
        if (ModList.get().isLoaded("appliede")) {
            APECommonLoad.container();
        }
    }

    private <T extends AEBaseBlockEntity> void bindTileEntity(Class<T> clazz, AEBaseEntityBlock<T> block, BlockEntityType.BlockEntitySupplier<? extends T> supplier) {
        BlockEntityTicker<T> serverTicker = null;
        if (ServerTickingBlockEntity.class.isAssignableFrom(clazz)) {
            serverTicker = (level, pos, state, entity) -> ((ServerTickingBlockEntity) entity).serverTick();
        }
        BlockEntityTicker<T> clientTicker = null;
        if (ClientTickingBlockEntity.class.isAssignableFrom(clazz)) {
            clientTicker = (level, pos, state, entity) -> ((ClientTickingBlockEntity) entity).clientTick();
        }
        block.setBlockEntity(clazz, GlodUtil.getTileType(clazz, supplier, block), clientTicker, serverTicker);
    }

    public void onInit() {
        for (Pair<String, Block> entry : blocks) {
            Block block = ForgeRegistries.BLOCKS.getValue(ExtendedAE.id(entry.getKey()));
            if (block instanceof AEBaseEntityBlock<?>) {
                AEBaseBlockEntity.registerBlockEntityItem(
                        ((AEBaseEntityBlock<?>) block).getBlockEntityType(),
                        block.asItem()
                );
            }
        }
        this.registerAEUpgrade();
        this.registerStorageHandler();
        this.registerRandomAPI();
        this.initPackageList();
        if (ModList.get().isLoaded("ae2wtlib")) {
            WTCommonLoad.init();
        }
        if (ModList.get().isLoaded("appflux")) {
            AFCommonLoad.init();
        }
        if (ModList.get().isLoaded("appliede")) {
            APECommonLoad.init();
        }
    }

    private void registerAEUpgrade() {
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_INTERFACE.asItem(), 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EPPItemAndBlock.EX_INTERFACE.asItem(), 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_INTERFACE_PART.asItem(), 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EPPItemAndBlock.EX_INTERFACE_PART.asItem(), 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.OVERSIZE_INTERFACE, 1, "gui.expatternprovider.oversize_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EPPItemAndBlock.OVERSIZE_INTERFACE, 1, "gui.expatternprovider.oversize_interface");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.OVERSIZE_INTERFACE_PART, 1, "gui.expatternprovider.oversize_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EPPItemAndBlock.OVERSIZE_INTERFACE_PART, 1, "gui.expatternprovider.oversize_interface");
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 5, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CRAFTING_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 5, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.ENERGY_CARD, EPPItemAndBlock.WIRELESS_CONNECTOR, 4);
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.EX_ASSEMBLER, 5);
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.EX_INSCRIBER, 4);
        Upgrades.add(AEItems.INVERTER_CARD, EPPItemAndBlock.TAG_STORAGE_BUS, 1);
        Upgrades.add(AEItems.VOID_CARD, EPPItemAndBlock.TAG_STORAGE_BUS, 1);
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.TAG_EXPORT_BUS, 1);
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.TAG_EXPORT_BUS, 4);
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.THRESHOLD_LEVEL_EMITTER, 1);
        Upgrades.add(AEItems.INVERTER_CARD, EPPItemAndBlock.MOD_STORAGE_BUS, 1);
        Upgrades.add(AEItems.VOID_CARD, EPPItemAndBlock.MOD_STORAGE_BUS, 1);
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.MOD_EXPORT_BUS, 1);
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.MOD_EXPORT_BUS, 4);
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.ACTIVE_FORMATION_PLANE, 1);
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.ACTIVE_FORMATION_PLANE, 5);
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.PRECISE_EXPORT_BUS, 5);
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.PRECISE_EXPORT_BUS, 1);
        Upgrades.add(AEItems.CRAFTING_CARD, EPPItemAndBlock.PRECISE_EXPORT_BUS, 1);
        Upgrades.add(AEItems.ENERGY_CARD, EPPItemAndBlock.WIRELESS_EX_PAT, 2, GuiText.WirelessTerminals.getTranslationKey());
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.EX_IO_PORT, 5);
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.EX_IO_PORT, 1);
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.PRECISE_STORAGE_BUS, 5);
        Upgrades.add(AEItems.VOID_CARD, EPPItemAndBlock.PRECISE_STORAGE_BUS, 1);
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.THRESHOLD_EXPORT_BUS, 5);
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.THRESHOLD_EXPORT_BUS, 1);
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.THRESHOLD_EXPORT_BUS, 4);
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.CIRCUIT_CUTTER, 4);
    }

    private void registerStorageHandler() {
        StorageCells.addCellHandler(InfinityCellInventory.HANDLER);
        StorageCellModels.registerModel(EPPItemAndBlock.INFINITY_CELL, ExtendedAE.id("block/drive/infinity_cell"));
    }

    private void onRegisterModels() {
        PartModels.registerModels(PartExPatternProvider.MODELS);
        PartModels.registerModels(PartExInterface.MODELS);
        PartModels.registerModels(PartExExportBus.MODELS);
        PartModels.registerModels(PartExImportBus.MODELS);
        PartModels.registerModels(PartExPatternAccessTerminal.MODELS);
        PartModels.registerModels(PartTagStorageBus.MODEL_BASE);
        PartModels.registerModels(PartTagExportBus.MODEL_BASE);
        PartModels.registerModels(PartThresholdLevelEmitter.MODELS);
        PartModels.registerModels(PartModStorageBus.MODEL_BASE);
        PartModels.registerModels(PartModExportBus.MODEL_BASE);
        PartModels.registerModels(PartActiveFormationPlane.MODELS);
        PartModels.registerModels(PartPreciseExportBus.MODELS);
        PartModels.registerModels(PartPreciseStorageBus.MODEL_BASE);
        PartModels.registerModels(PartThresholdExportBus.MODELS);
        PartModels.registerModels(PartOversizeInterface.MODELS);
    }

    private void initPackageList() {
        EPPConfig.tapeWhitelist.forEach(ItemMEPackingTape::registerPackableDevice);
    }

    private void registerRandomAPI() {
        GridLinkables.register(EPPItemAndBlock.WIRELESS_EX_PAT, WirelessTerminalItem.LINKABLE_HANDLER);
        if (!ModList.get().isLoaded("ae2wtlib")) {
            HotkeyActions.register(new InventoryHotkeyAction(EPPItemAndBlock.WIRELESS_EX_PAT, (player, i) -> EPPItemAndBlock.WIRELESS_EX_PAT.openFromInventory(player, i)), WIRELESS_TERMINAL);
        } else {
            HotkeyActions.register(new InventoryHotkeyAction(EPPItemAndBlock.WIRELESS_EX_PAT, (player, i) -> EPPItemAndBlock.WIRELESS_EX_PAT.openFromInventory(player, i)), "wireless_pattern_access_terminal");
        }
    }

    public void registerTab(Registry<CreativeModeTab> registry) {
        var tab = CreativeModeTab.builder()
                .icon(() -> new ItemStack(EPPItemAndBlock.EX_PATTERN_PROVIDER))
                .title(Component.translatable("itemGroup.epp"))
                .displayItems((__, o) -> {
                    for (Pair<String, Item> entry : items) {
                        if (entry.getRight() instanceof AEBaseItem aeItem) {
                            aeItem.addToMainCreativeTab(o);
                        } else {
                            o.accept(entry.getRight());
                        }
                    }
                    for (Pair<String, Block> entry : blocks) {
                        o.accept(entry.getRight());
                    }
                })
                .build();
        Registry.register(registry, ExtendedAE.id("tab_main"), tab);
    }

}
