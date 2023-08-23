package com.alrex.parcool.common.event;

import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.network.SyncClientInformationMessage;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventSendPermissions {
	@SubscribeEvent
	public static void JoinEvent(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			if (!player.isLocalPlayer()) return;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			SyncClientInformationMessage.sync((ClientPlayerEntity) player, true);
		}
	}
}
