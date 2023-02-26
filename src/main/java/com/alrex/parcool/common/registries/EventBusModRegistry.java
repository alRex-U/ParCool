package com.alrex.parcool.common.registries;

import com.alrex.parcool.client.hud.HUDRegistry;
import net.minecraftforge.eventbus.api.IEventBus;

public class EventBusModRegistry {
	public static void register(IEventBus bus) {
	}

	public static void registerClient(IEventBus bus) {
		bus.addListener(HUDRegistry.getInstance()::onSetup);
	}
}
