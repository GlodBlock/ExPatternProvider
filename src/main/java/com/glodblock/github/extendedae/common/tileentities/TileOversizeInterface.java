package com.glodblock.github.extendedae.common.tileentities;

import appeng.api.stacks.AEKey;
import appeng.api.storage.AEKeyFilter;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import appeng.util.ConfigInventory;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.config.EPPConfig;
import com.glodblock.github.extendedae.container.ContainerExInterface;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TileOversizeInterface extends TileExInterface {

    public TileOversizeInterface(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileOversizeInterface.class, TileOversizeInterface::new, EPPItemAndBlock.OVERSIZE_INTERFACE), pos, blockState);
        var logic = this.getInterfaceLogic();
        Ae2Reflect.setInterfaceConfig(logic, new OversizeConfigInv(null, GenericStackInv.Mode.CONFIG_STACKS, 36, () -> Ae2Reflect.onInterfaceConfigChange(logic), false));
        Ae2Reflect.setInterfaceStorage(logic, new OversizeConfigInv(null, GenericStackInv.Mode.STORAGE, 36, () -> Ae2Reflect.onInterfaceStorageChange(logic), false));
        this.getConfig().useRegisteredCapacities();
        this.getStorage().useRegisteredCapacities();
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        MenuOpener.open(ContainerExInterface.TYPE_OVERSIZE, player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(ContainerExInterface.TYPE_OVERSIZE, player, subMenu.getLocator());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(EPPItemAndBlock.OVERSIZE_INTERFACE);
    }

    private static class OversizeConfigInv extends ConfigInventory {

        private OversizeConfigInv(@Nullable AEKeyFilter filter, GenericStackInv.Mode mode, int size, @Nullable Runnable listener, boolean allowOverstacking) {
            super(filter, mode, size, listener, allowOverstacking);
        }

        @Override
        public long getMaxAmount(AEKey key) {
            try {
                return Math.multiplyExact(super.getMaxAmount(key), EPPConfig.oversizeMultiplier);
            } catch (Exception e) {
                return Long.MAX_VALUE;
            }
        }

    }

}
