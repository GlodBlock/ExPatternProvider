package com.glodblock.github.extendedae.network.packet;

import com.glodblock.github.extendedae.client.gui.GuiAssemblerMatrix;
import com.glodblock.github.glodium.network.packet.IMessage;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SAssemblerMatrixUpdate implements IMessage<SAssemblerMatrixUpdate> {

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
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeLong(this.patternID);
        buf.writeInt(this.updateMap.size());
        for (var entry : this.updateMap.int2ObjectEntrySet()) {
            buf.writeInt(entry.getIntKey());
            buf.writeItemStack(entry.getValue(), false);
        }
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        this.patternID = buf.readLong();
        this.updateMap = new Int2ObjectOpenHashMap<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i ++) {
            this.updateMap.put(buf.readInt(), buf.readItem());
        }
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public void onMessage(Player player) {
        if (Minecraft.getInstance().screen instanceof GuiAssemblerMatrix gui) {
            gui.receiveUpdate(this.patternID, this.updateMap);
        }
    }

    @Override
    public Class<SAssemblerMatrixUpdate> getPacketClass() {
        return SAssemblerMatrixUpdate.class;
    }

}
