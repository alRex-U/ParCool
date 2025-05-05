package com.alrex.parcool.common.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;

public record ZiplinePositionComponent(BlockPos pos) {
    public static final Codec<ZiplinePositionComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(BlockPos.CODEC.fieldOf("pos").forGetter(ZiplinePositionComponent::pos))
                    .apply(instance, ZiplinePositionComponent::new)
    );
    public static final StreamCodec<ByteBuf, ZiplinePositionComponent> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ZiplinePositionComponent::pos,
            ZiplinePositionComponent::new
    );
}
