package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.CatLeapAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.common.network.SyncCatLeapMessage;
import com.alrex.parcool.utilities.BufferUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class CatLeap extends Action {
	private boolean leaping = false;
	private int leapingTick = 0;
	private int readyTick = 0;
	private boolean ready = false;
	//flag to apply animation for not local player
	private boolean start = false;

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (leaping) {
			leapingTick++;
		} else {
			leapingTick = 0;
		}
		if (
				(leapingTick > 1 && player.isOnGround()) ||
						player.isFallFlying() ||
						player.isInWaterOrBubble() ||
						player.isInLava()
		) {
			leaping = false;
			leapingTick = 0;
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer()) {
			if (!leaping) leaping = parkourability.getPermission().canCatLeap() &&
					player.isOnGround() &&
					!stamina.isExhausted() &&
					ready && readyTick < 10 &&
					!KeyBindings.getKeySneak().isDown();

			if (ready && leaping) {
				Vector3d motionVec = player.getDeltaMovement();
				Vector3d vec = new Vector3d(motionVec.x(), 0, motionVec.z()).normalize();
				player.setDeltaMovement(vec.x(), parkourability.getActionInfo().getCatLeapPower(), vec.z());
				stamina.consume(parkourability.getActionInfo().getStaminaConsumptionCatLeap(), parkourability.getActionInfo());
			}

			ready = !leaping && ((parkourability.getFastRun().getNotRunningTick() < 10 && KeyBindings.getKeySneak().isDown()) || (ready && KeyBindings.getKeySneak().isDown() && readyTick < 10));
			if (ready) readyTick++;
			else readyTick = 0;
		}
		if (leaping && leapingTick <= 1 || start) {
			start = false;
			Animation animation = Animation.get(player);
			if (animation != null) animation.setAnimator(new CatLeapAnimator());
		}
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {

	}

	@Override
	public boolean needSynchronization(ByteBuffer savedInstanceState) {
		return this.ready != BufferUtil.getBoolean(savedInstanceState)
				|| this.leaping != BufferUtil.getBoolean(savedInstanceState);
	}

	@Override
	public void sendSynchronization(PlayerEntity player) {
		SyncCatLeapMessage.sync(player, this);
	}

	@Override
	public void synchronize(Object message) {
		if (message instanceof SyncCatLeapMessage) {
			SyncCatLeapMessage correctMessage = (SyncCatLeapMessage) message;
			leaping = correctMessage.isLeaping();
			ready = correctMessage.isReady();
			if (leaping) {
				start = true;
			}
		}
	}

	@Override
	public void saveState(ByteBuffer buffer) {
		BufferUtil.wrap(buffer)
				.putBoolean(this.ready)
				.putBoolean(this.leaping);
	}


	public boolean isReady() {
		return ready;
	}

	public boolean isLeaping() {
		return leaping;
	}

	public int getLeapingTick() {
		return leapingTick;
	}
}
