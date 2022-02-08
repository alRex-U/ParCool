package com.alrex.parcool.common.event;

import com.alrex.parcool.common.capability.provider.AnimationProvider;
import com.alrex.parcool.common.capability.provider.ParkourabilityProvider;
import com.alrex.parcool.common.capability.provider.StaminaProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventAttachCapability {

	@SubscribeEvent
	public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (!(event.getObject() instanceof PlayerEntity)) return;
		event.addCapability(ParkourabilityProvider.CAPABILITY_LOCATION, new ParkourabilityProvider());
		event.addCapability(StaminaProvider.CAPABILITY_LOCATION, new StaminaProvider());
		if (event.getObject().getEntityWorld().isRemote) {
			event.addCapability(AnimationProvider.CAPABILITY_LOCATION, new AnimationProvider());
		}
	}
}
