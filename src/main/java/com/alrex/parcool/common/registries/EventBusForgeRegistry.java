package com.alrex.parcool.common.registries;

import com.alrex.parcool.client.hud.HUDRegistry;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.ActionProcessor;
import com.alrex.parcool.common.event.*;
import net.minecraftforge.eventbus.api.IEventBus;

public class EventBusForgeRegistry {
	public static void register(IEventBus bus) {
		bus.register(EventPlayerJump.class);
		bus.register(EventAttachCapability.class);
		bus.register(EventSendPermissions.class);
		bus.register(EventPlayerFall.class);
		bus.register(EventPlayerClone.class);
		bus.register(new ActionProcessor());
	}

	public static void registerClient(IEventBus bus) {
		bus.register(HUDRegistry.getInstance());
		bus.register(KeyRecorder.class);
		bus.register(EventOpenSettingsParCool.class);
		bus.addListener(HUDRegistry.getInstance()::onTick);
	}
}
