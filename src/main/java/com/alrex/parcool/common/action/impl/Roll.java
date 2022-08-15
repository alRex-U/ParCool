package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.RollAnimator;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class Roll extends Action {
	private boolean start = false;
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
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (start) {
			start = false;
			rolling = true;
			if (player.isLocalPlayer()) {
				Vector3d vec = VectorUtil.fromYawDegree(player.yBodyRot);
				player.setDeltaMovement(vec.x(), 0, vec.z());
			}
			Animation animation = Animation.get(player);
			if (animation != null) animation.setAnimator(new RollAnimator());
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
	}


	public void startRoll(PlayerEntity player) {
		this.start = true;
	}

	@Override
	public void saveState(ByteBuffer buffer) {
		BufferUtil.wrap(buffer).putBoolean(start).putBoolean(rolling);
	}

	@Override
	public void restoreState(ByteBuffer buffer) {
		start = BufferUtil.getBoolean(buffer);
		rolling = BufferUtil.getBoolean(buffer);
	}

	public int getRollingTick() {
		return rollingTick;
	}

	public boolean isRolling() {
		return rolling;
	}

	public int getRollMaxTick() {
		return 9;
	}
}
