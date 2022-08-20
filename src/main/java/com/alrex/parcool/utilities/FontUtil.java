package com.alrex.parcool.utilities;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.text.ITextProperties;

public class FontUtil {
	public static void drawCenteredText(MatrixStack stack, ITextProperties text, int x, int y, int color) {
		FontRenderer fontRenderer = Minecraft.getInstance().font;
		int width = fontRenderer.width(text.getString());
		IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
		fontRenderer.draw(
				stack,
				text.getString(),
				x - (width >> 1),
				y - (fontRenderer.lineHeight >> 1),
				color
		);
		renderTypeBuffer.endBatch();
	}
}
