package com.alrex.parcool.utilities;

public class Easing {
	float phase;
	float result;
	boolean calculated = false;

	public Easing(float phase) {
		this.phase = phase;
	}

	public boolean isInRange(float start, float until) {
		return start <= phase && phase < until;
	}

	public Easing sinInOut(float start, float until, float from, float to) {
		if (calculated) return this;
		if (!isInRange(start, until)) return this;
		float offset = phase - start;
		result = MathUtil.lerp(from, to, EasingFunctions.SinInOutBySquare(offset / (until - start)));
		calculated = true;
		return this;
	}

	public Easing cubicInOut(float start, float until, float from, float to) {
		if (calculated) return this;
		if (!isInRange(start, until)) return this;
		float offset = phase - start;
		result = MathUtil.lerp(from, to, EasingFunctions.CubicInOut(offset / (until - start)));
		calculated = true;
		return this;
	}

	public Easing squareIn(float start, float until, float from, float to) {
		if (calculated) return this;
		if (!isInRange(start, until)) return this;
		float offset = phase - start;
		float inPhase = offset / (until - start);
		result = MathUtil.lerp(from, to, inPhase * inPhase);
		calculated = true;
		return this;
	}

	public Easing squareOut(float start, float until, float from, float to) {
		if (calculated) return this;
		if (!isInRange(start, until)) return this;
		float offset = phase - start;
		float inPhase = 1 - offset / (until - start);
		result = MathUtil.lerp(from, to, 1 - inPhase * inPhase);
		calculated = true;
		return this;
	}

	public Easing linear(float start, float until, float from, float to) {
		if (calculated) return this;
		if (!isInRange(start, until)) return this;
		float offset = phase - start;
		float inPhase = offset / (until - start);
		result = MathUtil.lerp(from, to, inPhase);
		calculated = true;
		return this;
	}

	public float get() {
		if (calculated) {
			return result;
		}
		return phase;
	}
}
