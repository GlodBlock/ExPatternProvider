package com.glodblock.github.extendedae.util;

import appeng.api.behaviors.StackTransferContext;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.blockentity.storage.IOPortBlockEntity;
import appeng.crafting.pattern.AECraftingPattern;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.parts.AEBasePart;
import appeng.parts.automation.AbstractLevelEmitterPart;
import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.IOBusPart;
import appeng.util.ConfigInventory;
import appeng.util.inv.AppEngInternalInventory;
import com.glodblock.github.glodium.reflect.ReflectKit;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.phys.BlockHitResult;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Ae2Reflect {

    private static final Field fContainerTracker_serverId;
    private static final Field fContainerTracker_container;
    private static final Field fUseOnContext_hitResult;
    private static final Field fDriveBlockEntity_clientSideCellState;
    private static final Field fDriveBlockEntity_clientSideCellItems;
    private static final Field fDriveBlockEntity_clientSideOnline;
    private static final Field fAbstractLevelEmitterPart_prevState;
    private static final Field fAEBaseBlockEntity_customName;
    private static final Field fAEBasePart_customName;
    private static final Field fAECraftingPattern_recipe;
    private static final Field fIOPortBlockEntity_inputCells;
    private static final Field fIOPortBlockEntity_upgrades;
    private static final Field fInterfaceLogic_config;
    private static final Field fInterfaceLogic_storage;
    private static final Method mDriveBlockEntity_updateClientSideState;
    private static final Method mAECraftingPattern_getCompressedIndexFromSparse;
    private static final Method mIOBusPart_updateState;
    private static final Method mExportBusPart_createTransferContext;
    private static final Method mIOPortBlockEntity_transferContents;
    private static final Method mIOPortBlockEntity_moveSlot;
    private static final Method mInterfaceLogic_onConfigRowChanged;
    private static final Method mInterfaceLogic_onStorageChanged;

    static {
        try {
            fContainerTracker_serverId = ReflectKit.reflectField(Class.forName("appeng.menu.implementations.PatternAccessTermMenu$ContainerTracker"), "serverId");
            fContainerTracker_container = ReflectKit.reflectField(Class.forName("appeng.menu.implementations.PatternAccessTermMenu$ContainerTracker"), "container");
            fUseOnContext_hitResult = ReflectKit.reflectField(UseOnContext.class, "hitResult", "f_43705_");
            fDriveBlockEntity_clientSideCellState = ReflectKit.reflectField(DriveBlockEntity.class, "clientSideCellState");
            fDriveBlockEntity_clientSideCellItems = ReflectKit.reflectField(DriveBlockEntity.class, "clientSideCellItems");
            fDriveBlockEntity_clientSideOnline = ReflectKit.reflectField(DriveBlockEntity.class, "clientSideOnline");
            fAbstractLevelEmitterPart_prevState = ReflectKit.reflectField(AbstractLevelEmitterPart.class, "prevState");
            fAEBaseBlockEntity_customName = ReflectKit.reflectField(AEBaseBlockEntity.class, "customName");
            fAEBasePart_customName = ReflectKit.reflectField(AEBasePart.class, "customName");
            fAECraftingPattern_recipe = ReflectKit.reflectField(AECraftingPattern.class, "recipe");
            fIOPortBlockEntity_inputCells = ReflectKit.reflectField(IOPortBlockEntity.class, "inputCells");
            fIOPortBlockEntity_upgrades = ReflectKit.reflectField(IOPortBlockEntity.class, "upgrades");
            fInterfaceLogic_config = ReflectKit.reflectField(InterfaceLogic.class, "config");
            fInterfaceLogic_storage = ReflectKit.reflectField(InterfaceLogic.class, "storage");
            mDriveBlockEntity_updateClientSideState = ReflectKit.reflectMethod(DriveBlockEntity.class, "updateClientSideState");
            mAECraftingPattern_getCompressedIndexFromSparse = ReflectKit.reflectMethod(AECraftingPattern.class, "getCompressedIndexFromSparse", int.class);
            mIOBusPart_updateState = ReflectKit.reflectMethod(IOBusPart.class, "updateState");
            mExportBusPart_createTransferContext = ReflectKit.reflectMethod(ExportBusPart.class, "createTransferContext", IStorageService.class, IEnergyService.class);
            mIOPortBlockEntity_transferContents = ReflectKit.reflectMethod(IOPortBlockEntity.class, "transferContents", IGrid.class, StorageCell.class, long.class);
            mIOPortBlockEntity_moveSlot = ReflectKit.reflectMethod(IOPortBlockEntity.class, "moveSlot", int.class);
            mInterfaceLogic_onConfigRowChanged = ReflectKit.reflectMethod(InterfaceLogic.class, "onConfigRowChanged");
            mInterfaceLogic_onStorageChanged = ReflectKit.reflectMethod(InterfaceLogic.class, "onStorageChanged");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize AE2 reflection hacks!", e);
        }
    }

    public static long getContainerID(Object owner) {
        return ReflectKit.readField(owner, fContainerTracker_serverId);
    }

    public static PatternContainer getContainer(Object owner) {
        return ReflectKit.readField(owner, fContainerTracker_container);
    }

    public static BlockHitResult getHitResult(UseOnContext owner) {
        return ReflectKit.readField(owner, fUseOnContext_hitResult);
    }

    public static void updateDriveClientSideState(DriveBlockEntity owner) {
        ReflectKit.executeMethod(owner, mDriveBlockEntity_updateClientSideState);
    }

    public static CellState[] getCellState(DriveBlockEntity owner) {
        return ReflectKit.readField(owner, fDriveBlockEntity_clientSideCellState);
    }

    public static boolean getClientOnline(DriveBlockEntity owner) {
        return ReflectKit.readField(owner, fDriveBlockEntity_clientSideOnline);
    }

    public static void setClientOnline(DriveBlockEntity owner, boolean val) {
        ReflectKit.writeField(owner, fDriveBlockEntity_clientSideOnline, val);
    }

    public static Item[] getCellItem(DriveBlockEntity owner) {
        return ReflectKit.readField(owner, fDriveBlockEntity_clientSideCellItems);
    }

    public static int getCompressIndex(AECraftingPattern owner, int id) {
        return ReflectKit.executeMethod2(owner, mAECraftingPattern_getCompressedIndexFromSparse, id);
    }

    public static boolean getPrevState(AbstractLevelEmitterPart owner) {
        return ReflectKit.readField(owner, fAbstractLevelEmitterPart_prevState);
    }

    public static void setCustomName(Object owner, Component name) {
        if (owner instanceof AEBaseBlockEntity) {
            ReflectKit.writeField(owner, fAEBaseBlockEntity_customName, name);
        } else if (owner instanceof AEBasePart) {
            ReflectKit.writeField(owner, fAEBasePart_customName, name);
        }
    }

    public static void updatePartState(IOBusPart owner) {
        ReflectKit.executeMethod(owner, mIOBusPart_updateState);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static StackTransferContext getExportContext(ExportBusPart owner, IStorageService storageService, IEnergyService energyService) {
        return ReflectKit.executeMethod2(owner, mExportBusPart_createTransferContext, storageService, energyService);
    }

    public static CraftingRecipe getCraftRecipe(AECraftingPattern owner) {
        return ReflectKit.readField(owner, fAECraftingPattern_recipe);
    }

    public static AppEngInternalInventory getInputCellInv(IOPortBlockEntity owner) {
        return ReflectKit.readField(owner, fIOPortBlockEntity_inputCells);
    }

    public static void setIOPortUpgrade(IOPortBlockEntity owner, IUpgradeInventory val) {
        ReflectKit.writeField(owner, fIOPortBlockEntity_upgrades, val);
    }

    public static long transferItemsFromCell(IOPortBlockEntity owner, IGrid grid, StorageCell cellInv, long itemsToMove) {
        return ReflectKit.executeMethod2(owner, mIOPortBlockEntity_transferContents, grid, cellInv, itemsToMove);
    }

    public static boolean moveSlotInCell(IOPortBlockEntity owner, int x) {
        return ReflectKit.executeMethod2(owner, mIOPortBlockEntity_moveSlot, x);
    }

    public static void setInterfaceStorage(InterfaceLogic owner, ConfigInventory storage) {
        ReflectKit.writeField(owner, fInterfaceLogic_storage, storage);
    }

    public static void setInterfaceConfig(InterfaceLogic owner, ConfigInventory config) {
        ReflectKit.writeField(owner, fInterfaceLogic_config, config);
    }

    public static void onInterfaceConfigChange(InterfaceLogic owner) {
        ReflectKit.executeMethod(owner, mInterfaceLogic_onConfigRowChanged);
    }

    public static void onInterfaceStorageChange(InterfaceLogic owner) {
        ReflectKit.executeMethod(owner, mInterfaceLogic_onStorageChanged);
    }

}
