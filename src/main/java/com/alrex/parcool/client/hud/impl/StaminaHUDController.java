package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.hud.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class StaminaHUDController {
	LightStaminaHUD lightStaminaHUD;
	StaminaHUD staminaHUD;

	public StaminaHUDController(Position pos) {
		lightStaminaHUD = new LightStaminaHUD();
		staminaHUD = new StaminaHUD(pos);
	}

	public void onTick(TickEvent.ClientTickEvent event) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null || player.isCreative()) return;
		lightStaminaHUD.onTick(event, player);
		staminaHUD.onTick(event, player);
	}

	public void render(RenderGameOverlayEvent.Post event, MatrixStack stack) {
		ParCoolConfig.Client config = ParCoolConfig.CONFIG_CLIENT;
		if (config.hideStaminaHUD.get() || !config.parCoolActivation.get() || config.useHungerBarInsteadOfStamina.get())
			return;
		if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

		if (config.useLightHUD.get()) {
			lightStaminaHUD.render(event, stack);
		} else {
			staminaHUD.render(event, stack);
		}
	}
}
