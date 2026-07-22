package com.alrex.parcool.client.hud.stamina;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.ParCoolMobEffects;
import com.alrex.parcool.api.client.gui.StaminaDisplayContext;
import com.alrex.parcool.common.Parkourability;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class LightStaminaHUD extends GuiComponent implements IStaminaHUD {
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("parcool", "textures/gui/stamina_bar.png");

	private boolean valueChanging;
	private int tickValueChangingOrNotChanging;
	private int consumingStaminaVibration;

	@Override
	public void tick(Player player, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
		if (valueChanging != (currentContext.value() != oldContext.value())) {
			valueChanging = (currentContext.value() != oldContext.value());
			tickValueChangingOrNotChanging = 0;
		} else {
			tickValueChangingOrNotChanging++;
		}
		if (player.getRandom().nextInt(5) == 0) {
			consumingStaminaVibration = player.getRandom().nextBoolean() ? 1 : -1;
		} else {
			consumingStaminaVibration = 0;
		}
	}

	@Override
	public void render(ForgeGui gui, PoseStack stack, Parkourability parkourability, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext, float partialTick, int width, int height) {
		if (!valueChanging && tickValueChangingOrNotChanging > 40 && ParCool.getConfig().client().staminaHud.hideAutomatically().get())
			return;
		var player = parkourability.player();
		final boolean inexhaustible = player.hasEffect(ParCoolMobEffects.INEXHAUSTIBLE.get());

		float staminaScale = (float) (Mth.lerp(partialTick, oldContext.value(), currentContext.value()) / currentContext.maxValue());
		if (staminaScale < 0) staminaScale = 0;
		if (staminaScale > 1) staminaScale = 1;
		staminaScale *= 10f;

		// TODO: Is it actually needed to show cooldown or other status on hud?
		// float statusScale = showStatus ? MathUtil.lerp(oldStatusValue, statusValue, partialTick) * 10f : 0f;

		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
		int baseX = width / 2 + 91 + ParCool.getConfig().client().staminaHud.offsetHorizontal().get();
		int baseY = height - gui.rightHeight + ParCool.getConfig().client().staminaHud.offsetVertical().get();
		for (int i = 0; i < 10; i++) {
			int x = baseX - i * 8 - 9;
			int offsetY = 0;
			int textureX;
			if (inexhaustible) {
				/*
				if (showStatus) {
					if (statusScale > i + 0.9f) {
						textureX = 90;
					} else {
						textureX = 0;
					}
				} else {
					textureX = 54;
				}
				 */
				textureX = 54;
			} else {
				if (currentContext.exhausted()) {
					textureX = 27;
				} else if (false/* statusScale > i + 0.9f*/) {
					textureX = 90;
				} else {
					textureX = 0;
				}
			}
			if (currentContext.justFilled()) {
				textureX = 81;
			} else if (staminaScale < i) {//empty
				textureX += 18;
			} else if (staminaScale < i + 0.5f) {//not full
				textureX += 9;
			}
			if (currentContext.justFilled()) {
				offsetY = -1;
			} else if (currentContext.value() > oldContext.value()) {
				if ((tickValueChangingOrNotChanging & 31) == i) {
					offsetY = -1;
				}
			} else if (i + 1 > staminaScale && staminaScale > i && currentContext.value() < oldContext.value()) {
				offsetY = consumingStaminaVibration;
			}

			blit(stack, x, baseY + offsetY, textureX, 119, 9, 9, 128, 128);
		}
		gui.rightHeight += 10;
	}
}
