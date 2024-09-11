package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.Animation;
import com.alrex.parcool.client.animation.impl.DiveAnimationHostAnimator;
import com.alrex.parcool.client.animation.impl.DiveIntoWaterAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class Dive extends Action {
	private boolean justJumped = false;
    private double initialYVelocityOfLastJump = 0.42;
	private double playerYSpeedOld = 0;
	private double playerYSpeed = 0;
	private int fallingTick = 0;

	public double getPlayerYSpeed(float partialTick) {
		return Mth.lerp(partialTick, playerYSpeedOld, playerYSpeed);
	}

	@Override
    public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
		playerYSpeedOld = playerYSpeed;
		playerYSpeed = player.getDeltaMovement().y();
	}

	@Override
    public void onClientTick(Player player, Parkourability parkourability) {
		if (isDoing() && (playerYSpeed < 0 || fallingTick > 0)) {
			fallingTick++;
		} else {
			fallingTick = 0;
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        if (player.getVehicle() != null) return false;
        boolean startInAir = player.getDeltaMovement().y() < 0
                && parkourability.getAdditionalProperties().getNotLandingTick() > 10
                && parkourability.getAdditionalProperties().getNotInWaterTick() > 30
                && KeyRecorder.keyJumpState.getTickKeyDown() > 10
                && WorldUtil.existsSpaceBelow(player);
        if (!(startInAir || (justJumped && WorldUtil.existsDivableSpace(player) && parkourability.get(FastRun.class).canActWithRunning(player)))) {
            justJumped = false;
            return false;
        }

        startInfo.putDouble(initialYVelocityOfLastJump);
        BufferUtil.wrap(startInfo).putBoolean(startInAir);

        justJumped = false;
        return parkourability.getActionInfo().can(Dive.class)
				&& !parkourability.get(Crawl.class).isDoing()
                && !player.isVisuallyCrawling();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
    public boolean canContinue(Player player, Parkourability parkourability) {
		return !(player.isFallFlying()
				|| player.getAbilities().flying
				|| player.isInWaterOrBubble()
				|| player.isInLava()
				|| player.isSwimming()
				|| player.onGround()
				|| (fallingTick > 5 && player.fallDistance < 0.1)
                || player.getData(Attachments.STAMINA).isExhausted()
		);
	}

    public void onJump(Player player, Parkourability parkourability) {
		if (!player.isLocalPlayer()) return;
        initialYVelocityOfLastJump = player.getDeltaMovement().y();
		justJumped = true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        double initialYSpeed = startData.getDouble();
        playerYSpeedOld = playerYSpeed = initialYSpeed;
		Animation animation = Animation.get(player);
		if (animation != null) {
            animation.setAnimator(new DiveAnimationHostAnimator(initialYSpeed, BufferUtil.getBoolean(startData)));
		}
	}

	@Override
	public void onStop(Player player) {
		if (player.isInWaterOrBubble()) {
            Animation animation = Animation.get(player);
            Parkourability parkourability = Parkourability.get(player);
            if (animation != null
                    && parkourability != null
                    && parkourability.getAdditionalProperties().getNotLandingTick() >= 5
                    && player.getDeltaMovement().y() < 0
            ) {
                animation.setAnimator(new DiveIntoWaterAnimator(parkourability.get(SkyDive.class).isDoing()));
            }
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.None;
	}

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
		buffer.putDouble(playerYSpeed)
				.putDouble(playerYSpeedOld);
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {
		playerYSpeed = buffer.getDouble();
		playerYSpeedOld = buffer.getDouble();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        double initialYSpeed = startData.getDouble();
        playerYSpeedOld = playerYSpeed = initialYVelocityOfLastJump = initialYSpeed;
		Animation animation = Animation.get(player);
		if (animation != null) {
            animation.setAnimator(new DiveAnimationHostAnimator(initialYSpeed, BufferUtil.getBoolean(startData)));
		}
	}
}
