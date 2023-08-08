package com.alrex.parcool.client.hud;

import com.alrex.parcool.client.hud.impl.StaminaHUDController;
import com.alrex.parcool.config.ParCoolConfig;
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
					ParCoolConfig.Client.AlignHorizontalStaminaHUD.get(),
					ParCoolConfig.Client.AlignVerticalStaminaHUD.get(),
					ParCoolConfig.Client.Integers.HorizontalMarginOfStaminaHUD.get(),
					ParCoolConfig.Client.Integers.VerticalMarginOfStaminaHUD.get()
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
