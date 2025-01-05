package com.glodblock.github.extendedae.network.packet;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.client.gui.GuiAssemblerMatrix;
import com.glodblock.github.glodium.network.packet.IMessage;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SAssemblerMatrixUpdate implements IMessage {

    private long patternID;
    private Int2ObjectMap<ItemStack> updateMap;

    public SAssemblerMatrixUpdate() {
        // NO-OP
    }

    public SAssemblerMatrixUpdate(long id, Int2ObjectMap<ItemStack> updateMap) {
        this.patternID = id;
        // deep clone to prevent CME
        this.updateMap = new Int2ObjectOpenHashMap<>(updateMap);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeLong(this.patternID);
        buf.writeInt(this.updateMap.size());
        for (var entry : this.updateMap.int2ObjectEntrySet()) {
            buf.writeInt(entry.getIntKey());
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, entry.getValue());
        }
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        this.patternID = buf.readLong();
        this.updateMap = new Int2ObjectOpenHashMap<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i ++) {
            this.updateMap.put(buf.readInt(), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
        }
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public ResourceLocation id() {
        return ExtendedAE.id("assembler_matrix_update");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onMessage(Player player) {
        if (Minecraft.getInstance().screen instanceof GuiAssemblerMatrix gui) {
            gui.receiveUpdate(this.patternID, this.updateMap);
        }
    }

}
