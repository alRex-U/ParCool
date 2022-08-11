package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.player.PlayerEntity;

public class DodgeAnimator extends Animator {
	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return !parkourability.getDodge().isDodging();
	}

	@Override
	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		Dodge dodge = parkourability.getDodge();

		float rotValue = (dodge.getDodgingTick() + rotator.getPartial()) * 30;

		if (dodge.isDodging() && dodge.getDodgeDirection() == Dodge.DodgeDirection.Back) {
			rotValue *= -1;
			rotator
					.startBasedCenter()
					.rotateFrontward(rotValue)
					.End();
		} else if (dodge.isDodging() && dodge.getDodgeDirection() == Dodge.DodgeDirection.Front) {
			rotator
					.startBasedCenter()
					.rotateFrontward(rotValue)
					.End();
		}
	}

}
