package com.alrex.parcool.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record GuiColorPallet(
        int primary,
        int primaryLight,
        int primaryDark,
        int onPrimary,
        int accent,
        int error,
        int onError,
        int background,
        int onBackground,
        int surface,
        int onSurface,
        int separator,
        int shadow
) {
    public static final GuiColorPallet DEFAULT_LIGHT = new GuiColorPallet(
            0xFF0255EE,
            0xFF718BF5,
            0xFF0041D6,
            0xFFFFFFFF,
            0xFFFFC64B,
            0xFFB00020,
            0xFFFFFFFF,
            0xFFD8D8D8,
            0xFF111111,
            0xFFFFFFFF,
            0xFF111111,
            0xFFE9E9E9,
            0x60808080
    );
    public static final GuiColorPallet DEFAULT_DARK = new GuiColorPallet(
            0xFF0255EE,
            0xFF718BF5,
            0xFF0041D6,
            0xFFFFFFFF,
            0xFFFFC64B,
            0xFFB00020,
            0xFFFFFFFF,
            0xFF33333d,
            0xFFEEEEEE,
            0xFF575766,
            0xFFFFFFFF,
            0xFF454545,
            0x60111111
    );
}
