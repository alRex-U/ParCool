package com.alrex.parcool.common.registries;

import com.alrex.parcool.client.hud.HUDManager;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.ActionProcessor;
import com.alrex.parcool.common.handlers.*;
import com.alrex.parcool.common.potion.ParCoolBrewingRecipe;
import net.neoforged.bus.api.IEventBus;

public class EventBusForgeRegistry {
	public static void register(IEventBus bus) {
        bus.register(ParCoolBrewingRecipe.class);
        bus.register(PlayerJumpHandler.class);
        bus.register(LoginLogoutHandler.class);
        bus.register(PlayerFallHandler.class);
        bus.register(PlayerCloneHandler.class);
		bus.register(new ActionProcessor());
	}

	public static void registerClient(IEventBus bus) {
		bus.register(KeyRecorder.class);
        bus.register(OpenSettingsParCoolHandler.class);
        bus.register(EnableOrDisableParCoolHandler.class);
        bus.register(PlayerJoinHandler.class);
        bus.register(HUDManager.getInstance());
	}
}
