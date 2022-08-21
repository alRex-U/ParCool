package com.alrex.parcool.common.event;

import com.alrex.parcool.common.network.ActionPermissionsMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventPlayerJoin {
	@SubscribeEvent
	public static void JoinEvent(EntityJoinLevelEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) entity;
			ActionPermissionsMessage.send(player);
		}
	}
}
