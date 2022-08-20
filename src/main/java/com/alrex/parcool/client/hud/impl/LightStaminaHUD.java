package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.hud.AbstractHUD;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static com.alrex.parcool.client.hud.impl.StaminaHUD.STAMINA;

public class LightStaminaHUD extends AbstractHUD {
	private int oldValue = 0;
	private long lastChangedTick = 0;

	public LightStaminaHUD(Position pos) {
		super(pos);
	}

	@Override
	public void render(ForgeIngameGui gui, PoseStack mStack, float partialTicks, int width, int height) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || player.isEyeInFluid(FluidTags.WATER)) return;
		if (player.isCreative()) return;

		Stamina stamina = Stamina.get(player);
		Parkourability parkourability = Parkourability.get(player);
		if (stamina == null || parkourability == null) return;

		if (ParCoolConfig.CONFIG_CLIENT.infiniteStamina.get() && parkourability.getActionInfo().isStaminaInfinite())
			return;

		if (stamina.getStamina() == 0) return;
		long gameTime = player.level.getGameTime();
		if (stamina.getStamina() != oldValue) {
			lastChangedTick = gameTime;
		} else if (gameTime - lastChangedTick > 40) return;

		oldValue = stamina.getStamina();
		float staminaScale = (float) stamina.getStamina() / stamina.getMaxStamina();
		if (staminaScale < 0) staminaScale = 0;
		if (staminaScale > 1) staminaScale = 1;
		Minecraft mc = Minecraft.getInstance();
		int scaledWidth = mc.getWindow().getGuiScaledWidth();
		int scaledHeight = mc.getWindow().getGuiScaledHeight();

		int iconNumber = (int) Math.floor(staminaScale * 10);
		float iconPartial = (staminaScale * 10) - iconNumber;

		RenderSystem.setShaderTexture(0, STAMINA);
		int baseX = scaledWidth / 2 + 92;
		int y = scaledHeight - 49 + ParCoolConfig.CONFIG_CLIENT.offsetVerticalLightStaminaHUD.get();
		for (int i = 1; i <= 10; i++) {
			int x = baseX - i * 8 - 1;
			int textureX;
			if (iconNumber >= i || (iconNumber + 1 == i && iconPartial > 0.3)) {
				textureX = 0;
			} else if (iconNumber + 1 == i) {
				textureX = 8;
			} else break;
			if (stamina.isExhausted()) {
				textureX += 16;
			}
			AbstractHUD.blit(mStack, x, y, textureX, 119f, 8, 9, 128, 128);
		}
	}
}
