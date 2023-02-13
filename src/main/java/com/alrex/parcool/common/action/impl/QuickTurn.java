package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class QuickTurn extends Action {
	private static final int AnimationTickLength = 4;
	private boolean turnRightward = false;
	private Vector3d startAngle = null;

	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startInfo) {
		Vector3d angle = player.getLookAngle();
		startInfo
				.putDouble(angle.x())
				.putDouble(angle.z());
		return KeyRecorder.keyQuickTurn.isPressed()
				&& !parkourability.getVault().isDoing()
				&& !parkourability.getRoll().isDoing()
				&& !parkourability.getFlipping().isDoing()
				&& !parkourability.getClingToCliff().isDoing();
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		return getDoingTick() < AnimationTickLength;
	}

	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startData) {
		turnRightward = !turnRightward;
		startAngle = new Vector3d(
				startData.getDouble(),
				0,
				startData.getDouble()
		).normalize();
	}

	@Override
	public void onRenderTick(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
		if (isDoing() && startAngle != null) {
			float renderTick = getDoingTick() + event.renderTickTime;
			float animationPhase = renderTick / AnimationTickLength;
			Vector3d rotatedAngle = startAngle.yRot((float) (Math.PI * animationPhase * (turnRightward ? -1 : 1)));
			player.yRot = (float) VectorUtil.toYawDegree(rotatedAngle);
		}
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
	}
}
