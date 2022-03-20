package com.alrex.parcool.common.event;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.network.ActionPermissionsMessage;
import com.alrex.parcool.common.network.DisableInfiniteStaminaMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventSendPermission {
	@SubscribeEvent
	public static void JoinEvent(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) entity;
			ActionPermissionsMessage.send(player);
			DisableInfiniteStaminaMessage.send(player, ParCoolConfig.CONFIG_SERVER.allowInfiniteStamina.get());
		}
	}

	@SubscribeEvent
	public static void AdvancementEvent(AdvancementEvent event) {
		Player entity = event.getPlayer();
		if (entity instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) entity;
			ActionPermissionsMessage.send(player);
		}
	}
}
