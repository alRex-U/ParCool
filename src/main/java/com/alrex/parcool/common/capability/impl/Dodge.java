package com.alrex.parcool.common.capability.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.capability.IDodge;
import com.alrex.parcool.common.capability.IGrabCliff;
import com.alrex.parcool.common.capability.IRoll;
import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class Dodge implements IDodge {
	private int dodgingTime = 0;
	private boolean dodging = false;
	private int coolTime = 0;
	private DodgeDirection direction = null;

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canDodge(PlayerEntity player) {
		IStamina stamina = IStamina.get(player);
		if (stamina == null) return false;
		return coolTime <= 0 && player.collidedVertically && !player.isSneaking() && !stamina.isExhausted() && ParCoolConfig.CONFIG_CLIENT.canDodge.get() && (
				KeyRecorder.keyBack.isDoubleTapped() ||
						KeyRecorder.keyLeft.isDoubleTapped() ||
						KeyRecorder.keyRight.isDoubleTapped() ||
						(ParCoolConfig.CONFIG_CLIENT.canFrontFlip.get() &&
								(KeyBindings.getKeyForward().conflicts(KeyBindings.getKeyFrontFlip()) ?
										KeyRecorder.keyFrontFlip.isDoubleTapped() :
										KeyRecorder.keyFrontFlip.isPressed()))
		);
	}

	@Override
	public void setDirection(DodgeDirection direction) {
		this.direction = direction;
	}

	@OnlyIn(Dist.CLIENT)
	@Nullable
	@Override
	public Vector3d getAndSetDodgeDirection(PlayerEntity player) {
		Vector3d lookVec = player.getLookVec();
		lookVec = new Vector3d(lookVec.getX(), 0, lookVec.getZ()).normalize();

		if (KeyBindings.getKeyBack().isKeyDown()) {
			direction = DodgeDirection.Back;
			return lookVec.inverse();
		}
		if (KeyBindings.getKeyFrontFlip().isKeyDown()) {
			direction = DodgeDirection.Front;
			return lookVec;
		}
		if (KeyBindings.getKeyLeft().isKeyDown() && KeyBindings.getKeyRight().isKeyDown()) return null;
		Vector3d vecToRight = lookVec.rotateYaw((float) Math.PI / -2);
		if (KeyBindings.getKeyLeft().isKeyDown()) {
			direction = DodgeDirection.Left;
			return vecToRight.inverse();
		} else {
			direction = DodgeDirection.Right;
			return vecToRight;
		}
	}

	@Override
	public boolean isDodging() {
		return dodging;
	}

	@Override
	public void setDodging(boolean dodging) {
		this.dodging = dodging;
		if (dodging) coolTime = 10;
		else direction = null;
	}

	@Override
	public int getDodgingTime() {
		return dodgingTime;
	}

	@Nullable
	@Override
	public DodgeDirection getDirection() {
		return direction;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinueDodge(PlayerEntity player) {
		IGrabCliff grabCliff = IGrabCliff.get(player);
		IRoll roll = IRoll.get(player);
		if (roll == null || grabCliff == null) return false;
		return dodging &&
				!roll.isRolling() &&
				!grabCliff.isGrabbing() &&
				!player.collidedVertically &&
				!player.isInWaterOrBubbleColumn() &&
				!player.isElytraFlying() &&
				!player.abilities.isFlying &&
				ParCoolConfig.CONFIG_CLIENT.canDodge.get();
	}

	@Override
	public void updateDodgingTime() {
		if (coolTime > 0) coolTime--;
		if (dodging) {
			dodgingTime++;
		} else {
			dodgingTime = 0;
		}
	}

	@Override
	public int getStaminaConsumption() {
		return 100;
	}
}
