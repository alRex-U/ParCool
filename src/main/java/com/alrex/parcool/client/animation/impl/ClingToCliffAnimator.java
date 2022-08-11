package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.Parkourability;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class ClingToCliffAnimator extends Animator {
	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return !parkourability.getClingToCliff().isCling();
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		MatrixStack stack = new MatrixStack();
		transformer
				.rotateLeftArm(
						(float) Math.toRadians(-160f),
						0,
						(float) Math.toRadians(13)
				)
				.rotateRightArm(
						(float) Math.toRadians(-160),
						0,
						(float) Math.toRadians(-13)
				)
				.makeArmsNatural()
				.makeLegsLittleMoving()
				.End();
	}
}
