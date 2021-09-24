package com.alrex.parcool.common.processor;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.ActionPermissions;
import com.alrex.parcool.common.capability.IVault;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class VaultLogic {
	//only in Client
	private static double wallHeight = 0;
	//only in Client
	private static Vector3d stepDirection = null;

	@SubscribeEvent
	public static void onTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.START || event.side == LogicalSide.SERVER) return;

		PlayerEntity player = event.player;
		IVault vault = IVault.get(player);
		if (vault == null) return;

		vault.updateVaultingTime();


		if (!player.isUser()) return;
		if (!ParCool.isActive() || !ActionPermissions.isAllowedVault()) return;

		if (!vault.isVaulting() && vault.canVault(player)) {
			vault.setVaulting(true);
			stepDirection = WorldUtil.getStep(event.player);
			wallHeight = WorldUtil.getWallHeight(event.player);
		}
		if (vault.isVaulting()) {
			player.setMotion(stepDirection.getX() / 10, (wallHeight + 0.05) / vault.getVaultAnimateTime(), stepDirection.getZ() / 10);
		}
		if (vault.getVaultingTime() >= vault.getVaultAnimateTime()) {
			vault.setVaulting(false);
			stepDirection = stepDirection.normalize();
			player.setMotion(stepDirection.getX() * 0.45, 0.15, stepDirection.getZ() * 0.45d);
		}
	}
}
