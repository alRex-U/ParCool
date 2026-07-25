package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.network.ActionCapabilitiesPacket;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

public class ActionCapabilities {
    private final TreeMap<String, boolean[]> capabilities;
    private boolean dirty;

    public ActionCapabilities(ActionRegistry registry) {
        capabilities = new TreeMap<>();
        for (var group : registry.getRegisteredGroups().entrySet()) {
            capabilities.put(group.getKey(), new boolean[group.getValue().actions().size()]);
        }
    }

    public boolean can(ActionEntry<?> action) {
        var groupCapabilities = capabilities.get(action.id().getNamespace());
        if (groupCapabilities != null && action.index() < groupCapabilities.length) {
            return groupCapabilities[action.index()];
        }
        return false;
    }

    public void set(ActionEntry<?> action, boolean value) {
        var groupCapabilities = capabilities.get(action.id().getNamespace());
        if (groupCapabilities != null && action.index() < groupCapabilities.length) {
            groupCapabilities[action.index()] = value;
            dirty = true;
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void sync(ServerPlayer owner) {
        this.dirty = false;
        ParCool.CONNECTION.send(PacketDistributor.PLAYER.with(() -> owner), new ActionCapabilitiesPacket(this));
    }

    public CompoundTag saveToTag() {
        var tag = new CompoundTag();
        for (var group : capabilities.entrySet()) {
            var groupCapabilities = group.getValue();
            tag.putByteArray(group.getKey(), encodeToByteArray(groupCapabilities));
        }
        return tag;
    }

    public void readFromTag(CompoundTag tag) {
        for (var groupName : tag.getAllKeys()) {
            if (!capabilities.containsKey(groupName)) continue;
            var groupTag = tag.get(groupName);
            if (!(groupTag instanceof ByteArrayTag bytesTag)) continue;
            capabilities.compute(groupName, (k, groupCapabilities) -> decodeFromByteArray(groupCapabilities.length, bytesTag.getAsByteArray()));
            dirty = true;
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeShort(capabilities.size());
        for (var group : capabilities.entrySet()) {
            buf.writeByte(group.getKey().length());
            buf.writeCharSequence(group.getKey(), StandardCharsets.US_ASCII);
            var groupCapabilities = group.getValue();
            var bytes = encodeToByteArray(groupCapabilities);
            buf.writeByteArray(bytes);
        }
    }

    public void read(FriendlyByteBuf buf) {
        var groupSize = buf.readShort();
        for (var i = 0; i < groupSize; i++) {
            var groupName = buf.readCharSequence(buf.readByte(), StandardCharsets.US_ASCII).toString();
            var byteCount = buf.readByte();
            var bytes = new byte[byteCount];
            buf.readBytes(bytes);
            capabilities.put(groupName, decodeFromByteArray(byteCount, bytes));
        }
    }

    private static byte[] encodeToByteArray(boolean[] logicalArray) {
        var byteCount = (logicalArray.length & 0b111) == 0
                ? logicalArray.length >> 3 : logicalArray.length / 8 + 1;
        var bytes = new byte[byteCount];
        for (int i = 0; i < bytes.length; i++) {
            byte data = 0;
            for (var bit = 0; bit < 8; bit++) {
                var idx = 8 * i + bit;
                if (idx >= bytes.length) break;
                data |= (byte) ((logicalArray[idx] ? 1 : 0) << bit);
            }
            bytes[i] = data;
        }
        return bytes;
    }

    private static boolean[] decodeFromByteArray(int size, byte[] byteArray) {
        var out = new boolean[size];
        for (var i = 0; i < byteArray.length; i++) {
            byte data = byteArray[i];
            for (var bit = 0; bit < 8; bit++) {
                var idx = 8 * i + bit;
                if (idx >= out.length) break;
                out[idx] = (data & (0b1 << bit)) != 0;
            }
        }
        return out;
    }

}
