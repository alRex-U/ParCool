package com.alrex.parcool.common.processor;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IVault;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VaultLogic {
	@OnlyIn(Dist.CLIENT)
	private static double wallHeight = 0;
	@OnlyIn(Dist.CLIENT)
	private static Vector3d stepDirection = null;

	@SubscribeEvent
	public static void onTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.START || !event.player.world.isRemote) return;

		IVault vault;
		{
			LazyOptional<IVault> vaultOptional = event.player.getCapability(IVault.VaultProvider.VAULT_CAPABILITY);
			if (!vaultOptional.isPresent()) return;
			vault = vaultOptional.resolve().get();
		}
		vault.updateVaultingTime();

		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != event.player) return;
		if (!ParCool.isActive()) return;

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
