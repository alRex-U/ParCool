package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.Vec3Wrapper;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class QuickTurn extends Action {
	private static final int AnimationTickLength = 4;
	private boolean turnRightward = false;
	private Vec3Wrapper startAngle = null;

	@Override
	public boolean canStart(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		Vec3Wrapper angle = player.getLookAngle();
		startInfo
				.putDouble(angle.x())
				.putDouble(angle.z());
		return KeyRecorder.keyQuickTurn.isPressed()
				&& !parkourability.get(Vault.class).isDoing()
				&& !parkourability.get(Roll.class).isDoing()
				&& !parkourability.get(Flipping.class).isDoing()
				&& !parkourability.get(ClingToCliff.class).isDoing();
	}

	@Override
	public boolean canContinue(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		return getDoingTick() < AnimationTickLength;
	}

	@Override
	public void onStartInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		turnRightward = !turnRightward;
		startAngle = new Vec3Wrapper(
				startData.getDouble(),
				0,
				startData.getDouble()
		).normalize();
	}

	@Override
	public void onRenderTick(TickEvent.RenderTickEvent event, PlayerWrapper player, Parkourability parkourability) {
		if (isDoing() && startAngle != null) {
			float renderTick = getDoingTick() + event.renderTickTime;
			float animationPhase = renderTick / AnimationTickLength;
			Vec3Wrapper rotatedAngle = startAngle.yRot((float) (Math.PI * animationPhase * (turnRightward ? -1 : 1)));
			player.setYRot(VectorUtil.toYawDegree(rotatedAngle));
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.None;
	}
}
