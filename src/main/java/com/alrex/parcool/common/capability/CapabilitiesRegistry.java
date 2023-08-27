package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.capability.impl.Animation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilitiesRegistry {
	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event) {
		event.register(com.alrex.parcool.common.capability.Parkourability.class);
		event.register(com.alrex.parcool.common.capability.IStamina.class);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerClient(RegisterCapabilitiesEvent event) {
		event.register(Animation.class);
	}
}
