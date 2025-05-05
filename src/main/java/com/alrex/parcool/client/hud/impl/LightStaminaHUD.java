package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.api.Effects;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.stamina.LocalStamina;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.MathUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.client.event.ClientTickEvent;

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
	private int oldValue = 0;

	public void onTick(ClientTickEvent.Post event, LocalPlayer player) {
		LocalStamina stamina = LocalStamina.get();
        Parkourability parkourability = Parkourability.get(player);
        if (stamina == null || parkourability == null) return;
		int newValue = stamina.getValue();
		changingSign = (int) Math.signum(newValue - oldValue);
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
		if (newValue != oldValue || stamina.isExhausted()) {
			lastStaminaChangedTick = gameTime;
		}
		justBecameMax = oldValue < newValue && newValue == stamina.getMax();

        oldStatusValue = statusValue;
        boolean oldShowStatus = showStatus;
        showStatus = false;
        if (ParCoolConfig.Client.Booleans.ShowActionStatusBar.get()) {
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
        }
        if (!oldShowStatus && showStatus) {
            oldStatusValue = statusValue;
        }
		oldValue = newValue;
	}

	public void render(GuiGraphics graphics, DeltaTracker partialTick) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || player.isCreative()) return;

		LocalStamina stamina = LocalStamina.get();
		Parkourability parkourability = Parkourability.get(player);
		if (stamina == null || parkourability == null) return;

		final boolean inexhaustible = player.hasEffect(Effects.INEXHAUSTIBLE);
        final boolean exhausted = stamina.isExhausted();

        if (!showStatus) {
            long gameTime = player.level().getGameTime();
            if (gameTime - lastStaminaChangedTick > 40) return;
        }
		float staminaScale = (float) stamina.getValue() / stamina.getMax();
		if (staminaScale < 0) staminaScale = 0;
		if (staminaScale > 1) staminaScale = 1;

        staminaScale *= 10f;
		float statusScale = showStatus ? MathUtil.lerp(oldStatusValue, statusValue, partialTick.getGameTimeDeltaPartialTick(true)) * 10f : 0f;

        RenderSystem.setShaderTexture(0, StaminaHUD.STAMINA);
		final int width = graphics.guiWidth();
		final int height = graphics.guiHeight();
        int baseX = width / 2 + 91 + ParCoolConfig.Client.Integers.HorizontalOffsetOfLightStaminaHUD.get();
		int baseY = height - Minecraft.getInstance().gui.rightHeight + ParCoolConfig.Client.Integers.VerticalOffsetOfLightStaminaHUD.get();
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
				if ((changingTimeTick & 0b11111) == i) {
					offsetY = -1;
				}
			} else if (i + 1 > staminaScale && staminaScale > i && changingSign == -1) {
				offsetY = randomOffset;
			}

			graphics.blit(StaminaHUD.STAMINA, x, baseY + offsetY, textureX, 119, 9, 9, 128, 128);
		}
		Minecraft.getInstance().gui.rightHeight += 10;
	}
}
