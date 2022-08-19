package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.ClimbUpAnimator;
import com.alrex.parcool.client.animation.impl.ClingToCliffAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.EntityUtil;
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
	private float armSwingAmountOld = 0;
	private float armSwingAmount = 0;

	public float getArmSwingAmount() {
		return armSwingAmount;
	}

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
		if (player.isLocalPlayer()) {
			double ySpeed = player.getDeltaMovement().y();
			cling = !stamina.isExhausted() &&
					ySpeed < 0.2 &&
					parkourability.getPermission().canClingToCliff() &&
					KeyBindings.getKeyGrabWall().isDown() &&
					WorldUtil.existsGrabbableWall(player);

			if (canClimbUp(player, parkourability, stamina)) {
				cling = false;
				clingTick = 0;
				EntityUtil.addVelocity(player, new Vector3d(0, 0.6, 0));
				stamina.consume(parkourability.getActionInfo().getStaminaConsumptionClimbUp(), player);
				Animation animation = Animation.get(player);
				if (animation != null) animation.setAnimator(new ClimbUpAnimator());
			}
			if (cling) {
				stamina.consume(parkourability.getActionInfo().getStaminaConsumptionClingToCliff(), player);
				if (KeyBindings.getKeyLeft().isDown() && KeyBindings.getKeyRight().isDown()) {
					player.setDeltaMovement(0, 0, 0);
				} else {
					Vector3d wallDirection = WorldUtil.getWall(player);
					if (wallDirection != null) {
						Vector3d vec = wallDirection.yRot((float) (Math.PI / 2)).normalize().scale(0.1);
						if (KeyBindings.getKeyLeft().isDown()) player.setDeltaMovement(vec);
						else if (KeyBindings.getKeyRight().isDown()) player.setDeltaMovement(vec.reverse());
						else player.setDeltaMovement(0, 0, 0);
					}
				}
			}

			if (cling) armSwingAmount += player.getDeltaMovement().lengthSqr();
		}
		armSwingAmountOld = armSwingAmount;
		if (cling) {
			Animation animation = Animation.get(player);
			if (animation != null) animation.setAnimator(new ClingToCliffAnimator());
		} else {
			armSwingAmount = 0;
		}
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
		if (cling) {
			Vector3d wall = WorldUtil.getWall(player);
			if (wall != null) {
				float yRot = (float) VectorUtil.toYawDegree(wall.normalize());
				player.setYBodyRot(yRot);
				Vector3d vec = VectorUtil.fromYawDegree(player.yHeadRot);
				Vector3d dividedVec =
						new Vector3d(
								vec.x() * wall.x() + vec.z() * wall.z(), 0,
								-vec.x() * wall.z() + vec.z() * wall.x()
						).normalize();
				if (dividedVec.x() < 0.34202/*cos(70)*/) {
					if (dividedVec.z() < 0) {
						player.yRot = (float) VectorUtil.toYawDegree(wall.yRot((float) (Math.PI * 0.38888888889/* PI*7/18 */)));
					} else {
						player.yRot = (float) VectorUtil.toYawDegree(wall.yRot((float) (-Math.PI * 0.38888888889)));
					}
				}
			}
		}
	}

	@Override
	public void restoreState(ByteBuffer buffer) {
		cling = BufferUtil.getBoolean(buffer);
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
