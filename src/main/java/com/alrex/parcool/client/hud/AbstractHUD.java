package com.alrex.parcool.client.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;

public abstract class AbstractHUD extends AbstractGui {
	AbstractHUD(Position pos) {
		position = pos;
	}

	protected Position position;

	public void render(MatrixStack stack) {

	}
}
