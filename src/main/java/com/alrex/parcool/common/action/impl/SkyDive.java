package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

import java.nio.ByteBuffer;

public class SkyDive extends Action {
	@Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
		return parkourability.get(Dive.class).getDoingTick() > 15
				&& getNotDoingTick() > 20
				&& KeyRecorder.keyJumpState.isPressed();
	}

	@Override
    public boolean canContinue(Player player, Parkourability parkourability) {
		return parkourability.get(Dive.class).isDoing() && !KeyRecorder.keyJumpState.isPressed();
	}

	@Override
    public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
        if (!(player instanceof LocalPlayer clientPlayer)) {
			return;
		}
		Vec3 forwardVec = VectorUtil.fromYawDegree(player.yHeadRot);
		Vec3 leftVec = forwardVec.yRot((float) Math.PI / 2).scale(clientPlayer.input.getMoveVector().x * 0.0);
		forwardVec = forwardVec.scale(clientPlayer.input.getMoveVector().y * 0.03);
		clientPlayer.setDeltaMovement(clientPlayer.getDeltaMovement()
				.multiply(1, 0.98, 1).add(
						forwardVec.add(leftVec)
				));
	}

	@Override
    public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
		if (isDoing()) player.setYBodyRot(player.yHeadRot);
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.None;
	}
}
