package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.compatibility.ClientPlayerWrapper;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.Vec3Wrapper;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class SkyDive extends Action {
	@Override
	public boolean canStart(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		return parkourability.get(Dive.class).getDoingTick() > 15
				&& !stamina.isExhausted()
				&& getNotDoingTick() > 20
				&& KeyRecorder.keyJumpState.isPressed();
	}

	@Override
	public boolean canContinue(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		return parkourability.get(Dive.class).isDoing() && !KeyRecorder.keyJumpState.isPressed();
	}

	@Override
	public void onWorkingTickInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		if (!player.isLocalPlayer()) return;
		ClientPlayerWrapper clientPlayer = ClientPlayerWrapper.get(player);
		Vec3Wrapper forwardVec = VectorUtil.fromYawDegree(player.getYHeadRot());
		Vec3Wrapper leftVec = forwardVec.yRot((float) Math.PI / 2).scale(clientPlayer.getLeftImpulse() * 0.0);
		forwardVec = forwardVec.scale(clientPlayer.getForwardImpulse() * 0.03);
		clientPlayer.setDeltaMovement(clientPlayer.getDeltaMovement()
				.multiply(1, 0.98, 1).add(
						forwardVec.add(leftVec)
				));
	}

	@Override
	public void onRenderTick(TickEvent.RenderTickEvent event, PlayerWrapper player, Parkourability parkourability) {
		if (isDoing()) player.setYBodyRot(player.getYHeadRot());
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.None;
	}
}
