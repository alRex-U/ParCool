package com.alrex.parcool.utilities;

import net.minecraft.client.gui.widget.Widget;

public class WidgetUtil {
	public static void setX(Widget widget, int x) {
		widget.x = x;
	}

	public static void setY(Widget widget, int y) {
		widget.y = y;
	}

	public static void setWidth(Widget widget, int width) {
		widget.setWidth(width);
	}

	public static void setHeight(Widget widget, int height) {
		widget.setHeight(height);
	}
}
