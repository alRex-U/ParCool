package com.alrex.parcool.common.registries;

import com.alrex.parcool.client.hud.StaminaHUD;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.client.renderer.PlayerRenderEventHandler;
import com.alrex.parcool.common.event.EventActivateParCool;
import com.alrex.parcool.common.event.EventAttachCapability;
import com.alrex.parcool.common.processor.*;
import net.minecraftforge.eventbus.api.IEventBus;

public class EventBusForgeRegistry {
	public static void register(IEventBus bus) {
		bus.register(new StaminaHUD());
		bus.register(KeyRecorder.class);
		bus.register(PlayerRenderEventHandler.class);

		bus.register(EventActivateParCool.class);
		bus.register(EventAttachCapability.class);

		bus.register(CrawlLogic.class);
		bus.register(DodgeLogic.class);
		bus.register(FastRunningLogic.class);
		bus.register(GrabCliffLogic.class);
		bus.register(JumpBoostLogic.class);
		bus.register(RollLogic.class);
		bus.register(StaminaLogic.class);
		bus.register(VaultLogic.class);
		bus.register(WallJumpLogic.class);
	}
}
