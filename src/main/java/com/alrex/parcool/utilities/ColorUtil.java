package com.alrex.parcool.utilities;

public class ColorUtil {
	public static int getColorCodeFromARGB(int a, int r, int g, int b) {
		return a * 0x1000000 + r * 0x10000 + g * 0x100 + b;
	}
}
