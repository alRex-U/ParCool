package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.capability.capabilities.Capabilities;
import com.alrex.parcool.common.capability.provider.AnimationProvider;
import com.alrex.parcool.common.capability.provider.ParkourabilityProvider;
import com.alrex.parcool.common.capability.provider.StaminaProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AttachCapabilityHandler {

	@SubscribeEvent
	public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (!(event.getObject() instanceof PlayerEntity)) return;
		PlayerEntity player = (PlayerEntity) event.getObject();
		event.addCapability(Capabilities.PARKOURABILITY_LOCATION, new ParkourabilityProvider());
		event.addCapability(Capabilities.STAMINA_LOCATION, new StaminaProvider(player));
		if (event.getObject().level.isClientSide) {
			event.addCapability(Capabilities.ANIMATION_LOCATION, new AnimationProvider());
		}
	}
}
