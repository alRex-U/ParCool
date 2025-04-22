package com.alrex.parcool.common.registries;

import com.alrex.parcool.client.hud.HUDRegistry;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.ActionProcessor;
import com.alrex.parcool.common.handlers.*;
import net.minecraftforge.eventbus.api.IEventBus;

public class EventBusForgeRegistry {
	public static void register(IEventBus bus) {
		bus.register(AttachCapabilityHandler.class);
        bus.register(LoginLogoutHandler.class);
		bus.register(PlayerJumpHandler.class);
		bus.register(PlayerVisibilityHandler.class);
		bus.register(PlayerDamageHandler.class);
		bus.register(PlayerCloneHandler.class);
		bus.register(new ActionProcessor());
	}

	public static void registerClient(IEventBus bus) {
		bus.register(HUDRegistry.getInstance());
		bus.register(KeyRecorder.class);
		bus.register(OpenSettingsParCoolHandler.class);
		bus.register(EnableOrDisableParCoolHandler.class);
		bus.register(PlayerJoinHandler.class);
		bus.register(InputHandler.class);
	}
}
