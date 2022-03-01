package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.SpeedVaultAnimator;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.common.network.StartVaultMessage;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.client.Minecraft;
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
		Vector3d lookVec = player.getLookVec();
		lookVec = new Vector3d(lookVec.getX(), 0, lookVec.getZ()).normalize();
		Vector3d wall = WorldUtil.getWall(player);
		if (wall == null) return false;
		return !this.vauting &&
				parkourability.getPermission().canVault() &&
				parkourability.getFastRun().canActWithRunning(player) &&
				player.collidedVertically &&
				(wall.dotProduct(lookVec) / wall.length() / lookVec.length()) > 0.707106 /*check facing wall*/ &&
				WorldUtil.getStep(player) != null &&
				WorldUtil.getWallHeight(player) > 0.8;
	}

	private int getVaultAnimateTime() {
		return 2;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (player.isUser()) {
			if (!this.isVaulting() && this.canVault(player, parkourability, stamina)) {
				vauting = true;
				vaultingTick = 0;
				stepDirection = WorldUtil.getStep(player);
				stepHeight = WorldUtil.getWallHeight(player);

				Vector3d lookVec = player.getLookVec();
				Vector3d vec = new Vector3d(lookVec.getX(), 0, lookVec.getZ()).normalize();
				Vector3d s = stepDirection;

				//doing "vec/stepDirection" as complex number(x + z i) to calculate difference of player's direction to steps
				Vector3d dividedVec =
						new Vector3d(
								vec.getX() * s.getX() + vec.getZ() * s.getZ(), 0,
								-vec.getX() * s.getZ() + vec.getZ() * s.getX()
						);
				Animation animation = Animation.get(player);
				if (animation != null)
					animation.setAnimator(new SpeedVaultAnimator(dividedVec.getZ() > 0 ? SpeedVaultAnimator.Type.Right : SpeedVaultAnimator.Type.Left));
			}

			if (vauting) {
				player.setMotion(
						stepDirection.getX() / 10,
						(stepHeight + 0.05) / this.getVaultAnimateTime(),
						stepDirection.getZ() / 10
				);
			}

			if (vaultingTick >= this.getVaultAnimateTime()) {
				vauting = false;
				stepDirection = stepDirection.normalize();
				player.setMotion(
						stepDirection.getX() * 0.45,
						0.15,
						stepDirection.getZ() * 0.45
				);
			}
		}
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
	}

	@Override
	public boolean needSynchronization(ByteBuffer savedInstanceState) {
		return false;
	}

	@Override
	public void sendSynchronization(PlayerEntity player) {

	}


	@Override
	public void synchronize(Object message) {
		if (message instanceof StartVaultMessage) {
			PlayerEntity player = Minecraft.getInstance().player;
			if (player == null) return;
			PlayerEntity startPlayer = player.getEntityWorld().getPlayerByUuid(((StartVaultMessage) message).getPlayerID());
			Animation animation = Animation.get(player);
			if (animation != null)
				animation.setAnimator(new SpeedVaultAnimator(SpeedVaultAnimator.Type.Right));
		}

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
