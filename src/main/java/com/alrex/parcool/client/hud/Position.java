package com.alrex.parcool.client.hud;

import net.minecraft.util.Tuple;

public class Position {
	public static final Position DEFAULT = new Position(Horizontal.Left, Vertical.Top, 0, 0);
	public enum Horizontal {Left, Right}

	public enum Vertical {Top, Bottom}

	public Position(Horizontal alignH, Vertical alignV, int marginH, int marginV) {
		alignmentHorizontal = alignH;
		alignmentVertical = alignV;
		marginHorizontal = marginH;
		marginVertical = marginV;
	}

	private final Horizontal alignmentHorizontal;
	private final Vertical alignmentVertical;
	private final int marginHorizontal;
	private final int marginVertical;

	//return position of the hud's top of left
	public Tuple<Integer, Integer> calculate(int width, int height, int screenWidth, int screenHeight) {
		int x;
		int y;
		if (alignmentHorizontal == Horizontal.Right) {
			x = screenWidth - marginHorizontal - width;
		} else {
			x = marginHorizontal;
		}
		if (alignmentVertical == Vertical.Bottom) {
			y = screenHeight - marginVertical - height;
		} else {
			y = marginVertical;
		}
		return new Tuple<>(x, y);
	}
}
