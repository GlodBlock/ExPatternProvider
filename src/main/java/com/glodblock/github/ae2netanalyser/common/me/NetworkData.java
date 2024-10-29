package com.glodblock.github.ae2netanalyser.common.me;

import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.ae2netanalyser.common.me.netdata.NodeFlag;
import com.glodblock.github.ae2netanalyser.common.me.netdata.LinkFlag;
import com.glodblock.github.ae2netanalyser.common.me.netdata.State;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NetworkData {

    public ANode[] nodes;
    public ALink[] links;
    private boolean isCorrupt;
    private final Object2IntMap<ANode> nodeMap = new Object2IntOpenHashMap<>();

    public NetworkData(ANode[] nodes, ALink[] links) {
        this.nodes = nodes;
        this.links = links;
        this.isCorrupt = false;
        for (int i = 0; i < nodes.length; i ++) {
            this.nodeMap.put(nodes[i], i);
        }
    }

    public int countNode(NodeFlag type) {
        if (this.isCorrupt) {
            return 0;
        }
        int cnt = 0;
        for (var n : this.nodes) {
            if (n.state.get() == type) {
                cnt ++;
            }
        }
        return cnt;
    }

    public boolean isCorrupt() {
        return this.isCorrupt;
    }

    private NetworkData() {
        this.isCorrupt = false;
    }

    public static NetworkData readBytes(FriendlyByteBuf buf) {
        var data = new NetworkData();
        try (var stream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteBufInputStream(buf))))) {
            int len = stream.readInt();
            data.nodes = new ANode[len];
            for (int i = 0; i < len; i ++) {
                data.nodes[i] = new ANode(BlockPos.of(stream.readLong()), new State<>(NodeFlag.byIndex(stream.readByte())));
            }
            len = stream.readInt();
            data.links = new ALink[len];
            for (int i = 0; i < len; i ++) {
                int a = stream.readInt();
                int b = stream.readInt();
                data.links[i] = new ALink(data.nodes[a], data.nodes[b], stream.readShort(), new State<>(LinkFlag.byIndex(stream.readByte())));
            }
        } catch (IOException | NullPointerException e) {
            AEAnalyser.LOGGER.error("Fail to analyse the network. The packet is corrupted!", e);
            data.isCorrupt = true;
        }
        // clear corrupted byte
        buf.clear();
        return data;
    }

    public void writeBytes(FriendlyByteBuf buf) {
        try (var stream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(new ByteBufOutputStream(buf))))) {
            stream.writeInt(this.nodes.length);
            for (var n : this.nodes) {
                stream.writeLong(n.pos.asLong());
                stream.writeByte(n.state.get().ordinal());
            }
            stream.writeInt(this.links.length);
            for (var l : this.links) {
                int a = this.nodeMap.getInt(l.a);
                int b = this.nodeMap.getInt(l.b);
                stream.writeInt(a);
                stream.writeInt(b);
                stream.writeShort(l.channel);
                stream.writeByte(l.state.get().ordinal());
            }
        } catch (IOException | NullPointerException e) {
            AEAnalyser.LOGGER.error("Fail to analyse the network. The packet is corrupted!", e);
            this.isCorrupt = true;
        }
    }

    public record ANode(BlockPos pos, State<NodeFlag> state) {

    }

    public record ALink(ANode a, ANode b, short channel, State<LinkFlag> state) {

    }

}
