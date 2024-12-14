package com.glodblock.github.extendedae.common.items;

import appeng.api.parts.IPartItem;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.items.AEBaseItem;
import appeng.parts.PartPlacement;
import appeng.util.Platform;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import com.glodblock.github.extendedae.util.FCUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemPackedDevice extends AEBaseItem {

    public ItemPackedDevice() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void addToMainCreativeTab(CreativeModeTab.Output output) {
    }

    @Override
    public void appendHoverText(@NotNull ItemStack is, Level world, @NotNull List<Component> lines, @NotNull TooltipFlag adv) {
        if (is.hasTag()) {
            var tag = is.getTag();
            assert tag != null;
            if (checkNBT(tag)) {
                if (tag.getBoolean("part")) {
                    var item = ForgeRegistries.ITEMS.getHolder(new ResourceLocation(tag.getString("id")));
                    if (item.isPresent()) {
                        var name = FCUtil.getItemDisplayName(item.get().get());
                        lines.add(Component.translatable("packaged_device.tooltip", name).withStyle(ChatFormatting.GRAY));
                        return;
                    }
                } else {
                    var item = ForgeRegistries.BLOCKS.getHolder(new ResourceLocation(tag.getString("block_id")));
                    if (item.isPresent()) {
                        var name = FCUtil.getItemDisplayName(item.get().get());
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
        if (!pack.hasTag()) {
            return InteractionResult.FAIL;
        }
        var ctx = pack.getTag();
        assert ctx != null;
        if (!checkNBT(ctx)) {
            return InteractionResult.FAIL;
        }
        if (ctx.getBoolean("part")) {
            var itemO = ForgeRegistries.ITEMS.getHolder(new ResourceLocation(ctx.getString("id")));
            if (itemO.isPresent()) {
                var item = itemO.get().get();
                if (item instanceof IPartItem<?> partItem) {
                    var placement = PartPlacement.getPartPlacement(context.getPlayer(), world, new ItemStack(partItem), pos, side, context.getClickLocation());
                    if (placement != null) {
                        var part = PartPlacement.placePart(context.getPlayer(), world, partItem, null, placement.pos(), placement.side());
                        if (part != null) {
                            var contents = ctx.getCompound("ctx");
                            contents.put("BYPASS_EXTENDEDAE", new CompoundTag());
                            part.readFromNBT(contents);
                            part.addToWorld();
                            pack.shrink(1);
                            return InteractionResult.sidedSuccess(world.isClientSide);
                        } else {
                            Platform.sendImmediateBlockEntityUpdate(context.getPlayer(), pos);
                        }
                    }
                }
            }
            return InteractionResult.PASS;
        } else {
            var blockO = ForgeRegistries.BLOCK_ENTITY_TYPES.getHolder(new ResourceLocation(ctx.getString("id")));
            if (blockO.isPresent()) {
                var block = blockO.get().get();
                var state = NbtUtils.readBlockState(world.holderLookup(Registries.BLOCK), ctx.getCompound("state"));
                var item = state.getBlock().asItem();
                if (item instanceof BlockItem blockItem && context.getPlayer() != null) {
                    var ctxB = new BlockPlaceContext(world, context.getPlayer(), context.getHand(), new ItemStack(blockItem), Ae2Reflect.getHitResult(context));
                    ctxB = blockItem.updatePlacementContext(ctxB);
                    if (ctxB != null && blockItem.place(ctxB) != InteractionResult.FAIL) {
                        var posNew = ctxB.getClickedPos();
                        var te = block.create(posNew, state);
                        if (te != null) {
                            world.setBlock(posNew, state, 3);
                            world.setBlockEntity(te);
                            te.deserializeNBT(ctx.getCompound("ctx"));
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

    public boolean checkNBT(CompoundTag content) {
        return content.contains("part") && content.contains("id") && content.contains("ctx");
    }

}
