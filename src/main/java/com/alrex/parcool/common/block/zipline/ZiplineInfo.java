package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import com.alrex.parcool.common.zipline.ZiplineType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;

public class ZiplineInfo {
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

    public Tag save() {
        var tag = new CompoundTag();
        tag.putInt("color", color);
        tag.putByte("type", (byte) getType().ordinal());
        return tag;
    }

    public static ZiplineInfo load(@Nullable Tag tag) {
        if (tag instanceof CompoundTag cTag) {
            int color = cTag.getInt("color").orElse(ZiplineRopeItem.DEFAULT_COLOR);
            ZiplineType type = ZiplineType.values()[cTag.getByte("type").orElse((byte) ZiplineType.LOOSE.ordinal()) % ZiplineType.values().length];

            return new ZiplineInfo(type, color);
        }
        return new ZiplineInfo(ZiplineType.LOOSE, ZiplineRopeItem.DEFAULT_COLOR);
    }
}
