package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.hud.AbstractHUD;
import com.alrex.parcool.client.hud.Position;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;

@OnlyIn(Dist.CLIENT)
public class StaminaHUDController extends AbstractHUD {
	LightStaminaHUD lightStaminaHUD;
	StaminaHUD staminaHUD;

	public StaminaHUDController(Position pos) {
		super(pos);
		lightStaminaHUD = new LightStaminaHUD(pos);
		staminaHUD = new StaminaHUD(pos);
	}

	@Override
	public void render(ForgeGui gui, PoseStack mStack, float partialTicks, int width, int height) {
		ParCoolConfig.Client config = ParCoolConfig.CONFIG_CLIENT;
		if (config.hideStaminaHUD.get() || !config.parCoolActivation.get() || config.useHungerBarInsteadOfStamina.get())
			return;

		if (config.useLightHUD.get()) {
			lightStaminaHUD.render(gui, mStack, partialTicks, width, height);
		} else {
			staminaHUD.render(gui, mStack, partialTicks, width, height);
		}
	}
}
