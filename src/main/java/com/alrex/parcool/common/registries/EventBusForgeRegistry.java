package com.alrex.parcool.common.registries;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.AnimationHandler;
import com.alrex.parcool.client.hud.HUDRegistry;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.client.hud.impl.LightStaminaHUD;
import com.alrex.parcool.client.hud.impl.StaminaHUD;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.ActionProcessor;
import com.alrex.parcool.common.event.EventActivateParCool;
import com.alrex.parcool.common.event.EventAttachCapability;
import com.alrex.parcool.common.event.EventPlayerDamaged;
import com.alrex.parcool.common.event.EventPlayerJoin;
import net.minecraftforge.eventbus.api.IEventBus;

public class EventBusForgeRegistry {
	public static void register(IEventBus bus) {
		bus.register(EventAttachCapability.class);
		bus.register(EventPlayerJoin.class);
		bus.register(EventPlayerDamaged.class);
		bus.register(new ActionProcessor());
	}

	public static void registerClient(IEventBus bus) {
		bus.register(HUDRegistry.getInstance());
		HUDRegistry.getInstance().getHuds().add(
				new StaminaHUD(
						new Position(
								ParCoolConfig.CONFIG_CLIENT.alignHorizontalStaminaHUD.get(),
								ParCoolConfig.CONFIG_CLIENT.alignVerticalStaminaHUD.get(),
								ParCoolConfig.CONFIG_CLIENT.marginHorizontalStaminaHUD.get(),
								ParCoolConfig.CONFIG_CLIENT.marginVerticalStaminaHUD.get()
						)
				));
		HUDRegistry.getInstance().getHuds().add(
				new LightStaminaHUD(null)
		);
		bus.register(KeyRecorder.class);
		bus.register(new AnimationHandler());
		bus.register(EventActivateParCool.class);
	}
}
