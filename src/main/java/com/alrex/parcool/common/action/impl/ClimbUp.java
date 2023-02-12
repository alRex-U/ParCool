package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.ClimbUpAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.utilities.EntityUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class ClimbUp extends Action {
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startInfo) {
		ClingToCliff cling = parkourability.getClingToCliff();
		return cling.isDoing()
				&& cling.getDoingTick() > 2
				&& parkourability.getPermission().canClingToCliff()
				&& KeyRecorder.keyJumpState.isPressed();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		return getDoingTick() < 2;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startData) {
		EntityUtil.addVelocity(player, new Vector3d(0, 0.6, 0));
		stamina.consume(parkourability.getActionInfo().getStaminaConsumptionClimbUp(), player);
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new ClimbUpAnimator());
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
	}
}
