package com.alrex.parcool.api.action;

import com.alrex.parcool.common.network.ActionStatePacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.function.Supplier;

public class SynchronizedDataHolder {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final SynchronizedDataHolder EMPTY = new SynchronizedDataHolder(new SynchronizedProperty[0], null);

    public static SynchronizedDataHolder empty() {
        return EMPTY;
    }

    private final SynchronizedProperty<?>[] data;
    private final ByteBuffer buffer;

    private SynchronizedDataHolder(SynchronizedProperty<?>[] data, @Nullable ActionEntry<?> holder) {
        this.data = data;
        var maxBufferSize = 0;
        for (var datum : data) {
            maxBufferSize += 1 + datum.getHandler().dataLengthInBytes();
        }
        buffer = ByteBuffer.allocate(maxBufferSize);
    }

    @Nullable
    public ActionStatePacket.Entry packToEntry(ActionStatePacket.Type type, ActionEntry<?> holder) {
        if (this == EMPTY) return packToEntry$returnIfEmpty(type, holder);

        int index = -1;
        while (++index < data.length) {
            if (data[index].isDirty()) break;
        }
        if (index >= data.length) return packToEntry$returnIfEmpty(type, holder);
        buffer.clear();
        do {
            var datum = data[index];
            if (datum.isDirty()) {
                buffer.put((byte) index);
                packToEntry$writeToBuffer(buffer, datum);
                datum.setDirty(false);
            }
        } while (++index < data.length);
        buffer.flip();
        var dataArray = new byte[buffer.limit()];
        buffer.get(dataArray);
        return new ActionStatePacket.Entry(type, holder, dataArray);
    }

    @Nullable
    private ActionStatePacket.Entry packToEntry$returnIfEmpty(ActionStatePacket.Type type, ActionEntry<?> holder) {
        if (type == ActionStatePacket.Type.DATA) return null;
        return new ActionStatePacket.Entry(type, holder, new byte[0]);
    }

    private static <T> void packToEntry$writeToBuffer(ByteBuffer buffer, SynchronizedProperty<T> property) {
        property.getHandler().write(buffer, property.get());
    }

    public void acceptPacket(ActionStatePacket.Entry entry) {
        var packetBuffer = ByteBuffer.wrap(entry.data());
        while (packetBuffer.hasRemaining()) {
            var index = packetBuffer.get();
            if (index < 0 || data.length <= index) {
                LOGGER.error("Invalid packet data: array_length:{} but index:{} is given", data.length, index);
                return;
            }
            var datum = data[index];
            if (packetBuffer.remaining() < datum.getHandler().dataLengthInBytes()) {
                LOGGER.error("Invalid packet data: buffer_remaining:{} but needed_length:{}", packetBuffer.remaining(), datum.getHandler().dataLengthInBytes());
                return;
            }
            acceptPacket$readFromBuffer(packetBuffer, datum);
        }
    }

    private static <T> void acceptPacket$readFromBuffer(ByteBuffer buffer, SynchronizedProperty<T> property) {
        property.sync(property.getHandler().read(buffer));
    }

    public static class Builder {
        private SynchronizedProperty<?>[] data;
        private byte index = 0;

        public Builder(byte size) {
            data = new SynchronizedProperty[size];
        }

        public <T> SynchronizedProperty<T> register(Supplier<SynchronizedProperty<T>> supplier) {
            return (SynchronizedProperty<T>) (data[index++] = supplier.get());
        }

        public SynchronizedDataHolder build(ActionEntry<?> holder) {
            if (index != data.length) {
                throw new IllegalStateException("Count of registered properties is not same to given length");
            }
            return new SynchronizedDataHolder(data, holder);
        }
    }

    public static SynchronizedDataHolder create(ActionEntry<?> entry, SynchronizedProperty<?>... properties) {
        return new SynchronizedDataHolder(properties, entry);
    }
}
