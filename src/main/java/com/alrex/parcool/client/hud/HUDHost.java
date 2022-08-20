package com.alrex.parcool.client.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

import java.util.LinkedList;

public class HUDHost implements IIngameOverlay {
	private static HUDHost instance = null;
	public static final IIngameOverlay parCoolOverlay = OverlayRegistry.registerOverlayTop("ParCool HUDs", getInstance());

	public static HUDHost getInstance() {
		if (instance == null) instance = new HUDHost();
		return instance;
	}

	private final LinkedList<AbstractHUD> huds = new LinkedList<>();

	public LinkedList<AbstractHUD> getHuds() {
		return huds;
	}

	@Override
	public void render(ForgeIngameGui gui, PoseStack mStack, float partialTicks, int width, int height) {
		huds.forEach((hud) -> {
			hud.render(gui, mStack, partialTicks, width, height);
		});
	}
}
