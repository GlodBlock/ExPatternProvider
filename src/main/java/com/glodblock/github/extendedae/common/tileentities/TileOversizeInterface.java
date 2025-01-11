package com.glodblock.github.extendedae.common.tileentities;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.storage.AEKeySlotFilter;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import appeng.util.ConfigInventory;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.config.EAEConfig;
import com.glodblock.github.extendedae.container.ContainerExInterface;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class TileOversizeInterface extends TileExInterface {

    public TileOversizeInterface(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileOversizeInterface.class, TileOversizeInterface::new, EAESingletons.OVERSIZE_INTERFACE), pos, blockState);
        var logic = this.getInterfaceLogic();
        Ae2Reflect.setInterfaceConfig(logic, new OversizeConfigInv(AEKeyTypes.getAll(), null, GenericStackInv.Mode.CONFIG_STACKS, 36, () -> Ae2Reflect.onInterfaceConfigChange(logic), false));
        Ae2Reflect.setInterfaceStorage(logic, new OversizeConfigInv(AEKeyTypes.getAll(), (slot, key) -> Ae2Reflect.isInterfaceSlotAllowed(logic, slot, key), GenericStackInv.Mode.STORAGE, 36, () -> Ae2Reflect.onInterfaceStorageChange(logic), false));
        this.getConfig().useRegisteredCapacities();
        this.getStorage().useRegisteredCapacities();
    }

    @Override
    public void openMenu(Player player, MenuHostLocator locator) {
        MenuOpener.open(ContainerExInterface.TYPE_OVERSIZE, player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(ContainerExInterface.TYPE_OVERSIZE, player, subMenu.getLocator());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(EAESingletons.OVERSIZE_INTERFACE);
    }

    private static class OversizeConfigInv extends ConfigInventory {

        protected OversizeConfigInv(Set<AEKeyType> supportedTypes, @Nullable AEKeySlotFilter slotFilter, Mode mode, int size, @Nullable Runnable listener, boolean allowOverstacking) {
            super(supportedTypes, slotFilter, mode, size, listener, allowOverstacking);
        }

        @Override
        public long getMaxAmount(AEKey key) {
            try {
                return Math.multiplyExact(super.getMaxAmount(key), EAEConfig.getOversizeMultiplier(key));
            } catch (Exception e) {
                return Long.MAX_VALUE;
            }
        }

    }

}
