package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.RollAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.common.network.StartRollMessage;
import com.alrex.parcool.common.network.SyncRollMessage;
import com.alrex.parcool.utilities.BufferUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class Roll extends Action {
	public static final int ROLL_DEFERMENT_TICK = 10;

	private float cameraPitch = 0;
	private boolean ready = false;
	private int readyTick = 0;
	private boolean start = false;
	private int readyCoolTick = 0;
	private boolean rolling = false;
	private int rollingTick = 0;

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (rolling) {
			rollingTick++;
			if (rollingTick >= getRollMaxTick()) rolling = false;
		} else {
			rollingTick = 0;
		}
		if (ready) {
			readyTick--;
			if (readyTick <= 0) ready = false;
		} else {
			readyTick = 0;
		}
		if (readyCoolTick > 0) {
			readyCoolTick--;
		} else {
			readyCoolTick = 0;
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (player.isUser()) {
			if (
					!ready
							&& parkourability.getPermission().canRoll()
							&& KeyRecorder.keyCrawlState.isPressed()
							&& !player.collidedVertically
							&& readyCoolTick <= 0
			) {
				ready = true;
				readyTick = ROLL_DEFERMENT_TICK;
				readyCoolTick = 30;
			}
			if (!ready) {
				ready = !player.collidedVertically
						&& KeyRecorder.keyCrawlState.isPressed();
			}
		}
		if (start) {
			if (player.isUser()) {
				Vector3d lookVec = player.getLookVec();
				Vector3d vec = new Vector3d(lookVec.getX(), 0, lookVec.getZ()).normalize().scale(1.4);
				player.addVelocity(vec.getX(), 0, vec.getZ());
				player.velocityChanged = true;
				this.cameraPitch = player.rotationPitch;
				sendSynchronization(player);
			}
			Animation animation = Animation.get(player);
			if (animation != null) animation.setAnimator(new RollAnimator());
			start = false;
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
		if (rolling && player.isUser() && Minecraft.getInstance().gameSettings.thirdPersonView == 0 && !ParCoolConfig.CONFIG_CLIENT.disableCameraRolling.get()) {
			float factor = RollAnimator.calculateMovementFactor((getRollingTick() + event.renderTickTime) / (float) getRollMaxTick());
			player.rotationPitch = (factor > 0.5 ? factor - 1 : factor) * 360f + cameraPitch;
		}
	}

	@Override
	public boolean needSynchronization(ByteBuffer savedInstanceState) {
		return this.ready != BufferUtil.getBoolean(savedInstanceState)
				|| this.rolling != BufferUtil.getBoolean(savedInstanceState);
	}

	@Override
	public void sendSynchronization(PlayerEntity player) {
		SyncRollMessage.sync(player, this);
	}


	@Override
	public void synchronize(Object message) {
		if (message instanceof SyncRollMessage) {
			this.rolling = ((SyncRollMessage) message).isRolling();
			this.ready = ((SyncRollMessage) message).isRollReady();
			this.readyTick = ((SyncRollMessage) message).getReadyTick();
			return;
		}
		if (message instanceof StartRollMessage) {
			this.rolling = true;
			this.ready = false;
			this.start = true;
		}
	}

	@Override
	public void saveState(ByteBuffer buffer) {
		BufferUtil.wrap(buffer)
				.putBoolean(ready)
				.putBoolean(rolling);
	}

	public int getRollingTick() {
		return rollingTick;
	}

	public boolean isRolling() {
		return rolling;
	}

	public int getReadyTick() {
		return readyTick;
	}

	public boolean isReady() {
		return ready;
	}

	public int getRollMaxTick() {
		return 9;
	}
}
