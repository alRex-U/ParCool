package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.ClimbUpAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.Vec3Wrapper;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class ClimbUp extends Action {
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		ClingToCliff cling = parkourability.get(ClingToCliff.class);
		return cling.isDoing()
				&& cling.getDoingTick() > 2
				&& cling.getFacingDirection() == ClingToCliff.FacingDirection.ToWall
				&& KeyRecorder.keyJumpState.isPressed();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinue(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		return getDoingTick() < 2;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
        Vec3Wrapper speed = player.getDeltaMovement();
        player.setDeltaMovement(speed.x(), 0.6, speed.z());
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.CLING_TO_CLIFF_JUMP.get(), 1f, 1f);
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new ClimbUpAnimator());
	}

	@Override
	public void onStartInOtherClient(PlayerWrapper player, Parkourability parkourability, ByteBuffer startData) {
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.CLING_TO_CLIFF_JUMP.get(), 1f, 1f);
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new ClimbUpAnimator());
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}
}
