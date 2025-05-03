package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.action.impl.*;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.network.StartBreakfallMessage;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.ServerPlayerWrapper;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerDamageHandler {
	@SubscribeEvent
	public static void onAttack(LivingAttackEvent event) {
		LivingEntity entity = event.getEntityLiving();
		PlayerWrapper player = PlayerWrapper.getOrDefault(entity);
		if (player == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		Dodge dodge = parkourability.get(Dodge.class);
		if (!dodge.isDoing()) return;
		if (!parkourability.getServerLimitation().get(ParCoolConfig.Server.Booleans.DodgeProvideInvulnerableFrame))
			return;
		if (event.getSource().isBypassArmor()) return;
		if (dodge.getDoingTick() <= 10) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onFall(LivingFallEvent event) {
		ServerPlayerWrapper serverPlayer = ServerPlayerWrapper.getFromEntityOrDefault(event);
		if (serverPlayer != null) {

			Parkourability parkourability = Parkourability.get(serverPlayer);
			if (parkourability == null) return;

			if (parkourability.get(BreakfallReady.class).isDoing()
					&& (parkourability.getActionInfo().can(Tap.class)
					|| parkourability.getActionInfo().can(Roll.class))
			) {
				boolean justTime = parkourability.get(BreakfallReady.class).getDoingTick() < 5;
				float distance = event.getDistance();
				if (distance > 2) {
					StartBreakfallMessage.send(serverPlayer, justTime);
				}
				if (distance < 6 || (justTime && distance < 8)) {
					event.setCanceled(true);
				} else {
					event.setDamageMultiplier(event.getDamageMultiplier() * (justTime ? 0.4f : 0.6f));
				}
			}
		} else {
			PlayerWrapper player = PlayerWrapper.getFromEntityOrDefault(event);
			if (player == null || !player.isLocalPlayer()) return;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			if (parkourability.getAdditionalProperties().getNotLandingTick() > 5 && event.getDistance() < 0.4f) {
				parkourability.get(ChargeJump.class).onLand(player, parkourability);
			}
		}
	}
}
