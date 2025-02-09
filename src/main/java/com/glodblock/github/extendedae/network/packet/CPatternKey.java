package com.glodblock.github.extendedae.network.packet;

import appeng.api.crafting.PatternDetailsHelper;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.container.pattern.PatternGuiHandler;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CPatternKey implements IMessage {

    private ItemStack pattern;
    private static long nextWarning = -1;

    public CPatternKey() {
        // NO-OP
    }

    public CPatternKey(ItemStack stack) {
        this.pattern = stack;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        ItemStack.STREAM_CODEC.encode(buf, this.pattern);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        this.pattern = ItemStack.STREAM_CODEC.decode(buf);
    }

    @Override
    public void onMessage(Player player) {
        var details = PatternDetailsHelper.decodePattern(this.pattern, player.level());
        if (!(details != null && PatternGuiHandler.open(player, details, this.pattern))) {
            if (nextWarning < System.currentTimeMillis()) {
                nextWarning = System.currentTimeMillis() + 2000;
                player.sendSystemMessage(Component.translatable("chat.pattern_view.error", "https://github.com/GlodBlock/ExtendedAE/issues"));
            }
        }
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ExtendedAE.id("c_pattern_key");
    }

}
