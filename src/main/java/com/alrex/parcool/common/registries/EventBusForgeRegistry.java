package com.alrex.parcool.common.registries;

import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.ActionProcessor;
import com.alrex.parcool.common.event.EventAttachCapability;
import com.alrex.parcool.common.event.EventOpenSettingsParCool;
import com.alrex.parcool.common.event.EventPlayerFall;
import com.alrex.parcool.common.event.EventPlayerJoin;
import net.minecraftforge.eventbus.api.IEventBus;

public class EventBusForgeRegistry {
	public static void register(IEventBus bus) {
		bus.register(EventAttachCapability.class);
		bus.register(EventPlayerJoin.class);
		bus.register(EventPlayerFall.class);
		bus.register(new ActionProcessor());
	}

	public static void registerClient(IEventBus bus) {
		bus.register(KeyRecorder.class);
		bus.register(EventOpenSettingsParCool.class);
	}
}
