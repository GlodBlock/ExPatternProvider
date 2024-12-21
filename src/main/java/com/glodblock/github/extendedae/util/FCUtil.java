package com.glodblock.github.extendedae.util;

import appeng.api.inventories.InternalInventory;
import appeng.api.parts.IPart;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Predicate;

public class FCUtil {

    public static void replaceTile(Level world, BlockPos pos, BlockEntity oldTile, BlockEntity newTile, BlockState newBlock) {
        var contents = oldTile.serializeNBT();
        world.removeBlockEntity(pos);
        world.removeBlock(pos, false);
        world.setBlock(pos, newBlock, 3);
        world.setBlockEntity(newTile);
        newTile.deserializeNBT(contents);
        if (newTile instanceof AEBaseBlockEntity aeTile) {
            aeTile.markForUpdate();
        } else {
            newTile.setChanged();
        }
    }

    public static Component getItemDisplayName(ItemLike item) {
        var itemStack = new ItemStack(item);
        return itemStack.getHoverName();
    }

    public static IPart getPart(BlockEntity te, Direction face) {
        if (te instanceof CableBusBlockEntity cable) {
            return cable.getPart(face);
        }
        return null;
    }

    public static boolean checkInvalidRL(String rl, IForgeRegistry<?> registry) {
        return checkInvalidRL(new ResourceLocation(rl), registry);
    }

    public static boolean checkInvalidRL(ResourceLocation rl, IForgeRegistry<?> registry) {
        return registry.containsKey(rl);
    }

    public static String[] trimSplit(String str) {
        var sp = str.split(",");
        for (int i = 0; i < sp.length; i ++) {
            sp[i] = sp[i].trim();
        }
        return sp;
    }

    public static int speedCardMap(int card) {
        return speedCardMap(card, 1);
    }

    public static int speedCardMap(int card, int multi) {
        return multi * switch (card) {
            default -> 2;
            case 1 -> 3;
            case 2 -> 5;
            case 3 -> 10;
            case 4 -> 50;
        };
    }

    public static boolean ejectInv(Level world, BlockPos pos, InternalInventory inv, Predicate<? super BlockEntity> shouldIgnore) {
        for (var dir : Direction.values()) {
            var te = world.getBlockEntity(pos.relative(dir));
            if (te == null || shouldIgnore.test(te)) {
                continue;
            }
            var target = InternalInventory.wrapExternal(world, pos.relative(dir), dir.getOpposite());
            if (target != null) {
                int startItems = inv.getStackInSlot(0).getCount();
                inv.insertItem(0, target.addItems(inv.extractItem(0, 64, false)), false);
                int endItems = inv.getStackInSlot(0).getCount();
                if (startItems != endItems) {
                    return true;
                }
            }
        }
        return false;
    }

}
