package com.glodblock.github.extendedae.common.items;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.parts.AEBasePart;
import com.glodblock.github.extendedae.util.FCUtil;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.HashMap;

public abstract class ItemUpgrade extends Item {

    private final HashMap<Class<? extends BlockEntity>, TileEntityPair> BLOCK_MAP = new HashMap<>();
    private final HashMap<Class<? extends IPart>, IPartItem<?>> PART_MAP = new HashMap<>();

    public ItemUpgrade(Item.Properties properties) {
        super(properties);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        var pos = context.getClickedPos();
        var world = context.getLevel();
        var tile = world.getBlockEntity(pos);
        if (tile != null) {
            var ctx = new BlockPlaceContext(context);
            var tClazz = tile.getClass();
            if (this.BLOCK_MAP.containsKey(tClazz)) {
                var originState = world.getBlockState(pos);
                var replaceInfo = this.BLOCK_MAP.get(tClazz);
                var state = replaceInfo.block.getStateForPlacement(ctx);
                if (state == null) {
                    return InteractionResult.PASS;
                }
                for (var sp : originState.getValues().entrySet()) {
                    var pt = sp.getKey();
                    var va = sp.getValue();
                    try {
                        if (state.hasProperty(pt)) {
                            state = state.<Comparable, Comparable>setValue((Property)pt, va);
                        }
                    } catch (Exception ignore) {
                        // NO-OP
                    }
                }
                var tileType = GlodUtil.getTileType(replaceInfo.tile);
                var te = tileType.create(pos, state);
                FCUtil.replaceTile(world, pos, tile, te, state);
                context.getItemInHand().shrink(1);
                return InteractionResult.CONSUME;
            } else if (tile instanceof CableBusBlockEntity cable) {
                Vec3 hitVec = context.getClickLocation();
                Vec3 hitInBlock = new Vec3(hitVec.x - pos.getX(), hitVec.y - pos.getY(), hitVec.z - pos.getZ());
                var part = cable.getCableBus().selectPartLocal(hitInBlock).part;
                if (part instanceof AEBasePart basePart && this.PART_MAP.containsKey(part.getClass())) {
                    var side = basePart.getSide();
                    var contents = new CompoundTag();
                    var partItem = this.PART_MAP.get(part.getClass());
                    part.writeToNBT(contents);
                    var p = cable.replacePart(partItem, side, context.getPlayer(), null);
                    if (p != null) {
                        contents.put("BYPASS_EXTENDEDAE", new CompoundTag());
                        p.readFromNBT(contents);
                        p.addToWorld();
                    }
                } else {
                    return InteractionResult.PASS;
                }
                context.getItemInHand().shrink(1);
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    protected void addTile(Class<? extends BlockEntity> clazz, Block block, Class<? extends BlockEntity> tile) {
        this.BLOCK_MAP.put(clazz, new TileEntityPair(block, tile));
    }

    protected void addPart(Class<? extends IPart> clazz, IPartItem<?> item) {
        this.PART_MAP.put(clazz, item);
    }

    private record TileEntityPair(Block block, Class<? extends BlockEntity> tile) {

    }

}
