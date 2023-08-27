package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.capability.impl.Parkourability;
import net.minecraft.world.entity.player.Player;

;

public class ClingToCliffAnimator extends Animator {
	private final float Lean_Angle = 20;

	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return !parkourability.get(ClingToCliff.class).isDoing();
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		switch (parkourability.get(ClingToCliff.class).getFacingDirection()) {
			case ToWall:
				double zAngle = 10 + 20 * Math.sin(24 * parkourability.get(ClingToCliff.class).getArmSwingAmount());
				transformer
						.rotateLeftArm(
								(float) Math.toRadians(-155),
								0,
								(float) Math.toRadians(zAngle)
						)
						.rotateRightArm(
								(float) Math.toRadians(-155),
								0,
								(float) Math.toRadians(-zAngle)
						)
						.makeArmsNatural()
						.rotateRightLeg(0, 0, 0)
						.rotateLeftLeg(0, 0, 0)
						.makeLegsLittleMoving()
						.end();
				break;
			case RightAgainstWall:
				transformer
						.rotateAdditionallyHeadPitch(-10)
						.rotateRightArm(0, 0, (float) Math.toRadians(Lean_Angle))
						.makeArmsNatural()
						.rotateLeftArm(0, 0, (float) Math.toRadians(-100))
						.rotateLeftLeg(6, 0, (float) Math.toRadians(-15))
						.rotateRightLeg(-6, 0, (float) Math.toRadians(-25))
						.end();
				break;
			case LeftAgainstWall:
				transformer
						.rotateAdditionallyHeadPitch(-10)
						.rotateLeftArm(0, 0, (float) Math.toRadians(-Lean_Angle))
						.makeArmsNatural()
						.rotateRightArm(0, 0, (float) Math.toRadians(100))
						.rotateLeftLeg(-6, 0, (float) Math.toRadians(25))
						.rotateRightLeg(6, 0, (float) Math.toRadians(15))
						.end();
		}
	}

	@Override
	public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		ClingToCliff cling = parkourability.get(ClingToCliff.class);
		switch (cling.getFacingDirection()) {
			case RightAgainstWall:
				rotator.startBasedCenter()
						.rotateRollRightward(Lean_Angle)
						.end();
				break;
			case LeftAgainstWall:
				rotator.startBasedCenter()
						.rotateRollRightward(-Lean_Angle)
						.end();
		}
	}
}
