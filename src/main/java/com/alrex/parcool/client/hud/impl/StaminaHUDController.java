package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.client.gui.ParCoolHUDEvent;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nonnull;

//@OnlyIn(Dist.CLIENT)
public class StaminaHUDController implements GuiLayer {
    public static Identifier ID = Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "hud.stamina");
	LightStaminaHUD lightStaminaHUD;
	StaminaHUD staminaHUD;

	public StaminaHUDController() {
		lightStaminaHUD = new LightStaminaHUD();
		staminaHUD = new StaminaHUD();
	}

	public void onTick(ClientTickEvent.Post event) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || player.isCreative()) return;
		lightStaminaHUD.onTick(event, player);
		staminaHUD.onTick(event, player);
	}

	@Override
	public void render(@Nonnull GuiGraphics graphics, @Nonnull DeltaTracker partialTick) {
		var player = Minecraft.getInstance().player;
		if (player == null) return;
		if (!ParCoolConfig.Client.Booleans.ParCoolIsActive.get()) return;

		Parkourability parkourability = Parkourability.get(player);

		var localStamina = LocalStamina.get(player);
		var stamina = player.getData(Attachments.STAMINA);

		if (ParCoolConfig.Client.Booleans.HideStaminaHUDWhenStaminaIsInfinite.get() &&
				parkourability.getActionInfo().isStaminaInfinite(localStamina, player)
		) return;

		if (!localStamina.shouldShowHUD(player)) return;

		if (NeoForge.EVENT_BUS.post(new ParCoolHUDEvent.RenderEvent(graphics, partialTick)).isCanceled())
			return;

		switch (ParCoolConfig.Client.getInstance().StaminaHUDType.get()) {
			case Light:
				lightStaminaHUD.render(graphics, parkourability, stamina, partialTick.getGameTimeDeltaPartialTick(true));
				break;
			case Normal:
				staminaHUD.render(graphics, parkourability, stamina, partialTick.getGameTimeDeltaPartialTick(true));
				break;
		}
	}
}
