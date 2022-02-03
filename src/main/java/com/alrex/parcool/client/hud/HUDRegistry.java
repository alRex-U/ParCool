package com.alrex.parcool.client.hud;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.LinkedList;

public class HUDRegistry {
	private static HUDRegistry instance = null;

	public static HUDRegistry getInstance() {
		if (instance == null) instance = new HUDRegistry();
		return instance;
	}

	private final LinkedList<AbstractHUD> huds = new LinkedList<>();

	public LinkedList<AbstractHUD> getHuds() {
		return huds;
	}

	@SubscribeEvent
	public void onOverlay(RenderGameOverlayEvent.Pre event) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;
		huds.forEach((hud) -> hud.render(event, event.getMatrixStack()));
	}
}
