package com.alrex.parcool.client.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public abstract class AbstractHUD extends AbstractGui {
	public AbstractHUD(Position pos) {
		position = pos;
	}

	protected Position position;

	public void render(RenderGameOverlayEvent.Pre event, MatrixStack stack) {

	}
}
