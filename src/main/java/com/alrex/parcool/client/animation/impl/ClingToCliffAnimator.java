package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.impl.Parkourability;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.player.Player;

public class ClingToCliffAnimator extends Animator {
	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return !parkourability.getClingToCliff().isCling();
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		double zAngle = 10 + 20 * Math.sin(24 * parkourability.getClingToCliff().getArmSwingAmount());
		transformer
				.rotateLeftArm(
						(float) Math.toRadians(-160f),
						0,
						(float) Math.toRadians(zAngle)
				)
				.rotateRightArm(
						(float) Math.toRadians(-160),
						0,
						(float) Math.toRadians(-zAngle)
				)
				.makeArmsNatural()
				.makeLegsLittleMoving();
		PlayerModel model = transformer.getRawModel();
		model.leftLeg.xRot /= 3;
		model.leftLeg.yRot /= 3;
		model.leftLeg.zRot /= 3;
		model.rightLeg.xRot /= 3;
		model.rightLeg.yRot /= 3;
		model.rightLeg.zRot /= 3;
		transformer
				.end();
	}
}
