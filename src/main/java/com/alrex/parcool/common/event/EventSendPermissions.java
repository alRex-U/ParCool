package com.alrex.parcool.common.event;

import com.alrex.parcool.common.network.SyncLimitationByServerMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventSendPermissions {
	@SubscribeEvent
	public static void JoinEvent(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) entity;
			SyncLimitationByServerMessage.send(player);
			SyncLimitationByServerMessage.sendIndividualLimitation(player);
		}
	}
}
