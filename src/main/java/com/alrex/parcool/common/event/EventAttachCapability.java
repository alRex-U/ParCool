package com.alrex.parcool.common.event;

import com.alrex.parcool.common.capability.provider.AnimationProvider;
import com.alrex.parcool.common.capability.provider.ParkourabilityProvider;
import com.alrex.parcool.common.capability.provider.StaminaProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventAttachCapability {

	@SubscribeEvent
	public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (!(event.getObject() instanceof Player)) return;
		event.addCapability(ParkourabilityProvider.CAPABILITY_LOCATION, new ParkourabilityProvider());
		event.addCapability(StaminaProvider.CAPABILITY_LOCATION, new StaminaProvider());
		if (event.getObject().level.isClientSide) {
			event.addCapability(AnimationProvider.CAPABILITY_LOCATION, new AnimationProvider());
		}
	}
}
