package com.alrex.parcool.utilities;

import net.minecraft.client.gui.widget.Widget;

public class WidgetUtil {
	public static void setX(Widget widget, int x) {
		widget.field_230690_l_ = x;
	}

	public static void setY(Widget widget, int y) {
		widget.field_230691_m_ = y;
	}

	public static void setWidth(Widget widget, int width) {
		widget.func_230991_b_(width);
	}

	public static void setHeight(Widget widget, int height) {
		widget.setHeight(height);
	}
}
