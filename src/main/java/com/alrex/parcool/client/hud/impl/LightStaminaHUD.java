package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.api.Effects;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.event.TickEvent;

public class LightStaminaHUD {
	private long lastStaminaChangedTick = 0;
	//1-> recovering, -1->consuming, 0->no changing
	private int lastChangingSign = 0;
	private int changingSign = 0;
	private long changingTimeTick = 0;
	private int randomOffset = 0;
	private boolean justBecameMax = false;

	private float statusValue = 0f;
	private float oldStatusValue = 0f;
	private boolean showStatus = false;

	public void onTick(TickEvent.ClientTickEvent event, LocalPlayer player) {
		IStamina stamina = IStamina.get(player);
		Parkourability parkourability = Parkourability.get(player);
		if (stamina == null || parkourability == null) return;
		changingSign = (int) Math.signum(stamina.get() - stamina.getOldValue());
		final long gameTime = player.getCommandSenderWorld().getGameTime();
		if (changingSign != lastChangingSign) {
			lastChangingSign = changingSign;
			changingTimeTick = 0;
		} else {
			changingTimeTick++;
		}
		if (player.getRandom().nextInt(5) == 0) {
			randomOffset += player.getRandom().nextBoolean() ? 1 : -1;
		} else {
			randomOffset = 0;
		}
		if (stamina.get() != stamina.getOldValue() || stamina.isExhausted()) {
			lastStaminaChangedTick = gameTime;
		}
		justBecameMax = stamina.getOldValue() < stamina.get() && stamina.get() == stamina.getActualMaxStamina();

		oldStatusValue = statusValue;
		boolean oldShowStatus = showStatus;
		showStatus = false;
		for (Action a : parkourability.getList()) {
			if (a.wantsToShowStatusBar(player, parkourability)) {
				showStatus = true;
				statusValue = a.getStatusValue(player, parkourability);
				if (statusValue > 1f) {
					statusValue = 1f;
				} else if (statusValue < 0f) {
					statusValue = 0f;
				}
				break;
			}
		}
		if (!oldShowStatus && showStatus) {
			oldStatusValue = statusValue;
		}
	}

	public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || player.isCreative()) return;

		IStamina stamina = IStamina.get(player);
		Parkourability parkourability = Parkourability.get(player);
		if (stamina == null || parkourability == null) return;

		final boolean inexhaustible = player.hasEffect(Effects.INEXHAUSTIBLE.get());
		final boolean exhausted = stamina.isExhausted();

		if (!showStatus) {
			long gameTime = player.level().getGameTime();
			if (gameTime - lastStaminaChangedTick > 40) return;
		}
		float staminaScale = (float) stamina.get() / stamina.getActualMaxStamina();
		if (staminaScale < 0) staminaScale = 0;
		if (staminaScale > 1) staminaScale = 1;

		staminaScale *= 10f;
		float statusScale = showStatus ? MathUtil.lerp(oldStatusValue, statusValue, partialTick) * 10f : 0f;

		RenderSystem.setShaderTexture(0, StaminaHUD.STAMINA);
		int baseX = width / 2 + 91 + ParCoolConfig.Client.Integers.HorizontalOffsetOfLightStaminaHUD.get();
		int baseY = height - gui.rightHeight + ParCoolConfig.Client.Integers.VerticalOffsetOfLightStaminaHUD.get();
		for (int i = 0; i < 10; i++) {
			int x = baseX - i * 8 - 9;
			int offsetY = 0;
			int textureX;
			if (inexhaustible) {
				if (showStatus) {
					if (statusScale > i + 0.9f) {
						textureX = 90;
					} else {
						textureX = 0;
					}
				} else {
					textureX = 54;
				}
			} else {
				if (exhausted) {
					textureX = 27;
				} else if (statusScale > i + 0.9f) {
					textureX = 90;
				} else {
					textureX = 0;
				}
			}
			if (justBecameMax) {
				textureX = 81;
			} else if (staminaScale < i) {//empty
				textureX += 18;
			} else if (staminaScale < i + 0.5f) {//not full
				textureX += 9;
			}
			if (justBecameMax) {
				offsetY = -1;
			} else if (changingSign == 1) {
				if ((changingTimeTick & 31) == i) {
					offsetY = -1;
				}
			} else if (i + 1 > staminaScale && staminaScale > i && changingSign == -1) {
				offsetY = randomOffset;
			}

			graphics.blit(StaminaHUD.STAMINA, x, baseY + offsetY, textureX, 119, 9, 9, 129, 128);
		}
		gui.rightHeight += 10;
	}
}
