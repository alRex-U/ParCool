package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.RollAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

;

public class Roll extends Action {
	private int creativeCoolTime = 0;
	private boolean startRequired = false;

	public enum Direction {Front, Back}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(Player player, Parkourability parkourability, IStamina stamina) {
		if (player.isLocalPlayer()) {
			if (KeyBindings.getKeyBreakfall().isDown()
					&& KeyBindings.getKeyForward().isDown()
					&& !parkourability.get(Dodge.class).isDoing()
					&& ParCoolConfig.CONFIG_CLIENT.enableRollWhenCreative.get()
					&& player.isCreative()
					&& parkourability.getAdditionalProperties().getLandingTick() <= 1
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
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}

	@Override
	public boolean canStart(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		BufferUtil.wrap(startInfo).putBoolean(KeyBindings.getKeyBack().isDown());
		return startRequired;
	}

	@Override
	public boolean canContinue(Player player, Parkourability parkourability, IStamina stamina) {
		return getDoingTick() < getRollMaxTick();
	}

	@Override
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		startRequired = false;
		Direction direction = BufferUtil.getBoolean(startData) ? Direction.Back : Direction.Front;
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new RollAnimator(direction));
	}

	@Override
	public void onStartInLocalClient(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		startRequired = false;
		Direction direction = BufferUtil.getBoolean(startData) ? Direction.Back : Direction.Front;
		double modifier = Math.sqrt(player.getBbWidth());
		Vec3 vec = VectorUtil.fromYawDegree(player.yBodyRot).scale(modifier);
		if (direction == Direction.Back) {
			vec = vec.reverse();
		}
		player.setDeltaMovement(vec.x, 0, vec.z);
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new RollAnimator(direction));
	}

	public void startRoll(Player player) {
		startRequired = true;
	}

	public int getRollMaxTick() {
		return 9;
	}
}
