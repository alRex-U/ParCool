package com.alrex.parcool.common.event;

import com.alrex.parcool.common.network.ActionPermissionsMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventSendPermissions {
	@SubscribeEvent
	public static void JoinEvent(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) entity;
			ActionPermissionsMessage.send(player);
		}
	}

	@SubscribeEvent
	public static void AdvancementEvent(AdvancementEvent event) {
		PlayerEntity entity = event.getPlayer();
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) entity;
			ActionPermissionsMessage.send(player);
		}
	}
}
