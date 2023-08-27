package com.alrex.parcool.common.event;

import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.network.SyncClientInformationMessage;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventSendPermissions {
	@SubscribeEvent
	public static void JoinEvent(EntityJoinLevelEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player player) {
			if (!player.isLocalPlayer()) return;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			SyncClientInformationMessage.sync((LocalPlayer) player, true);
		}
	}
}
