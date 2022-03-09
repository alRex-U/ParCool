package com.alrex.parcool.client.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public abstract class AbstractHUD {
	public AbstractHUD(Position pos) {
		position = pos;
	}

	protected Position position;

	public void render(ForgeIngameGui gui, PoseStack mStack, float partialTicks, int width, int height) {
	}
}
