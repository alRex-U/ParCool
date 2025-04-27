package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.action.impl.*;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.network.StartBreakfallMessage;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerDamageHandler {
	@SubscribeEvent
	public static void onAttack(LivingAttackEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			Dodge dodge = parkourability.get(Dodge.class);
			if (dodge.isDoing()) {
				if (!parkourability.getServerLimitation().get(ParCoolConfig.Server.Booleans.DodgeProvideInvulnerableFrame))
					return;
				if (event.getSource().isBypassArmor()) return;
				if (dodge.getDoingTick() <= 10) {
					event.setCanceled(true);
				}
			}
		}
	}
	@SubscribeEvent
	public static void onFall(LivingFallEvent event) {
		if (event.getEntity() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();

			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;

			if (parkourability.get(BreakfallReady.class).isDoing()
					&& (parkourability.getActionInfo().can(Tap.class)
					|| parkourability.getActionInfo().can(Roll.class))
			) {
				boolean justTime = parkourability.get(BreakfallReady.class).getDoingTick() < 5;
				float distance = event.getDistance();
				if (distance > 2) {
					StartBreakfallMessage.send(player, justTime);
				}
				if (distance < 6 || (justTime && distance < 8)) {
					event.setCanceled(true);
				} else {
					event.setDamageMultiplier(event.getDamageMultiplier() * (justTime ? 0.4f : 0.6f));
				}
			} else if (parkourability.get(HideInBlock.class).isStandbyInAir(parkourability)) {
				Tuple<BlockPos, BlockPos> area= WorldUtil.getHideAbleSpace(player,new BlockPos(player.blockPosition().below()));
				if (area != null){
					boolean stand = player.getBbHeight() < (Math.abs(area.getB().getY() - area.getA().getY()) + 1);
					if (!stand){
						if (event.getDistance() < 10){
							event.setCanceled(true);
						}else {
							event.setDamageMultiplier(event.getDamageMultiplier() * 0.4f);
						}
					}
				}
			}
		} else if (event.getEntity() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) event.getEntity();
			if (!player.isLocalPlayer()) {
				return;
			}
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			if (parkourability.getAdditionalProperties().getNotLandingTick() > 5 && event.getDistance() < 0.4f) {
				parkourability.get(ChargeJump.class).onLand(player, parkourability);
			}
		}
	}
}
