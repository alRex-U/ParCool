package com.alrex.parcool.client.animation;

import com.alrex.parcool.utilities.MathUtil;
import com.alrex.parcool.utilities.VectorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

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

	public PlayerModelRotator rotatePitchFrontward(float angleDegree) {
		Vec3 lookVec;
		if (player.isLocalPlayer()) {
			lookVec = VectorUtil.fromYawDegree(MathUtil.lerp(player.yBodyRotO, player.yBodyRot, getPartialTick())).yRot((float) Math.PI / 2);
		} else {
			lookVec = VectorUtil.fromYawDegree(player.yBodyRot).yRot((float) Math.PI / 2);
		}
		stack.mulPose(Axis.of(lookVec.toVector3f()).rotationDegrees(angleDegree));
		return this;
	}

	public PlayerModelRotator rotateRollRightward(float angleDegree) {
		Vec3 lookVec;
		if (player.isLocalPlayer()) {
			lookVec = VectorUtil.fromYawDegree(MathUtil.lerp(player.yBodyRotO, player.yBodyRot, getPartialTick()));
		} else {
			lookVec = VectorUtil.fromYawDegree(player.yBodyRot);
		}
		Vector3f vec = new Vector3f((float) lookVec.x(), 0, (float) lookVec.z());
		stack.mulPose(Axis.of(lookVec.toVector3f()).rotationDegrees(angleDegree));
		return this;
	}

	public PlayerModelRotator rotateYawRightward(float angleDegree) {
		stack.mulPose(Axis.YN.rotationDegrees(angleDegree));
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
