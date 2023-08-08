package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.config.ParCoolConfig;
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
		if (!ParCoolConfig.Client.Booleans.ParCoolIsActive.get() ||
				ParCoolConfig.Client.Booleans.UseHungerBarInstead.get())
			return;
		if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

		switch (ParCoolConfig.Client.StaminaHUDType.get()) {
			case Light:
				lightStaminaHUD.render(event, stack);
				break;
			case Normal:
				staminaHUD.render(event, stack);
				break;
		}
	}
}
