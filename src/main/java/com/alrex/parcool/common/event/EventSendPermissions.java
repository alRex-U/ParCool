package com.alrex.parcool.common.event;

import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.network.SyncLimitationMessage;
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
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			parkourability.getActionInfo().getServerLimitation().readFromServerConfig();
			parkourability.getActionInfo().getServerLimitation().setReceived();
			parkourability.getActionInfo().getIndividualLimitation().setReceived();
			SyncLimitationMessage.sendServerLimitation(player);
			SyncLimitationMessage.sendIndividualLimitation(player);
		}
	}
}
