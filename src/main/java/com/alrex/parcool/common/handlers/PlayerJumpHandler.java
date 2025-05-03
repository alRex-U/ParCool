package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.compatibility.PlayerWrapper;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerJumpHandler {
	@SubscribeEvent
	public static void onJump(LivingEvent.LivingJumpEvent event) {
		PlayerWrapper player = PlayerWrapper.getFromEntityOrDefault(event);
		if (player == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		parkourability.getAdditionalProperties().onJump();
		if (!player.isLocalPlayer()) return;
		IStamina stamina = IStamina.get(player);
		if (stamina == null) return;
		parkourability.get(Dive.class).onJump(player, parkourability, stamina);
		parkourability.get(Flipping.class).onJump(player, parkourability, stamina);
        parkourability.get(ChargeJump.class).onJump(player, parkourability, stamina);
	}
}
