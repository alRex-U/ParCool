package com.alrex.parcool.client.hud;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.hud.impl.StaminaHUDController;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HUDRegistry {
	private static HUDRegistry instance = null;

	public static HUDRegistry getInstance() {
		if (instance == null) instance = new HUDRegistry();
		return instance;
	}

	private final StaminaHUDController staminaHUD = new StaminaHUDController(
			new Position(
					ParCoolConfig.CONFIG_CLIENT.alignHorizontalStaminaHUD.get(),
					ParCoolConfig.CONFIG_CLIENT.alignVerticalStaminaHUD.get(),
					ParCoolConfig.CONFIG_CLIENT.marginHorizontalStaminaHUD.get(),
					ParCoolConfig.CONFIG_CLIENT.marginVerticalStaminaHUD.get()
			)
	);

	@SubscribeEvent
	public void onOverlay(RenderGameOverlayEvent.Post event) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;
		staminaHUD.render(event, event.getMatrixStack());
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;
		staminaHUD.onTick(event);
	}
}
