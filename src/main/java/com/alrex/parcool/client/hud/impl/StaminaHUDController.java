package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.hud.AbstractHUD;
import com.alrex.parcool.client.hud.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

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
	public void render(RenderGameOverlayEvent.Pre event, MatrixStack stack) {
		ParCoolConfig.Client config = ParCoolConfig.CONFIG_CLIENT;
		if (config.hideStaminaHUD.get() || !config.parCoolActivation.get()) return;
		if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

		if (config.useLightHUD.get()) {
			lightStaminaHUD.render(event, stack);
		} else {
			staminaHUD.render(event, stack);
		}
	}
}
