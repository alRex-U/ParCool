package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.hud.AbstractHUD;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class LightStaminaHUD extends AbstractHUD {
	private int oldValue = 0;
	private long lastChangedTick = 0;

	public LightStaminaHUD(Position pos) {
		super(pos);
	}

	@Override
	public void render(RenderGameOverlayEvent.Pre event, MatrixStack stack) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null || player.areEyesInFluid(FluidTags.WATER)) return;
		if (player.isCreative()) return;

		Stamina stamina = Stamina.get(player);
		Parkourability parkourability = Parkourability.get(player);
		if (stamina == null || parkourability == null) return;

		if (ParCoolConfig.CONFIG_CLIENT.infiniteStamina.get() && parkourability.getActionInfo().isStaminaInfinite())
			return;

		if (stamina.getStamina() == 0) return;
		long gameTime = player.getEntityWorld().getGameTime();
		if (stamina.getStamina() != oldValue) {
			lastChangedTick = gameTime;
		} else if (gameTime - lastChangedTick > 40) return;

		oldValue = stamina.getStamina();
		float staminaScale = (float) stamina.getStamina() / stamina.getMaxStamina();
		if (staminaScale < 0) staminaScale = 0;
		if (staminaScale > 1) staminaScale = 1;
		Minecraft mc = Minecraft.getInstance();
		int scaledWidth = mc.getMainWindow().getScaledWidth();
		int scaledHeight = mc.getMainWindow().getScaledHeight();

		int iconNumber = (int) Math.floor(staminaScale * 10);
		float iconPartial = (staminaScale * 10) - iconNumber;

		mc.getTextureManager().bindTexture(StaminaHUD.STAMINA);
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
			AbstractHUD.func_238463_a_(stack, x, y, textureX, 119f, 8, 9, 128, 128);
		}
	}
}
