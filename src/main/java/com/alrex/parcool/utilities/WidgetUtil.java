package com.alrex.parcool.utilities;

import net.minecraft.client.gui.components.AbstractWidget;

public class WidgetUtil {
	public static void setX(AbstractWidget widget, int x) {
		widget.x = x;
	}

	public static void setY(AbstractWidget widget, int y) {
		widget.y = y;
	}

	public static void setWidth(AbstractWidget widget, int width) {
		widget.setWidth(width);
	}

	public static void setHeight(AbstractWidget widget, int height) {
		widget.setHeight(height);
	}
}
