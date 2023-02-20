package com.alrex.parcool.client.animation;

import com.alrex.parcool.utilities.VectorUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class PlayerModelRotator {
	private final MatrixStack stack;
	private final PlayerEntity player;
	private final float partial;

	public float getPartialTick() {
		return partial;
	}

	private boolean basedCenter = false;
	private boolean basedTop = false;
	private boolean legGrounding = false;

	private float angleFront = 0;

	public PlayerModelRotator(MatrixStack stack, PlayerEntity player, float partial) {
		this.stack = stack;
		this.player = player;
		this.partial = partial;
	}

	public PlayerModelRotator start() {
		return this;
	}

	public PlayerModelRotator startBasedCenter() {
		basedCenter = true;
		stack.translate(0, player.getBbHeight() / 2, 0);
		return this;
	}

	public PlayerModelRotator startBasedTop() {
		basedTop = true;
		stack.translate(0, player.getBbHeight(), 0);
		return this;
	}

	public PlayerModelRotator rotateFrontward(float angleDegree) {
		Vector3d lookVec = VectorUtil.fromYawDegree(player.yBodyRot).yRot((float) Math.PI / 2);
		Vector3f vec = new Vector3f((float) lookVec.x(), 0, (float) lookVec.z());
		angleFront += angleDegree;
		stack.mulPose(vec.rotationDegrees(angleDegree));
		return this;
	}

	public PlayerModelRotator rotateRightward(float angleDegree) {
		Vector3d lookVec = VectorUtil.fromYawDegree(player.yBodyRot);
		Vector3f vec = new Vector3f((float) lookVec.x(), 0, (float) lookVec.z());
		stack.mulPose(vec.rotationDegrees(angleDegree));
		return this;
	}

	public void end() {
		if (basedCenter) {
			stack.translate(0, -player.getBbHeight() / 2, 0);
		}
		if (basedTop) {
			stack.translate(0, -player.getBbHeight(), 0);
		}
	}

	public void endEnabledLegGrounding() {
		end();
	}
}
