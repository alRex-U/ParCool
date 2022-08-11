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
import net.minecraft.client.Minecraft;
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
	private boolean needPitchReset = false;
	private int damageCoolTime = 0;
	private boolean avoided = false;
	private boolean dodging = false;
	private boolean flipping = false;

	public boolean isAvoided() {
		return avoided;
	}

	public boolean isDodging() {
		return dodging;
	}

	public boolean isFlipping() {
		return flipping;
	}

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (coolTime > 0) coolTime--;
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
		return parkourability.getPermission().canDodge() && coolTime <= 0 && player.isOnGround() && !player.isShiftKeyDown() && !stamina.isExhausted() && (
				enabledDoubleTap && (
						KeyRecorder.keyBack.isDoubleTapped() ||
								KeyRecorder.keyLeft.isDoubleTapped() ||
								KeyRecorder.keyRight.isDoubleTapped() ||
								(ParCoolConfig.CONFIG_CLIENT.canFrontFlip.get() && KeyRecorder.keyForward.isDoubleTapped())
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
				if (dodging && flipping && (dodgeDirection == DodgeDirection.Front || dodgeDirection == DodgeDirection.Back)) {
					if (!ParCoolConfig.CONFIG_CLIENT.disableCameraDodge.get()) {
						needPitchReset = true;
					}
				}
				dodgingTick = 0;
				dodging = false;
				avoided = false;
				flipping = false;
			}
			if (!dodging && canDodge(player, parkourability, stamina)) {
				dodging = true;
				avoided = false;
				flipping = false;
				stamina.consume(parkourability.getActionInfo().getStaminaConsumptionDodge(), parkourability.getActionInfo());
				dodgeDirection = getDirectionFromInput();

				Vector3d lookVec = player.getLookAngle();
				lookVec = new Vector3d(lookVec.x(), 0, lookVec.z()).normalize();
				double jump = 0;
				Vector3d dodgeVec = Vector3d.ZERO;
				boolean enabledFlipping = !ParCoolConfig.CONFIG_CLIENT.disableFlipping.get();
				switch (dodgeDirection) {
					case Front:
						dodgeVec = lookVec;
						jump = enabledFlipping ? 0.5 : 0.3;
						flipping = enabledFlipping;
						break;
					case Back:
						dodgeVec = lookVec.reverse();
						jump = enabledFlipping ? 0.5 : 0.3;
						flipping = enabledFlipping;
						break;
					case Right:
						dodgeVec = lookVec.yRot((float) Math.PI / -2);
						jump = 0.3;
						break;
					case Left:
						dodgeVec = lookVec.yRot((float) Math.PI / 2);
						jump = 0.3;
						break;
				}
				coolTime = 10;
				dodgeVec = dodgeVec.scale(0.4);
				EntityUtil.addVelocity(player, new Vector3d(dodgeVec.x(), jump, dodgeVec.z()));
			}
		}
		if (flipping) {
			Animation animation = Animation.get(player);
			if (animation != null) animation.setAnimator(new DodgeAnimator());
		}
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
		if (!player.isLocalPlayer() ||
				!Minecraft.getInstance().options.getCameraType().isFirstPerson() ||
				ParCoolConfig.CONFIG_CLIENT.disableFlipping.get() ||
				ParCoolConfig.CONFIG_CLIENT.disableCameraDodge.get()
		) return;
		if (needPitchReset) {
			player.xRot = 0;
			needPitchReset = false;
		}
		if (!dodging) return;
		if (dodgeDirection == DodgeDirection.Front) {
			player.xRot = (getDodgingTick() + event.renderTickTime) * 30;
		} else if (dodgeDirection == DodgeDirection.Back) {
			player.xRot = (getDodgingTick() + event.renderTickTime) * -24;
		}
	}

	@Override
	public void restoreState(ByteBuffer buffer) {
		dodging = BufferUtil.getBoolean(buffer);
		avoided = BufferUtil.getBoolean(buffer);
		flipping = BufferUtil.getBoolean(buffer);
		dodgeDirection = DodgeDirection.getFromCode(buffer.getInt());
	}

	@Override
	public void saveState(ByteBuffer buffer) {
		BufferUtil.wrap(buffer)
				.putBoolean(dodging)
				.putBoolean(avoided)
				.putBoolean(flipping);
		buffer.putInt(dodgeDirection == null ? -1 : dodgeDirection.getCode());
	}

	public int getStaminaConsumptionOfAvoiding(float damage) {
		return Math.round(150 + damage * 30);
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
