package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.RollAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class Roll extends Action {
	private int creativeCoolTime = 0;
	private boolean startRequired = false;
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer()) {
			if (KeyBindings.getKeyBreakfall().isDown()
					&& KeyBindings.getKeyForward().isDown()
					&& !parkourability.get(Dodge.class).isDoing()
					&& ParCoolConfig.CONFIG_CLIENT.enableRollWhenCreative.get()
					&& player.isCreative()
					&& parkourability.get(AdditionalProperties.class).getLandingTick() <= 1
					&& player.isOnGround()
					&& !isDoing()
					&& creativeCoolTime == 0
			) {
				startRequired = true;
				creativeCoolTime = 20;
			}
			if (creativeCoolTime > 0) creativeCoolTime--;
		}
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startInfo) {
		return startRequired;
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		return getDoingTick() < getRollMaxTick();
	}

	@Override
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		startRequired = false;
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new RollAnimator());
	}

	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startData) {
		startRequired = false;
		double modifier = Math.sqrt(player.getBbWidth());
		Vector3d vec = VectorUtil.fromYawDegree(player.yBodyRot).scale(modifier);
		player.setDeltaMovement(vec.x(), 0, vec.z());
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new RollAnimator());
	}

	public void startRoll(PlayerEntity player) {
		startRequired = true;
	}

	public int getRollMaxTick() {
		return 9;
	}
}
