package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.RollAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.alrex.parcool.common.network.StartRollMessage;
import com.alrex.parcool.common.network.SyncRollMessage;
import com.alrex.parcool.utilities.BufferUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class Roll extends Action {
	public enum Type {
		Front, Back, Tap;

		public byte getAsByte() {
			return switch (this) {
				case Tap -> 0;
				case Back -> 1;
				case Front -> 2;
			};
		}

		public static Type getFromByte(byte value) {
			return switch (value) {
				case 0 -> Tap;
				case 1 -> Back;
				case 2 -> Front;
				default -> null;
			};
		}
	}

	public static final int ROLL_DEFERMENT_TICK = 9;

	private float cameraPitch = 0;
	private boolean ready = false;
	private int readyTick = 0;
	private boolean start = false;
	private int readyCoolTick = 0;
	private boolean rolling = false;
	private int rollingTick = 0;

	private Type type = null;
	private Type rollingType = null;

	@Override
	public void onTick(Player player, Parkourability parkourability, Stamina stamina) {
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
	public void onClientTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer()) {
			if (
					!ready
							&& parkourability.getPermission().canRoll()
							&& KeyRecorder.keyRollState.isPressed()
							&& !player.isOnGround()
							&& readyCoolTick <= 0
			) {
				ready = true;
				readyTick = ROLL_DEFERMENT_TICK;
				readyCoolTick = 30;
				if (KeyBindings.getKeyForward().isDown()) type = Type.Front;
				else if (KeyBindings.getKeyBack().isDown()) type = Type.Back;
				else type = Type.Front; //Temporal TODO:implement Tap
			}
		}
		if (start) {
			if (player.isLocalPlayer()) {
				if (rollingType == Type.Back || rollingType == Type.Front) {
					Vec3 lookVec = player.getLookAngle();
					Vec3 vec = new Vec3(lookVec.x(), 0, lookVec.z()).normalize().scale(1.4);
					if (rollingType == Type.Back) vec = vec.reverse();
					player.setDeltaMovement(vec.x(), 0, vec.z());
					this.cameraPitch = 20;
					sendSynchronization(player);
				}
			}
			Animation animation = Animation.get(player);
			if (animation != null) animation.setAnimator(new RollAnimator(type));
			start = false;
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onRender(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {
		if (rolling &&
				player.isLocalPlayer() &&
				Minecraft.getInstance().options.getCameraType().isFirstPerson() &&
				!ParCoolConfig.CONFIG_CLIENT.disableCameraRolling.get() &&
				(rollingType == Type.Back || rollingType == Type.Front)
		) {
			float factor = RollAnimator.calculateMovementFactor((getRollingTick() + event.renderTickTime) / (float) getRollMaxTick());
			player.setXRot((factor > 0.5 ? factor - 1 : factor) * (rollingType == Type.Front ? 360f : -360f) + cameraPitch);
		}
	}

	@Override
	public boolean needSynchronization(ByteBuffer savedInstanceState) {
		return this.ready != BufferUtil.getBoolean(savedInstanceState)
				|| this.rolling != BufferUtil.getBoolean(savedInstanceState)
				|| this.type != Type.getFromByte((byte) savedInstanceState.getInt());
	}

	@Override
	public void sendSynchronization(Player player) {
		SyncRollMessage.sync(player, this);
	}


	@Override
	public void synchronize(Object message) {
		if (message instanceof SyncRollMessage) {
			this.rolling = ((SyncRollMessage) message).isRolling();
			this.ready = ((SyncRollMessage) message).isRollReady();
			this.readyTick = ((SyncRollMessage) message).getReadyTick();
			this.type = ((SyncRollMessage) message).getType();
			return;
		}
		if (message instanceof StartRollMessage) {
			this.rolling = true;
			this.ready = false;
			this.start = true;
			this.rollingType = this.type;
		}
	}

	@Override
	public void saveState(ByteBuffer buffer) {
		BufferUtil.wrap(buffer)
				.putBoolean(ready)
				.putBoolean(rolling)
				.unwrap()
				.putInt(type != null ? type.getAsByte() : -1);
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

	public Type getType() {
		return type;
	}
}
