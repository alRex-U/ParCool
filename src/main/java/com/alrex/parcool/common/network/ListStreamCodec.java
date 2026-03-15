package com.alrex.parcool.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ListStreamCodec<B extends ByteBuf, R> implements StreamCodec<B, List<R>> {
    private final StreamCodec<B, R> CODEC;

    public ListStreamCodec(StreamCodec<B, R> codec) {
        CODEC = codec;
    }

    @Nonnull
    @Override
    public List<R> decode(B b) {
        int count = b.readInt();
        var list = new ArrayList<R>();
        for (int i = 0; i < count; i++) {
            list.addLast(CODEC.decode(b));
        }
        return list;
    }

    @Override
    public void encode(B b, List<R> rs) {
        b.writeInt(rs.size());
        for (R elem : rs) {
            CODEC.encode(b, elem);
        }
    }
}
