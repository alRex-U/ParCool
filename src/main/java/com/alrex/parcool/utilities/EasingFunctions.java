package com.alrex.parcool.utilities;

/**
 * All Functions take arg1 as phase(0~1) and return value will also be in 0~1
 */
public class EasingFunctions {
	public static float CubicInOut(float phase) {
		if (phase < 0.5) {
			return 4 * phase * phase * phase;
		} else {
			phase = 1 - phase;
			return 1 - 4 * phase * phase * phase;
		}
	}

	public static float SinInOutBySquare(float phase) {
		if (phase < 0.5) {
			return 2 * phase * phase;
		} else {
			return 1 - 2 * (phase - 1) * (phase - 1);
		}
	}
}
