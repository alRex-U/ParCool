package com.alrex.parcool.client.animation;

import com.alrex.parcool.api.compatibility.AxisWrapper;
import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.api.compatibility.Vec3Wrapper;
import com.alrex.parcool.api.unstable.animation.AnimationOption;
import com.mojang.blaze3d.matrix.MatrixStack;

public class PlayerModelRotator {
	private final MatrixStack stack;
	private final PlayerWrapper player;
	private final float partial;
	private AnimationOption option = new AnimationOption();
	private double playerHeight = 1.8;
	private final float givenXRot, givenYRot, givenZRot;

	public float getXRot() {
		return givenXRot;
	}

	public float getYRot() {
		return givenYRot;
	}

	public float getZRot() {
		return givenZRot;
	}

	public float getPartialTick() {
		return partial;
	}

	private boolean basedCenter = false;
	private boolean basedTop = false;

	public PlayerModelRotator(MatrixStack stack, PlayerWrapper player, float partial, float xRot, float yRot, float zRot) {
		this.stack = stack;
		this.player = player;
		this.partial = partial;
		this.givenXRot = xRot;
		this.givenYRot = yRot;
		this.givenZRot = zRot;
		switch (player.getPose()) {
			case SWIMMING:
			case CROUCHING:
			case SLEEPING:
				playerHeight = 0.6;
		}
	}

	public void setOption(AnimationOption option) {
		this.option = option;
	}

	public PlayerModelRotator start() {
		return this;
	}

	public PlayerModelRotator startBasedCenter() {
		basedCenter = true;
		stack.translate(0, playerHeight / 2, 0);
		return this;
	}

	public PlayerModelRotator startBasedTop() {
		basedTop = true;
		stack.translate(0, playerHeight, 0);
		return this;
	}

	public MatrixStack getRawStack() {
		return stack;
	}

	public PlayerModelRotator translateY(float offset) {
		stack.translate(0, offset, 0);
		return this;
	}

	public PlayerModelRotator translate(float offsetX, float offsetY, float offsetZ) {
		stack.translate(offsetX, offsetY, offsetZ);
		return this;
	}

	public PlayerModelRotator rotatePitchFrontward(float angleDegree) {
		stack.mulPose(AxisWrapper.XN.rotationDegrees(angleDegree));
		return this;
	}

	public PlayerModelRotator rotateRollRightward(float angleDegree) {
		stack.mulPose(AxisWrapper.ZN.rotationDegrees(angleDegree));
		return this;
	}

	public PlayerModelRotator rotateYawRightward(float angleDegree) {
		stack.mulPose(AxisWrapper.YN.rotationDegrees(angleDegree));
		return this;
	}

	public PlayerModelRotator rotate(float angle, AxisWrapper axis) {
		stack.mulPose(axis.rotation(angle));
		return this;
	}

	public PlayerModelRotator rotateDegrees(float angleDegree, AxisWrapper axis) {
		stack.mulPose(axis.rotationDegrees(angleDegree));
		return this;
	}

	public void end() {
		if (basedCenter) {
			stack.translate(0, -playerHeight / 2, 0);
		}
		if (basedTop) {
			stack.translate(0, -playerHeight, 0);
		}
	}

	public void endEnabledLegGrounding() {
		end();
	}
}
