package com.glodblock.github.extendedae.common;

import appeng.api.AECapabilities;
import appeng.api.client.StorageCellModels;
import appeng.api.features.GridLinkables;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.parts.PartModels;
import appeng.api.parts.RegisterPartCapabilitiesEvent;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.block.AEBaseBlockItem;
import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.AEBaseInvBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.blockentity.powersink.AEBasePoweredBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.items.AEBaseItem;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.items.tools.powered.powersink.PoweredItemCapabilities;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.api.caps.ICrankPowered;
import com.glodblock.github.extendedae.api.caps.IGenericInvHost;
import com.glodblock.github.extendedae.api.caps.IMEStorageAccess;
import com.glodblock.github.extendedae.common.inventory.InfinityCellInventory;
import com.glodblock.github.extendedae.common.inventory.VoidCellInventory;
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
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixBase;
import com.glodblock.github.extendedae.config.ConfigCondition;
import com.glodblock.github.extendedae.config.EAEConfig;
import com.glodblock.github.extendedae.container.ContainerActiveFormationPlane;
import com.glodblock.github.extendedae.container.ContainerAssemblerMatrix;
import com.glodblock.github.extendedae.container.ContainerCaner;
import com.glodblock.github.extendedae.container.ContainerCircuitCutter;
import com.glodblock.github.extendedae.container.ContainerCrystalAssembler;
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
import com.glodblock.github.extendedae.container.ContainerVoidCell;
import com.glodblock.github.extendedae.container.ContainerWirelessConnector;
import com.glodblock.github.extendedae.container.pattern.ContainerCraftingPattern;
import com.glodblock.github.extendedae.container.pattern.ContainerProcessingPattern;
import com.glodblock.github.extendedae.container.pattern.ContainerSmithingTablePattern;
import com.glodblock.github.extendedae.container.pattern.ContainerStonecuttingPattern;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipe;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipeSerializer;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipe;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipeSerializer;
import com.glodblock.github.extendedae.recipe.CrystalFixerRecipe;
import com.glodblock.github.extendedae.recipe.CrystalFixerRecipeSerializer;
import com.glodblock.github.extendedae.xmod.ModConstants;
import com.glodblock.github.extendedae.xmod.appflux.AFCommonLoad;
import com.glodblock.github.extendedae.xmod.wt.ContainerWirelessExPAT;
import com.glodblock.github.glodium.registry.RegistryHandler;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;

public class EAERegistryHandler extends RegistryHandler {

    public static final EAERegistryHandler INSTANCE = new EAERegistryHandler();

    @SuppressWarnings("UnstableApiUsage")
    public EAERegistryHandler() {
        super(ExtendedAE.MODID);
        this.cap(AEBaseInvBlockEntity.class, Capabilities.ItemHandler.BLOCK, AEBaseInvBlockEntity::getExposedItemHandler);
        this.cap(AEBasePoweredBlockEntity.class, Capabilities.EnergyStorage.BLOCK, AEBasePoweredBlockEntity::getEnergyStorage);
        this.cap(IInWorldGridNodeHost.class, AECapabilities.IN_WORLD_GRID_NODE_HOST, (object, context) -> object);
        this.cap(IAEItemPowerStorage.class, Capabilities.EnergyStorage.ITEM, (object, context) -> new PoweredItemCapabilities(object, (IAEItemPowerStorage) object.getItem()));
        this.cap(ICrankPowered.class, AECapabilities.CRANKABLE, ICrankPowered::getCrankable);
        this.cap(ICraftingMachine.class, AECapabilities.CRAFTING_MACHINE, (object, context) -> object);
        this.cap(IGenericInvHost.class, AECapabilities.GENERIC_INTERNAL_INV, IGenericInvHost::getGenericInv);
        this.cap(IMEStorageAccess.class, AECapabilities.ME_STORAGE, IMEStorageAccess::getMEStorage);
        this.cap(TileAssemblerMatrixBase.class, Capabilities.ItemHandler.BLOCK, TileAssemblerMatrixBase::getPatternInv);
    }

    public <T extends AEBaseBlockEntity> void block(String name, AEBaseEntityBlock<T> block, Class<T> clazz, BlockEntityType.BlockEntitySupplier<? extends T> supplier) {
        bindTileEntity(clazz, block, supplier);
        block(name, block, b -> new AEBaseBlockItem(b, new Item.Properties()));
        tile(name, block.getBlockEntityType());
    }

    @Override
    public void runRegister() {
        super.runRegister();
        this.onRegisterContainer();
        this.onRegisterModels();
        this.onRegisterRecipe();
    }

    public Collection<Block> getBlocks() {
        return this.blocks.stream().map(Pair::getRight).toList();
    }

    @SubscribeEvent
    public void onRegisterCapability(RegisterPartCapabilitiesEvent event) {
        PartExInterface.registerCapability(event);
        PartExPatternProvider.registerCapability(event);
        PartOversizeInterface.registerCapability(event);
    }

    @SubscribeEvent
    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        super.onRegisterCapabilities(event);
    }

    private void onRegisterRecipe() {
        Registry.register(NeoForgeRegistries.CONDITION_SERIALIZERS, ExtendedAE.id("config"), ConfigCondition.CODEC);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, CrystalAssemblerRecipe.ID, CrystalAssemblerRecipe.TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CrystalAssemblerRecipe.ID, CrystalAssemblerRecipeSerializer.INSTANCE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, CircuitCutterRecipe.ID, CircuitCutterRecipe.TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CircuitCutterRecipe.ID, CircuitCutterRecipeSerializer.INSTANCE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, CrystalFixerRecipe.ID, CrystalFixerRecipe.TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CrystalFixerRecipe.ID, CrystalFixerRecipeSerializer.INSTANCE);
    }

    private void onRegisterContainer() {
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_pattern_provider"), ContainerExPatternProvider.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_interface"), ContainerExInterface.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_export_bus"), ContainerExIOBus.EXPORT_TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_import_bus"), ContainerExIOBus.IMPORT_TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_pattern_access_terminal"), ContainerExPatternTerminal.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("wireless_connector"), ContainerWirelessConnector.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ingredient_buffer"), ContainerIngredientBuffer.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_drive"), ContainerExDrive.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("pattern_modifier"), ContainerPatternModifier.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_molecular_assembler"), ContainerExMolecularAssembler.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_inscriber"), ContainerExInscriber.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("tag_storage_bus"), ContainerTagStorageBus.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("tag_export_bus"), ContainerTagExportBus.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("threshold_level_emitter"), ContainerThresholdLevelEmitter.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("renamer"), ContainerRenamer.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("mod_storage_bus"), ContainerModStorageBus.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("mod_export_bus"), ContainerModExportBus.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("active_formation_plane"), ContainerActiveFormationPlane.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("caner"), ContainerCaner.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("precise_export_bus"), ContainerPreciseExportBus.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("u_wireless_ex_pattern_access_terminal"), ContainerWirelessExPAT.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_ioport"), ContainerExIOPort.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("precise_storage_bus"), ContainerPreciseStorageBus.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("threshold_export_bus"), ContainerThresholdExportBus.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("crystal_assembler"), ContainerCrystalAssembler.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("circuit_cutter"), ContainerCircuitCutter.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("oversize_interface"), ContainerExInterface.TYPE_OVERSIZE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("assembler_matrix"), ContainerAssemblerMatrix.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("void_cell"), ContainerVoidCell.TYPE);
        Registry.register(BuiltInRegistries.MENU, ContainerProcessingPattern.ID, ContainerProcessingPattern.TYPE);
        Registry.register(BuiltInRegistries.MENU, ContainerCraftingPattern.ID, ContainerCraftingPattern.TYPE);
        Registry.register(BuiltInRegistries.MENU, ContainerStonecuttingPattern.ID, ContainerStonecuttingPattern.TYPE);
        Registry.register(BuiltInRegistries.MENU, ContainerSmithingTablePattern.ID, ContainerSmithingTablePattern.TYPE);
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
            Block block = entry.getRight();
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
        if (GlodUtil.checkMod(ModConstants.APPFLUX)) {
            AFCommonLoad.init();
        }
    }

    private void registerAEUpgrade() {
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.EX_INTERFACE, 1, "gui.extendedae.ex_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EAESingletons.EX_INTERFACE, 1, "gui.extendedae.ex_interface");
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.EX_INTERFACE_PART, 1, "gui.extendedae.ex_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EAESingletons.EX_INTERFACE_PART, 1, "gui.extendedae.ex_interface");
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.OVERSIZE_INTERFACE, 1, "gui.extendedae.oversize_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EAESingletons.OVERSIZE_INTERFACE, 1, "gui.extendedae.oversize_interface");
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.OVERSIZE_INTERFACE_PART, 1, "gui.extendedae.oversize_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EAESingletons.OVERSIZE_INTERFACE_PART, 1, "gui.extendedae.oversize_interface");
        Upgrades.add(AEItems.CAPACITY_CARD, EAESingletons.EX_EXPORT_BUS, 5, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EAESingletons.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.EX_EXPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CRAFTING_CARD, EAESingletons.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CAPACITY_CARD, EAESingletons.EX_IMPORT_BUS, 5, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EAESingletons.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.EX_IMPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EAESingletons.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.ENERGY_CARD, EAESingletons.WIRELESS_CONNECTOR, 4);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.EX_ASSEMBLER, 5);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.EX_INSCRIBER, 4);
        Upgrades.add(AEItems.INVERTER_CARD, EAESingletons.TAG_STORAGE_BUS, 1);
        Upgrades.add(AEItems.VOID_CARD, EAESingletons.TAG_STORAGE_BUS, 1);
        Upgrades.add(AEItems.REDSTONE_CARD, EAESingletons.TAG_EXPORT_BUS, 1);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.TAG_EXPORT_BUS, 4);
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.THRESHOLD_LEVEL_EMITTER, 1);
        Upgrades.add(AEItems.INVERTER_CARD, EAESingletons.MOD_STORAGE_BUS, 1);
        Upgrades.add(AEItems.VOID_CARD, EAESingletons.MOD_STORAGE_BUS, 1);
        Upgrades.add(AEItems.REDSTONE_CARD, EAESingletons.MOD_EXPORT_BUS, 1);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.MOD_EXPORT_BUS, 4);
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.ACTIVE_FORMATION_PLANE, 1);
        Upgrades.add(AEItems.CRAFTING_CARD, EAESingletons.ACTIVE_FORMATION_PLANE, 1);
        Upgrades.add(AEItems.CAPACITY_CARD, EAESingletons.ACTIVE_FORMATION_PLANE, 5);
        Upgrades.add(AEItems.CAPACITY_CARD, EAESingletons.PRECISE_EXPORT_BUS, 5);
        Upgrades.add(AEItems.REDSTONE_CARD, EAESingletons.PRECISE_EXPORT_BUS, 1);
        Upgrades.add(AEItems.CRAFTING_CARD, EAESingletons.PRECISE_EXPORT_BUS, 1);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.EX_IO_PORT, 5);
        Upgrades.add(AEItems.REDSTONE_CARD, EAESingletons.EX_IO_PORT, 1);
        Upgrades.add(AEItems.CAPACITY_CARD, EAESingletons.PRECISE_STORAGE_BUS, 5);
        Upgrades.add(AEItems.VOID_CARD, EAESingletons.PRECISE_STORAGE_BUS, 1);
        Upgrades.add(AEItems.CAPACITY_CARD, EAESingletons.THRESHOLD_EXPORT_BUS, 5);
        Upgrades.add(AEItems.REDSTONE_CARD, EAESingletons.THRESHOLD_EXPORT_BUS, 1);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.THRESHOLD_EXPORT_BUS, 4);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.CRYSTAL_ASSEMBLER, 4);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.CIRCUIT_CUTTER, 4);
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.VOID_CELL, 1);
        Upgrades.add(AEItems.INVERTER_CARD, EAESingletons.VOID_CELL, 1);
    }

    private void registerStorageHandler() {
        StorageCells.addCellHandler(InfinityCellInventory.HANDLER);
        StorageCells.addCellHandler(VoidCellInventory.HANDLER);
        StorageCellModels.registerModel(EAESingletons.INFINITY_WATER_CELL, ExtendedAE.id("block/drive/infinity_water_cell"));
        StorageCellModels.registerModel(EAESingletons.INFINITY_COBBLESTONE_CELL, ExtendedAE.id("block/drive/infinity_cobblestone_cell"));
        StorageCellModels.registerModel(EAESingletons.VOID_CELL, ExtendedAE.id("block/drive/void_cell"));
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
        EAEConfig.tapeWhitelist.forEach(ItemMEPackingTape::registerPackableDevice);
    }

    private void registerRandomAPI() {
        GridLinkables.register(EAESingletons.WIRELESS_EX_PAT, WirelessTerminalItem.LINKABLE_HANDLER);
    }

    public void registerTab(Registry<CreativeModeTab> registry) {
        var tab = CreativeModeTab.builder()
                .icon(() -> new ItemStack(EAESingletons.EX_PATTERN_PROVIDER))
                .title(Component.translatable("itemGroup.extendedae"))
                .displayItems((p, o) -> {
                    for (Pair<String, Item> entry : items) {
                        if (entry.getRight() instanceof AEBaseItem aeItem) {
                            aeItem.addToMainCreativeTab(p, o);
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
