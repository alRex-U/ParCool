package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import com.alrex.parcool.common.zipline.ZiplineType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;

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

    public INBT save() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("color", color);
        tag.putByte("type", (byte) getType().ordinal());
        return tag;
    }

    public static ZiplineInfo load(@Nullable INBT tag) {
        if (tag instanceof CompoundNBT) {
            CompoundNBT cTag = (CompoundNBT) tag;
            int color = cTag.contains("color") ? cTag.getInt("color") : ZiplineRopeItem.DEFAULT_COLOR;
            ZiplineType type = cTag.contains("type") ?
                    ZiplineType.values()[cTag.getByte("type") % ZiplineType.values().length] :
                    ZiplineType.QUAD_CURVE;
            return new ZiplineInfo(type, color);
        }
        return new ZiplineInfo(ZiplineType.QUAD_CURVE, ZiplineRopeItem.DEFAULT_COLOR);
    }
}
