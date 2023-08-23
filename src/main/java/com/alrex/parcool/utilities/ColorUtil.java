package com.alrex.parcool.utilities;

public class ColorUtil {
	public static int getColorCodeFromARGB(int a, int r, int g, int b) {
		return a * 0x1000000 + r * 0x10000 + g * 0x100 + b;
	}

	public static int multiple(int value, double scale) {
		int a = ((value & 0xFF000000) >> 24) & 0xFF,
				r = (value & 0x00FF0000) >> 16,
				g = (value & 0x0000FF00) >> 8,
				b = value & 0x000000FF;
		r = (int) Math.min(0xFF, r * scale);
		g = (int) Math.min(0xFF, g * scale);
		b = (int) Math.min(0xFF, b * scale);
		return getColorCodeFromARGB(a, r, g, b);
	}
}
