package com.alrex.parcool.common.registries;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.AnimationHandler;
import com.alrex.parcool.client.hud.HUDHost;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.client.hud.impl.RollDefermentHUD;
import com.alrex.parcool.client.hud.impl.StaminaHUDController;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.ActionProcessor;
import com.alrex.parcool.common.event.EventAttachCapability;
import com.alrex.parcool.common.event.EventOpenSettingsParCool;
import com.alrex.parcool.common.event.EventPlayerDamaged;
import com.alrex.parcool.common.event.EventSendPermission;
import net.minecraftforge.eventbus.api.IEventBus;

public class EventBusForgeRegistry {
	public static void register(IEventBus bus) {
		bus.register(EventAttachCapability.class);
		bus.register(EventSendPermission.class);
		bus.register(EventPlayerDamaged.class);
		bus.register(new ActionProcessor());
	}

	public static void registerClient(IEventBus bus) {
		bus.register(HUDHost.getInstance());
		HUDHost.getInstance().getHuds().add(
				new StaminaHUDController(
						new Position(
								ParCoolConfig.CONFIG_CLIENT.alignHorizontalStaminaHUD.get(),
								ParCoolConfig.CONFIG_CLIENT.alignVerticalStaminaHUD.get(),
								ParCoolConfig.CONFIG_CLIENT.marginHorizontalStaminaHUD.get(),
								ParCoolConfig.CONFIG_CLIENT.marginVerticalStaminaHUD.get()
						)
				));
		HUDHost.getInstance().getHuds().add(
				new RollDefermentHUD()
		);
		bus.register(KeyRecorder.class);
		bus.register(new AnimationHandler());
		bus.register(EventOpenSettingsParCool.class);
	}
}
