package com.glodblock.github.extendedae.common.items;

import appeng.api.parts.IPartItem;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.items.AEBaseItem;
import appeng.parts.PartPlacement;
import appeng.util.Platform;
import com.glodblock.github.extendedae.common.EAESingletons;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemPackedDevice extends AEBaseItem {

    public ItemPackedDevice() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void addToMainCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        // NO-OP
    }

    @Override
    public void appendHoverText(@NotNull ItemStack is, Item.@NotNull TooltipContext ctx, @NotNull List<Component> lines, @NotNull TooltipFlag adv) {
        if (is.has(EAESingletons.IS_PART)) {
            boolean isPart = Boolean.TRUE.equals(is.get(EAESingletons.IS_PART));
            if (isPart) {
                var data = is.get(EAESingletons.TAPE_PART_DATA);
                if (data != null) {
                    var item = BuiltInRegistries.ITEM.get(data.id());
                    if (item != Items.AIR) {
                        var name = new ItemStack(item).getDisplayName();
                        lines.add(Component.translatable("packaged_device.tooltip", name).withStyle(ChatFormatting.GRAY));
                        return;
                    }
                }
            } else {
                var data = is.get(EAESingletons.TAPE_TILE_DATA);
                if (data != null) {
                    var item = BuiltInRegistries.BLOCK.get(data.block());
                    if (item != Blocks.AIR) {
                        var name = new ItemStack(item).getDisplayName();
                        lines.add(Component.translatable("packaged_device.tooltip", name).withStyle(ChatFormatting.GRAY));
                        return;
                    }
                }
            }
        }
        lines.add(Component.translatable("packaged_device.error.tooltip").withStyle(ChatFormatting.RED));
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        var side = context.getClickedFace();
        var pos = context.getClickedPos();
        var world = context.getLevel();
        var pack = context.getItemInHand();
        if (!pack.has(EAESingletons.IS_PART)) {
            return InteractionResult.FAIL;
        }
        boolean isPart = Boolean.TRUE.equals(pack.get(EAESingletons.IS_PART));
        if (isPart) {
            var data = pack.get(EAESingletons.TAPE_PART_DATA);
            if (data == null) {
                return InteractionResult.FAIL;
            }
            var item = BuiltInRegistries.ITEM.get(data.id());
            if (item instanceof IPartItem<?> partItem) {
                var placement = PartPlacement.getPartPlacement(context.getPlayer(), world, new ItemStack(partItem), pos, side, context.getClickLocation());
                if (placement != null) {
                    var part = PartPlacement.placePart(context.getPlayer(), world, partItem, null, placement.pos(), placement.side());
                    if (part != null) {
                        var contents = data.context();
                        contents.put("BYPASS_EXTENDEDAE", new CompoundTag());
                        part.readFromNBT(data.context(), world.registryAccess());
                        part.addToWorld();
                        pack.shrink(1);
                        return InteractionResult.sidedSuccess(world.isClientSide);
                    } else {
                        Platform.sendImmediateBlockEntityUpdate(context.getPlayer(), pos);
                    }
                }
            }
            return InteractionResult.PASS;
        } else {
            var data = pack.get(EAESingletons.TAPE_TILE_DATA);
            if (data == null) {
                return InteractionResult.FAIL;
            }
            var block = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(data.id());
            if (block != null) {
                var state = NbtUtils.readBlockState(world.holderLookup(Registries.BLOCK), data.state());
                var item = state.getBlock().asItem();
                if (item instanceof BlockItem blockItem && context.getPlayer() != null) {
                    var ctxB = new BlockPlaceContext(world, context.getPlayer(), context.getHand(), new ItemStack(blockItem), context.hitResult);
                    ctxB = blockItem.updatePlacementContext(ctxB);
                    if (ctxB != null && blockItem.place(ctxB) != InteractionResult.FAIL) {
                        var posNew = ctxB.getClickedPos();
                        var te = block.create(posNew, state);
                        if (te != null) {
                            world.setBlock(posNew, state, 3);
                            world.setBlockEntity(te);
                            te.loadWithComponents(data.context(), world.registryAccess());
                            if (te instanceof AEBaseBlockEntity aeTile) {
                                aeTile.markForUpdate();
                            } else {
                                te.setChanged();
                            }
                            pack.shrink(1);
                            return InteractionResult.sidedSuccess(world.isClientSide);
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

}
