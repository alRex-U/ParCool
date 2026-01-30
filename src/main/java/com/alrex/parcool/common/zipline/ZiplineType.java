package com.alrex.parcool.common.zipline;

import com.alrex.parcool.common.zipline.impl.GeneralQuadraticCurveZipline;
import com.alrex.parcool.common.zipline.impl.StraightZipline;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;

public enum ZiplineType {
    STRAIGHT("parcool.gui.text.zipline.type.tight"),
    STANDARD("parcool.gui.text.zipline.type.normal"),
    LOOSE("parcool.gui.text.zipline.type.loose"),
    VERY_LOOSE("parcool.gui.text.zipline.type.very_loose");

    public static final Codec<ZiplineType> CODEC = ExtraCodecs.idResolverCodec(ZiplineType::ordinal, i -> ZiplineType.values()[i], 0);

    private ZiplineType(String translation) {
        this.translationID = translation;
    }

    private final String translationID;

    public Component getTranslationName() {
        return Component.translatable(translationID);
    }

    public Zipline getZipline(Vec3 point1, Vec3 point2) {
        if (this == STRAIGHT) {
            return new StraightZipline(point1, point2);
        } else if (this == STANDARD) {
            if (Math.abs(point1.y() - point2.y()) < 0.0001)
                return new StraightZipline(point1, point2);
            else
                return new GeneralQuadraticCurveZipline(point1, point2, 0);
        } else if (this == LOOSE) {
            return new GeneralQuadraticCurveZipline(point1, point2, 0.35 + 0.035 * point2.distanceTo(point1));
        } else if (this == VERY_LOOSE) {
            return new GeneralQuadraticCurveZipline(point1, point2, 0.6 + 0.06 * point2.distanceTo(point1));
        }
        return new StraightZipline(point1, point2);
    }
}
