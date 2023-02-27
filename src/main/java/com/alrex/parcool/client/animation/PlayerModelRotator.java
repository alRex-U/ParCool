package com.alrex.parcool.client.animation;

import com.alrex.parcool.utilities.VectorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class PlayerModelRotator {
	private final PoseStack stack;
	private final Player player;
	private final float partial;

	public float getPartialTick() {
		return partial;
	}

	private boolean basedCenter = false;
	private boolean basedTop = false;
	private boolean legGrounding = false;

	private float angleFront = 0;

	public PlayerModelRotator(PoseStack stack, Player player, float partial) {
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
		Vec3 lookSideVec = VectorUtil.fromYawDegree(player.yBodyRot).yRot((float) Math.PI / 2);
		angleFront += angleDegree;
		stack.mulPose(Axis.of(lookSideVec.toVector3f()).rotationDegrees(angleDegree));
		return this;
	}

	public PlayerModelRotator rotateRightward(float angleDegree) {
		Vec3 lookVec = VectorUtil.fromYawDegree(player.yBodyRot);
		stack.mulPose(Axis.of(lookVec.toVector3f()).rotationDegrees(angleDegree));
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
