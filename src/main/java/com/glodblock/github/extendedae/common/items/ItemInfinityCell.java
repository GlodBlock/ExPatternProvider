package com.glodblock.github.extendedae.common.items;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.items.AEBaseItem;
import appeng.items.storage.StorageCellTooltipComponent;
import com.glodblock.github.extendedae.util.LazyInits;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ItemInfinityCell extends AEBaseItem {

    private AEKey record;

    public ItemInfinityCell(@NotNull Supplier<AEKey> type) {
        super(new Item.Properties().stacksTo(1));
        LazyInits.addFinal(() -> this.record = type.get());
    }

    public ItemInfinityCell(@NotNull Supplier<AEKey> type, Properties properties) {
        super(properties);
        LazyInits.addFinal(() -> this.record = type.get());
    }

    public AEKey getRecord() {
        return this.record;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack is) {
        return Component.translatable("item.extendedae.infinity_cell_name", this.record.getDisplayName());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack is, Item.@NotNull TooltipContext ctx, @NotNull List<Component> lines, @NotNull TooltipFlag adv) {
        lines.add(Component.translatable("infinity.tooltip").withStyle(ChatFormatting.GREEN));
    }

    @NotNull
    @Override
    public Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        var content = Collections.singletonList(new GenericStack(this.record, getAsIntMax(this.record)));
        return Optional.of(new StorageCellTooltipComponent(List.of(), content, false, true));
    }

    public static long getAsIntMax(AEKey key) {
        return (long) Integer.MAX_VALUE * key.getAmountPerUnit();
    }

}
