package com.alrex.parcool.common.capability.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.capability.IFastRunning;
import com.alrex.parcool.common.capability.IVault;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

public class Vault implements IVault {
	private int vaultingTime = 0;
	private boolean vaulting = false;

	@Override
	public int getVaultAnimateTime() {
		return 2;
	}

	@Override
	public boolean canVault(ClientPlayerEntity player) {
		IFastRunning fastRunning = IFastRunning.get(player);
		if (fastRunning == null) return false;

		Vector3d lookVec = player.getLookVec();
		lookVec = new Vector3d(lookVec.getX(), 0, lookVec.getZ()).normalize();
		Vector3d wall = WorldUtil.getWall(player);
		if (wall == null) return false;
		return !vaulting && ParCoolConfig.CONFIG_CLIENT.canVault.get() && fastRunning.isFastRunning() && fastRunning.getRunningTime() > 20 && player.collidedVertically && (wall.dotProduct(lookVec) / wall.length() / lookVec.length()) > 0.707106 /*check facing wall*/ && WorldUtil.getStep(player) != null && WorldUtil.getWallHeight(player) > 0.8;
	}

	@Override
	public int getVaultingTime() {
		return vaultingTime;
	}

	@Override
	public void setVaulting(boolean vaulting) {
		this.vaulting = vaulting;
		if (!vaulting) vaultingTime = 0;
	}

	@Override
	public boolean isVaulting() {
		return vaulting;
	}

	@Override
	public void updateVaultingTime() {
		if (vaulting) vaultingTime++;
		if (vaultingTime > getVaultAnimateTime()) {
			vaultingTime = 0;
			vaulting = false;
		}
	}
}
