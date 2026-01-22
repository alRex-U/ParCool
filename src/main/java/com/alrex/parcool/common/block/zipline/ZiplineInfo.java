package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import com.alrex.parcool.common.zipline.ZiplineType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;

public class ZiplineInfo {
    public static final Codec<ZiplineInfo> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ZiplineType.CODEC.optionalFieldOf("type", ZiplineType.LOOSE)
                .forGetter(ZiplineInfo::getType),
            Codec.INT.optionalFieldOf("color", ZiplineRopeItem.DEFAULT_COLOR)
                .forGetter(ZiplineInfo::getColor)
        )
            .apply(instance, ZiplineInfo::new)
    );

    public ZiplineInfo(ZiplineType type, int color) {
        this.color = color;
        this.type = type;
    }

    private final ZiplineType type;

    private final int color;

    public int getColor() {
        return color;
    }

    public ZiplineType getType() {
        return type;
    }
}
