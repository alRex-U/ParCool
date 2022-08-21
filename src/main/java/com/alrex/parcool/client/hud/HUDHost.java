package com.alrex.parcool.client.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.LinkedList;

public class HUDHost implements IGuiOverlay {
	private static HUDHost instance = null;

	public static HUDHost getInstance() {
		if (instance == null) instance = new HUDHost();
		return instance;
	}

	private final LinkedList<AbstractHUD> huds = new LinkedList<>();

	public LinkedList<AbstractHUD> getHuds() {
		return huds;
	}

	@SubscribeEvent
	public void register(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll("parcool", this);
	}

	@Override
	public void render(ForgeGui gui, PoseStack mStack, float partialTicks, int width, int height) {
		huds.forEach((hud) -> {
			hud.render(gui, mStack, partialTicks, width, height);
		});
	}
}
