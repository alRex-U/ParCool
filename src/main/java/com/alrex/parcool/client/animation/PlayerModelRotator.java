package com.alrex.parcool.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.world.entity.player.Player;

public class PlayerModelRotator {
	private final PoseStack stack;
	private final Player player;
	private final float partial;
	private double playerHeight = 1.8;

	public float getPartialTick() {
		return partial;
	}

	private boolean basedCenter = false;
	private boolean basedTop = false;

	public PlayerModelRotator(PoseStack stack, Player player, float partial) {
		this.stack = stack;
		this.player = player;
		this.partial = partial;
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
