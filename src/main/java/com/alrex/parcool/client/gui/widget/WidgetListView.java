package com.alrex.parcool.client.gui.widget;

import com.alrex.parcool.utilities.WidgetUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.Tuple;

import javax.annotation.Nullable;
import java.util.List;

public class WidgetListView<T extends net.minecraft.client.gui.widget.Widget> extends Widget {
	List<T> items;
	private int scrollValue = 0;
	private final int itemHeight;

	public WidgetListView(int x, int y, int width, int height, List<T> items, int itemHeight) {
		super(x, y, width, height);
		this.items = items;
		this.itemHeight = itemHeight;
	}

	@Override
	public void render(MatrixStack stack, FontRenderer fontRenderer, int mouseX, int mouseY, float partial) {
		int renderingY = 0;
		for (int i = scrollValue; i < items.size(); i++) {

			T item = items.get(i);

			WidgetUtil.setX(item, x);
			WidgetUtil.setY(item, y + renderingY);
			WidgetUtil.setWidth(item, width);
			WidgetUtil.setHeight(item, itemHeight);
			if (renderingY + item.getHeight() > this.height) break;

			item.func_230430_a_(stack, mouseX, mouseY, partial);

			renderingY += item.getHeight();
		}

	}

	public void scroll(int value) {
		scrollValue += value;
		int max = items.size() - (height / itemHeight);
		if (scrollValue > max) scrollValue = max;
		if (scrollValue < 0) scrollValue = 0;
	}

	@Nullable
	public Tuple<Integer, T> clicked(double mouse_X, double mouse_Y, int type) {
		if (mouse_X < x || x + width < mouse_X) return null;
		if (mouse_Y < y || y + height < mouse_Y) return null;

		int index = scrollValue + (int) Math.floor((mouse_Y - y) / itemHeight);
		if (index < items.size()) {
			return new Tuple<>(index, items.get(index));
		}
		return null;
	}

}
