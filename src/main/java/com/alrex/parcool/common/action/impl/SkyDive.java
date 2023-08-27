package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class SkyDive extends Action {
	@Override
	public boolean canStart(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		return parkourability.get(Dive.class).getDoingTick() > 15
				&& !stamina.isExhausted()
				&& getNotDoingTick() > 20
				&& KeyRecorder.keyJumpState.isPressed();
	}

	@Override
	public boolean canContinue(Player player, Parkourability parkourability, IStamina stamina) {
		return parkourability.get(Dive.class).isDoing() && !KeyRecorder.keyJumpState.isPressed();
	}

	@Override
	public void onWorkingTickInLocalClient(Player player, Parkourability parkourability, IStamina stamina) {
		if (!(player instanceof LocalPlayer)) {
			return;
		}
		LocalPlayer clientPlayer = (LocalPlayer) player;
		Vec3 forwardVec = VectorUtil.fromYawDegree(player.yHeadRot);
		Vec3 leftVec = forwardVec.yRot((float) Math.PI / 2).scale(clientPlayer.input.leftImpulse * 0.0);
		forwardVec = forwardVec.scale(clientPlayer.input.forwardImpulse * 0.03);
		clientPlayer.setDeltaMovement(clientPlayer.getDeltaMovement()
				.multiply(1, 0.98, 1).add(
						forwardVec.add(leftVec)
				));
	}

	@Override
	public void onRenderTick(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {
		if (isDoing()) player.setYBodyRot(player.yHeadRot);
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.None;
	}
}
