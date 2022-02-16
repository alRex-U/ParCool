package com.alrex.parcool.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;

public abstract class Widget {
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;

	protected Widget(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public boolean contains(double x, double y) {
		return this.x < x && x < this.x + width && this.y < y && y < this.y + height;
	}

	abstract public void render(MatrixStack stack, FontRenderer fontRenderer, int mouseX, int mouseY, float partial);

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}
