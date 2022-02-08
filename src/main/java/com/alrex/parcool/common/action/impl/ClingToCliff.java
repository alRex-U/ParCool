package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.ClingToCliffAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.common.network.SyncClingToCliffMessage;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class ClingToCliff extends Action {
	private boolean cling = false;
	private int clingTick = 0;
	private int notClingTick = 0;

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (cling) {
			clingTick++;
			notClingTick = 0;
		} else {
			clingTick = 0;
			notClingTick++;
		}

	}

	@OnlyIn(Dist.CLIENT)
	public boolean canClimbUp(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		return cling && parkourability.getPermission().canClingToCliff() && clingTick > 2 && KeyRecorder.keyJumpState.isPressed();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (player.isUser()) {
			double ySpeed = player.getMotion().getY();
			cling = !stamina.isExhausted() &&
					ySpeed < 0.2 &&
					parkourability.getPermission().canClingToCliff() &&
					KeyBindings.getKeyGrabWall().isKeyDown() &&
					player.getHeldItemMainhand().isEmpty() &&
					player.getHeldItemOffhand().isEmpty() &&
					WorldUtil.existsGrabbableWall(player);

			if (canClimbUp(player, parkourability, stamina)) {
				cling = false;
				clingTick = 0;
				player.addVelocity(0, 0.6, 0);
				player.velocityChanged = true;
				stamina.consume(parkourability.getActionInfo().getStaminaConsumptionClimbUp(), parkourability.getActionInfo());
			}
			if (cling) {
				player.setVelocity(0, 0, 0);
				player.velocityChanged = true;
			}
		}
		if (cling) {
			Animation animation = Animation.get(player);
			if (animation != null) animation.setAnimator(new ClingToCliffAnimator());
		}
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
		if (cling) {
			Vector3d wall = WorldUtil.getWall(player);
			if (wall != null) {
				player.rotationYaw = (float) VectorUtil.toYawDegree(wall.normalize());
			}
		}
	}

	@Override
	public boolean needSynchronization(ByteBuffer savedInstanceState) {
		return cling != BufferUtil.getBoolean(savedInstanceState);
	}

	@Override
	public void sendSynchronization(PlayerEntity player) {
		SyncClingToCliffMessage.sync(player, this);
	}


	@Override
	public void synchronize(Object message) {
		if (message instanceof SyncClingToCliffMessage) {
			this.cling = ((SyncClingToCliffMessage) message).isCling();
		}
	}

	@Override
	public void saveState(ByteBuffer buffer) {
		BufferUtil.wrap(buffer)
				.putBoolean(cling);
	}

	public boolean isCling() {
		return cling;
	}

	public int getClingTick() {
		return clingTick;
	}

	public int getNotClingTick() {
		return notClingTick;
	}
}
