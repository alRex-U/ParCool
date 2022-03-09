package com.alrex.parcool.utilities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class FontUtil {
	public static void drawCenteredText(PoseStack stack, String text, int x, int y, int color) {
		Font fontRenderer = Minecraft.getInstance().font;
		int width = fontRenderer.width(text);
		fontRenderer.draw(
				stack,
				text,
				x - (width >> 1),
				y - (fontRenderer.lineHeight >> 1),
				color
		);
	}
}
