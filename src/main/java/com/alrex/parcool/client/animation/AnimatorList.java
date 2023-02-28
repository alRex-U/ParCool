package com.alrex.parcool.client.animation;

import com.alrex.parcool.client.animation.impl.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AnimatorList {
	public static final List<Class<? extends Animator>> ANIMATORS = Arrays.asList(
			BackwardWallJumpAnimator.class,
			CatLeapAnimator.class,
			ClimbUpAnimator.class,
			ClingToCliffAnimator.class,
			CrawlAnimator.class,
			DiveAnimator.class,
			DodgeAnimator.class,
			FastRunningAnimator.class,
			FlippingAnimator.class,
			HorizontalWallRunAnimator.class,
			JumpFromBarAnimator.class,
			HangAnimator.class,
			KongVaultAnimator.class,
			RollAnimator.class,
			SlidingAnimator.class,
			SpeedVaultAnimator.class,
			TapAnimator.class,
			WallJumpAnimator.class,
			WallSlideAnimator.class
	);
	private static final HashMap<Class<? extends Animator>, Short> INDEX_MAP;

	static {
		INDEX_MAP = new HashMap<>((int) (ANIMATORS.size() * 1.5));
		for (Class<? extends Animator> animator : ANIMATORS) {
			INDEX_MAP.put(animator, (short) ANIMATORS.indexOf(animator));
		}
	}

	public static short getIndex(Class<? extends Animator> animator) {
		return INDEX_MAP.getOrDefault(animator, (short) -1);
	}
}
