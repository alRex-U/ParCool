package com.alrex.parcool.common.zipline;

import com.alrex.parcool.common.item.misc.ZiplineRopeItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;

public record ZiplineInfo(ZiplineType type, int color) {

    public Tag save() {
        var tag = new CompoundTag();
        tag.putInt("color", color);
        tag.putByte("type", (byte) type().ordinal());
        return tag;
    }

    public static ZiplineInfo load(@Nullable Tag tag) {
        if (tag instanceof CompoundTag cTag) {
            int color = cTag.contains("color") ? cTag.getInt("color") : ZiplineRopeItem.DEFAULT_COLOR;
            ZiplineType type = cTag.contains("type") ?
                    ZiplineType.values()[cTag.getByte("type") % ZiplineType.values().length] :
                    ZiplineType.LOOSE;
            return new ZiplineInfo(type, color);
        }
        return new ZiplineInfo(ZiplineType.LOOSE, ZiplineRopeItem.DEFAULT_COLOR);
    }
}
