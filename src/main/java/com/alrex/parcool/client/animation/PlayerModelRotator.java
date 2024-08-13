package com.alrex.parcool.client.animation;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3f;

public class PlayerModelRotator {
	private final MatrixStack stack;
	private final PlayerEntity player;
	private final float partial;
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

	public PlayerModelRotator(MatrixStack stack, PlayerEntity player, float partial, float xRot, float yRot, float zRot) {
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
		stack.mulPose(Vector3f.XN.rotationDegrees(angleDegree));
		return this;
	}

	public PlayerModelRotator rotateRollRightward(float angleDegree) {
		stack.mulPose(Vector3f.ZN.rotationDegrees(angleDegree));
		return this;
	}

	public PlayerModelRotator rotateYawRightward(float angleDegree) {
		stack.mulPose(Vector3f.YN.rotationDegrees(angleDegree));
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
