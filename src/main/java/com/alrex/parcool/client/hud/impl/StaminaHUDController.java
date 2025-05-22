package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.api.client.gui.ParCoolHUDEvent;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.stamina.ParCoolStamina;
import com.alrex.parcool.config.ParCoolConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class StaminaHUDController {
	LightStaminaHUD lightStaminaHUD;
	StaminaHUD staminaHUD;

	public StaminaHUDController() {
		lightStaminaHUD = new LightStaminaHUD();
		staminaHUD = new StaminaHUD();
	}

	public void onTick(TickEvent.ClientTickEvent event) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null || player.isCreative()) return;
		lightStaminaHUD.onTick(event, player);
		staminaHUD.onTick(event, player);
		IStamina stamina = IStamina.get(player);
		if (stamina == null) return;
		stamina.updateOldValue();
	}

	public void render(RenderGameOverlayEvent.Post event, MatrixStack stack) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null) return;
		if (!ParCoolConfig.Client.Booleans.ParCoolIsActive.get()) return;
		if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;

		if (ParCoolConfig.Client.Booleans.HideStaminaHUDWhenStaminaIsInfinite.get() &&
				parkourability.getActionInfo().isStaminaInfinite(player.isCreative() || player.isSpectator())
		) return;

		IStamina stamina = IStamina.get(player);
		if (stamina == null) return;

		if (!(stamina instanceof ParCoolStamina)) return;

		if (MinecraftForge.EVENT_BUS.post(new ParCoolHUDEvent.RenderEvent(event, stack))) return;

		switch (ParCoolConfig.Client.StaminaHUDType.get()) {
			case Light:
				lightStaminaHUD.render(event, stack, parkourability, stamina);
				break;
			case Normal:
				staminaHUD.render(event, stack, parkourability, stamina);
				break;
		}
	}
}
