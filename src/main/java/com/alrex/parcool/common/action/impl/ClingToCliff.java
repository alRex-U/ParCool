package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.ClingToCliffAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class ClingToCliff extends Action {
	private float armSwingAmount = 0;

	public float getArmSwingAmount() {
		return armSwingAmount;
	}

	@Override
	public void onWorkingTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		player.fallDistance = 0;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		return (!stamina.isExhausted()
				&& player.getDeltaMovement().y() < 0.2
				&& parkourability.getActionInfo().can(ClingToCliff.class)
				&& KeyBindings.getKeyGrabWall().isDown()
				&& WorldUtil.existsGrabbableWall(player)
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		return (!stamina.isExhausted()
				&& parkourability.getActionInfo().can(ClingToCliff.class)
				&& KeyBindings.getKeyGrabWall().isDown()
				&& !parkourability.get(ClimbUp.class).isDoing()
				&& WorldUtil.existsGrabbableWall(player)
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		armSwingAmount = 0;
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new ClingToCliffAnimator());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onWorkingTickInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
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
		armSwingAmount += player.getDeltaMovement().lengthSqr();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onRenderTick(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
		if (isDoing()) {
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
	public void restoreSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnWorking;
	}
}
