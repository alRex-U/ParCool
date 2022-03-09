package com.alrex.parcool.client.hud.impl;


import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.hud.AbstractHUD;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.Date;

@OnlyIn(Dist.CLIENT)
public class StaminaHUD extends AbstractHUD {
	public static final ResourceLocation STAMINA = new ResourceLocation("parcool", "textures/gui/stamina_bar.png");

	public StaminaHUD(Position pos) {
		super(pos);
	}

	private float shadowScale = 1f;

	public void render(ForgeIngameGui gui, PoseStack stack, float partialTicks, int width, int height) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return;
		if (player.isCreative()) return;

		Stamina stamina = Stamina.get(player);
		Parkourability parkourability = Parkourability.get(player);
		if (stamina == null || parkourability == null) return;

		if (ParCoolConfig.CONFIG_CLIENT.infiniteStamina.get() && parkourability.getActionInfo().isStaminaInfinite())
			return;
		int renderGage = (int) ((new Date().getTime() / 500) % 3);

		Window window = Minecraft.getInstance().getWindow();
		final int boxWidth = 91;
		final int boxHeight = 17;
		final Tuple<Integer, Integer> pos = position.calculate(boxWidth, boxHeight, width, height);

		float staminaScale = (float) stamina.getStamina() / stamina.getMaxStamina();
		float dodgeCoolTimeScale = (float) (parkourability.getActionInfo().getDodgeCoolTick() - parkourability.getDodge().getCoolTime()) / parkourability.getActionInfo().getDodgeCoolTick();
		if (staminaScale < 0) staminaScale = 0;
		if (staminaScale > 1) staminaScale = 1;

		RenderSystem.setShaderTexture(0, STAMINA);
		GuiComponent.blit(stack, pos.getA(), pos.getB(), 0f, 0f, 92, 16, 128, 128);
		if (!stamina.isExhausted()) {
			GuiComponent.blit(stack, pos.getA(), pos.getB(), 0f, 102f, (int) Math.ceil(92 * dodgeCoolTimeScale), 16, 128, 128);
			GuiComponent.blit(stack, pos.getA(), pos.getB(), 0f, 85f, Math.round(16 + 69 * shadowScale) + 1, 12, 128, 128);
			GuiComponent.blit(stack, pos.getA(), pos.getB(), 0f, 17 * (renderGage + 1), Math.round(16 + 69 * staminaScale) + 1, 12, 128, 128);
		} else {
			GuiComponent.blit(stack, pos.getA(), pos.getB(), 0f, 68f, Math.round(16 + 69 * staminaScale) + 1, 16, 128, 128);
		}
		shadowScale = staminaScale - (staminaScale - shadowScale) / 1.1f;
	}
}
