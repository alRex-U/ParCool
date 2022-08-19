package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.DodgeAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.EntityUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;


public class Dodge extends Action {
	public enum DodgeDirection {
		Front, Back, Left, Right;

		int getCode() {
			switch (this) {
				case Front:
					return 0;
				case Back:
					return 1;
				case Left:
					return 2;
				case Right:
					return 3;
			}
			return -1;
		}

		@Nullable
		public static DodgeDirection getFromCode(int code) {
			switch (code) {
				case 0:
					return Front;
				case 1:
					return Back;
				case 2:
					return Left;
				case 3:
					return Right;
			}
			return null;
		}
	}

	private DodgeDirection dodgeDirection = null;
	private int coolTime = 0;
	private int dodgingTick = 0;
	private int damageCoolTime = 0;
	private boolean dodging = false;
	private int successivelyCount = 0;
	private int successivelyCoolTick = 0;

	public boolean isDodging() {
		return dodging;
	}

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (coolTime > 0) coolTime--;
		if (successivelyCoolTick > 0) {
			successivelyCoolTick--;
		} else {
			successivelyCount = 0;
		}
		if (damageCoolTime > 0) damageCoolTime--;

		if (dodging) {
			dodgingTick++;
		} else {
			dodgingTick = 0;
		}
	}

	@OnlyIn(Dist.CLIENT)
	private boolean canDodge(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		boolean enabledDoubleTap = !ParCoolConfig.CONFIG_CLIENT.disableDoubleTappingForDodge.get();
		return parkourability.getPermission().canDodge() && successivelyCount < 2 && coolTime <= 0 && player.isOnGround() && !player.isShiftKeyDown() && !stamina.isExhausted() && (
				enabledDoubleTap && !parkourability.getRoll().isRolling() && !parkourability.getTap().isTapping() && (
						KeyRecorder.keyBack.isDoubleTapped() ||
								KeyRecorder.keyLeft.isDoubleTapped() ||
								KeyRecorder.keyRight.isDoubleTapped() ||
								(ParCoolConfig.CONFIG_CLIENT.canFrontDodge.get() && KeyRecorder.keyForward.isDoubleTapped())
				) || (
						KeyBindings.getKeyDodge().isDown() && (
								KeyBindings.getKeyForward().isDown() ||
										KeyBindings.getKeyBack().isDown() ||
										KeyBindings.getKeyLeft().isDown() ||
										KeyBindings.getKeyRight().isDown()
						)
				)
		);
	}

	@OnlyIn(Dist.CLIENT)
	private boolean canContinue(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		return dodging &&
				!parkourability.getRoll().isRolling() &&
				!parkourability.getClingToCliff().isCling() &&
				!player.isOnGround() &&
				!player.isInWaterOrBubble() &&
				!player.isFallFlying() &&
				!player.abilities.flying &&
				parkourability.getPermission().canClingToCliff();
	}

	@OnlyIn(Dist.CLIENT)
	private DodgeDirection getDirectionFromInput() {
		if (KeyBindings.getKeyBack().isDown()) {
			return DodgeDirection.Back;
		}
		if (KeyBindings.getKeyForward().isDown()) {
			return DodgeDirection.Front;
		}
		if (KeyBindings.getKeyLeft().isDown()) {
			return DodgeDirection.Left;
		} else {
			return DodgeDirection.Right;
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer()) {
			if (canContinue(player, parkourability, stamina)) {
				dodging = true;
			} else {
				dodgingTick = 0;
				dodging = false;
			}
			if (!dodging && canDodge(player, parkourability, stamina)) {
				dodging = true;
				stamina.consume(parkourability.getActionInfo().getStaminaConsumptionDodge(), player);
				dodgeDirection = getDirectionFromInput();

				Vector3d lookVec = player.getLookAngle();
				lookVec = new Vector3d(lookVec.x(), 0, lookVec.z()).normalize();
				double jump = 0;
				Vector3d dodgeVec = Vector3d.ZERO;
				switch (dodgeDirection) {
					case Front:
						dodgeVec = lookVec;
						break;
					case Back:
						dodgeVec = lookVec.reverse();
						break;
					case Right:
						dodgeVec = lookVec.yRot((float) Math.PI / -2);
						break;
					case Left:
						dodgeVec = lookVec.yRot((float) Math.PI / 2);
						break;
				}
				jump = 0.3;
				coolTime = 10;
				if (successivelyCoolTick != 0) {
					successivelyCount++;
				}
				successivelyCoolTick = 30;
				dodgeVec = dodgeVec.scale(0.4);
				EntityUtil.addVelocity(player, new Vector3d(dodgeVec.x(), jump, dodgeVec.z()));
			}
		}
		if (dodging && dodgingTick <= 1) {
			Animation animation = Animation.get(player);
			if (animation != null) animation.setAnimator(new DodgeAnimator());
		}
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
	}

	@Override
	public void restoreState(ByteBuffer buffer) {
		dodging = BufferUtil.getBoolean(buffer);
		dodgeDirection = DodgeDirection.getFromCode(buffer.getInt());
	}

	@Override
	public void saveState(ByteBuffer buffer) {
		BufferUtil.wrap(buffer)
				.putBoolean(dodging);
		buffer.putInt(dodgeDirection == null ? -1 : dodgeDirection.getCode());
	}

	public DodgeDirection getDodgeDirection() {
		return dodgeDirection;
	}

	public int getCoolTime() {
		return coolTime;
	}

	public int getDamageCoolTime() {
		return damageCoolTime;
	}

	public int getDodgingTick() {
		return dodgingTick;
	}
}
