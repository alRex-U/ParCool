package com.alrex.parcool.client.hud;

import net.minecraft.util.Tuple;

public class Position {
	public static final Position DEFAULT = new Position(Horizontal.Left, Vertical.Top, 0, 0);
	public static enum Horizontal {Left, Right}

	public static enum Vertical {Top, Bottom}

	public Position(Horizontal alignH, Vertical alignV, int marginH, int marginV) {
		alignmentHorizontal = alignH;
		alignmentVertical = alignV;
		marginHorizontal = marginH;
		marginVertical = marginV;
	}

	private Horizontal alignmentHorizontal;
	private Vertical alignmentVertical;
	private int marginHorizontal;
	private int marginVertical;

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

	public void setAlignmentHorizontal(Horizontal alignmentHorizontal) {
		this.alignmentHorizontal = alignmentHorizontal;
	}

	public void setAlignmentVertical(Vertical alignmentVertical) {
		this.alignmentVertical = alignmentVertical;
	}

	public void setMarginHorizontal(int marginHorizontal) {
		this.marginHorizontal = marginHorizontal;
	}

	public void setMarginVertical(int marginVertical) {
		this.marginVertical = marginVertical;
	}

	public Horizontal getAlignmentHorizontal() {
		return alignmentHorizontal;
	}

	public Vertical getAlignmentVertical() {
		return alignmentVertical;
	}

	public int getMarginHorizontal() {
		return marginHorizontal;
	}

	public int getMarginVertical() {
		return marginVertical;
	}
}
