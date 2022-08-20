package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilitiesRegistry {
	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event) {
		event.register(Parkourability.class);
		event.register(Stamina.class);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerClient(RegisterCapabilitiesEvent event) {
		event.register(Animation.class);
	}
}
