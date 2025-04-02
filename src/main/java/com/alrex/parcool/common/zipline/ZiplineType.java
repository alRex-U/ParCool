package com.alrex.parcool.common.zipline;

import com.alrex.parcool.common.zipline.impl.GeneralQuadraticCurveZipline;
import com.alrex.parcool.common.zipline.impl.QuadraticCurveZipline;
import com.alrex.parcool.common.zipline.impl.StraightZipline;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum ZiplineType {
    STRAIGHT("parcool.gui.text.zipline.type.tight"),
    STANDARD("parcool.gui.text.zipline.type.normal"),
    LOOSE("parcool.gui.text.zipline.type.loose");

    private ZiplineType(String translation) {
        this.translationID = translation;
    }

    private final String translationID;

    public ITextComponent getTranslationName() {
        return new TranslationTextComponent(translationID);
    }

    public Zipline getZipline(Vector3d point1, Vector3d point2) {
        if (this == STRAIGHT) {
            return new StraightZipline(point1, point2);
        } else if (this == STANDARD) {
            if (Math.abs(point1.y() - point2.y()) < 0.0001)
                return new StraightZipline(point1, point2);
            else
                return new QuadraticCurveZipline(point1, point2);
        } else if (this == LOOSE) {
            return new GeneralQuadraticCurveZipline(point1, point2);
        }
        return new StraightZipline(point1, point2);
    }
}
