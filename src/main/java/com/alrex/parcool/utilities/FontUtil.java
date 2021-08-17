package com.alrex.parcool.utilities;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.text.ITextProperties;

public class FontUtil {
	public static void drawCenteredText(MatrixStack stack, ITextProperties text, int x, int y, int color) {
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		int width = fontRenderer.getStringWidth(text.getString());
		IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
		fontRenderer.renderString(text.getString(), x - (width >> 1), y - (fontRenderer.FONT_HEIGHT >> 1), color, false, stack.getLast().getMatrix(), renderTypeBuffer, true, 0, 15728880);
		renderTypeBuffer.finish();
	}
}
