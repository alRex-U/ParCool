package com.alrex.parcool.client.gui;


public enum ColorTheme {
	Red(
			0xBB400000,
			0xBB402000,
			0xCC201010,
			0xFFAA6A33,
			0xFFAA3333,
			0xFFCCCCCC,
			0xFF808080,
			0xFFEEEEEE,
			0xFFAAAAAA,
			0xFFFF8888
	),
	Green(
			0xBB004000,
			0xBB004040,
			0xCC101020,
			0xFF33AAAA,
			0xFF33AA33,
			0xFFCCCCCC,
			0xFF808080,
			0xFFEEEEEE,
			0xFFAAAAAA,
			0xFF88FF88
	),
	Blue(
			0xBB000040,
			0xBB400040,
			0xCC101020,
			0xFFAA33AA,
			0xFF3333AA,
			0xFFCCCCCC,
			0xFF808080,
			0xFFEEEEEE,
			0xFFAAAAAA,
			0xFF8888FF
	),
	Yellow(
			0xBB404000,
			0xBB204040,
			0xCC202010,
			0xFF4A9133,
			0xFF919133,
			0xFFCCCCCC,
			0xFF808080,
			0xFFEEEEEE,
			0xFFAAAAAA,
			0xFFFFFF88
	),
	Black(
			0xBB404040,
			0xBB202020,
			0xCC101010,
			0xFF777777,
			0xFF555555,
			0xFFCCCCCC,
			0xFF808080,
			0xFFEEEEEE,
			0xFFAAAAAA,
			0xFFFFFFFF
	);

	ColorTheme(
			int header1,
			int header2,
			int background,
			int topBar1,
			int topBar2,
			int separator,
			int subSeparator,
			int text,
			int subText,
			int strongText
	) {
		this.header1 = header1;
		this.header2 = header2;
		this.background = background;
		this.topBar1 = topBar1;
		this.topBar2 = topBar2;
		this.separator = separator;
		this.subSeparator = subSeparator;
		this.text = text;
		this.subText = subText;
		this.strongText = strongText;
	}

	public int getHeader1() {
		return header1;
	}

	public int getHeader2() {
		return header2;
	}

	public int getBackground() {
		return background;
	}

	public int getSeparator() {
		return separator;
	}

	public int getSubSeparator() {
		return subSeparator;
	}

	public int getSubText() {
		return subText;
	}

	public int getText() {
		return text;
	}

	public int getTopBar1() {
		return topBar1;
	}

	public int getTopBar2() {
		return topBar2;
	}

	public int getStrongText() {
		return strongText;
	}

	private final int header1;
	private final int header2;
	private final int background;
	private final int topBar1;
	private final int topBar2;
	private final int separator;
	private final int subSeparator;
	private final int text;
	private final int subText;
	private final int strongText;
}
