package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.action.impl.Flipping;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class PlayerJumpHandler {
	@SubscribeEvent
	public static void onJump(LivingEvent.LivingJumpEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		parkourability.getAdditionalProperties().onJump();
		if (!player.isLocalPlayer()) return;
		parkourability.get(Dive.class).onJump(player, parkourability);
		parkourability.get(Flipping.class).onJump(player, parkourability);
		parkourability.get(ChargeJump.class).onJump(player, parkourability);
	}
}
