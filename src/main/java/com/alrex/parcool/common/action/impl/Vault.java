package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.SpeedVaultAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.common.network.StartVaultMessage;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class Vault extends Action {
	private boolean vauting = false;
	private int vaultingTick = 0;

	//only in client
	private double stepHeight = 0;
	private Vector3d stepDirection = null;

	//for not Local Player
	private boolean start = false;

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (vauting) {
			vaultingTick++;
		} else {
			vaultingTick = 0;
		}
	}

	@OnlyIn(Dist.CLIENT)
	private boolean canVault(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		Vector3d lookVec = player.getLookAngle();
		lookVec = new Vector3d(lookVec.x(), 0, lookVec.z()).normalize();
		Vector3d wall = WorldUtil.getWall(player);
		if (wall == null) return false;
		return !this.vauting &&
				parkourability.getPermission().canVault() &&
				!(ParCoolConfig.CONFIG_CLIENT.vaultNeedKeyPressed.get() && !KeyBindings.getKeyVault().isDown()) &&
				parkourability.getFastRun().canActWithRunning(player) &&
				player.isOnGround() &&
				(wall.dot(lookVec) / wall.length() / lookVec.length()) > 0.707106 /*check facing wall*/ &&
				WorldUtil.getVaultableStep(player) != null &&
				WorldUtil.getWallHeight(player) > 0.8;
	}

	private int getVaultAnimateTime() {
		return 2;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (start) {
			start = false;
			Animation animation = Animation.get(player);
			if (animation != null)
				animation.setAnimator(new SpeedVaultAnimator(SpeedVaultAnimator.Type.Right));
		}
		if (player.isLocalPlayer()) {
			if (!this.isVaulting() && this.canVault(player, parkourability, stamina)) {
				vauting = true;
				vaultingTick = 0;
				stepDirection = WorldUtil.getVaultableStep(player);
				stepHeight = WorldUtil.getWallHeight(player);

				Vector3d lookVec = player.getLookAngle();
				Vector3d vec = new Vector3d(lookVec.x(), 0, lookVec.z()).normalize();
				Vector3d s = stepDirection;

				//doing "vec/stepDirection" as complex number(x + z i) to calculate difference of player's direction to steps
				Vector3d dividedVec =
						new Vector3d(
								vec.x() * s.x() + vec.z() * s.z(), 0,
								-vec.x() * s.z() + vec.z() * s.x()
						);
				stamina.consume(parkourability.getActionInfo().getStaminaConsumptionVault(), player);
				Animation animation = Animation.get(player);
				SpeedVaultAnimator.Type type = dividedVec.z() > 0 ? SpeedVaultAnimator.Type.Right : SpeedVaultAnimator.Type.Left;
				if (animation != null)
					animation.setAnimator(new SpeedVaultAnimator(type));
				StartVaultMessage.send(player);
			}

			if (vauting) {
				player.setDeltaMovement(
						stepDirection.x() / 10,
						(stepHeight + 0.05) / this.getVaultAnimateTime(),
						stepDirection.z() / 10
				);
			}

			if (vaultingTick >= this.getVaultAnimateTime()) {
				vauting = false;
				stepDirection = stepDirection.normalize();
				player.setDeltaMovement(
						stepDirection.x() * 0.45,
						0.15,
						stepDirection.z() * 0.45
				);
			}
		}
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
	}

	@Override
	public void restoreState(ByteBuffer buffer) {

	}

	public void receiveStartVault(StartVaultMessage message) {
		start = true;
	}

	@Override
	public void saveState(ByteBuffer buffer) {

	}

	public boolean isVaulting() {
		return vauting;
	}

	public int getVaultingTick() {
		return vaultingTick;
	}
}
