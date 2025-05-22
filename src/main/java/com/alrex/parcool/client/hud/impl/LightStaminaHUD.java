package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.Effects;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class LightStaminaHUD {
	public static final ResourceLocation STAMINA_CHARGED_MAX = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "hud/stamina_charged_max");
	public static final ResourceLocation STAMINA_CHARGED_FULL = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "hud/stamina_charged_full");
	public static final ResourceLocation STAMINA_CHARGED_HALF = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "hud/stamina_charged_half");
	public static final ResourceLocation STAMINA_CHARGED_EMPTY = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "hud/stamina_charged_empty");
	public static final ResourceLocation STAMINA_DEPLETED_FULL = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "hud/stamina_depleted_full");
	public static final ResourceLocation STAMINA_DEPLETED_HALF = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "hud/stamina_depleted_half");
	public static final ResourceLocation STAMINA_DEPLETED_EMPTY = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "hud/stamina_depleted_empty");
	public static final ResourceLocation STAMINA_INFINITE_FULL = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "hud/stamina_infinite_full");
	public static final ResourceLocation STAMINA_INFINITE_HALF = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "hud/stamina_infinite_half");
	public static final ResourceLocation STAMINA_INFINITE_EMPTY = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "hud/stamina_infinite_empty");
	public static final ResourceLocation STAMINA_NORMAL_FULL = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "hud/stamina_normal_full");
	public static final ResourceLocation STAMINA_NORMAL_HALF = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "hud/stamina_normal_half");
	public static final ResourceLocation STAMINA_NORMAL_EMPTY = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "hud/stamina_normal_empty");

	private enum Type {
		NORMAL(new ResourceLocation[]{STAMINA_NORMAL_EMPTY, STAMINA_NORMAL_HALF, STAMINA_NORMAL_FULL, STAMINA_CHARGED_MAX}),
		DEPLETED(new ResourceLocation[]{STAMINA_DEPLETED_EMPTY, STAMINA_DEPLETED_HALF, STAMINA_DEPLETED_FULL, STAMINA_CHARGED_MAX}),
		INFINITE(new ResourceLocation[]{STAMINA_INFINITE_EMPTY, STAMINA_INFINITE_HALF, STAMINA_INFINITE_FULL, STAMINA_CHARGED_MAX}),
		CHARGED(new ResourceLocation[]{STAMINA_CHARGED_EMPTY, STAMINA_CHARGED_HALF, STAMINA_CHARGED_FULL, STAMINA_CHARGED_MAX});
		private ResourceLocation[] list;

		Type(ResourceLocation[] list) {
			this.list = list;
		}

		private ResourceLocation getTexture(Size size) {
			return list[size.ordinal()];
		}
	}

	private enum Size {EMPTY, HALF, FULL, MAX}

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
        Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		var stamina = player.getData(Attachments.STAMINA);
		int newValue = stamina.value();
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
		justBecameMax = oldValue < newValue && newValue == stamina.max();

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

	public void render(GuiGraphics graphics, Parkourability parkourability, ReadonlyStamina stamina, float partialTick) {
		var player = Minecraft.getInstance().player;
		if (player == null) return;
		final boolean inexhaustible = player.hasEffect(Effects.INEXHAUSTIBLE);
		final boolean exhausted = stamina.isExhausted();

		if (!showStatus) {
			long gameTime = player.level().getGameTime();
			if (gameTime - lastStaminaChangedTick > 40 && !ParCoolConfig.Client.Booleans.ShowLightStaminaHUDAlways.get())
				return;
		}
		float staminaScale = (float) stamina.value() / stamina.max();
		if (staminaScale < 0) staminaScale = 0;
		if (staminaScale > 1) staminaScale = 1;

        staminaScale *= 10f;
		float statusScale = showStatus ? MathUtil.lerp(oldStatusValue, statusValue, partialTick) * 10f : 0f;

		final int width = graphics.guiWidth();
		final int height = graphics.guiHeight();
        int baseX = width / 2 + 91 + ParCoolConfig.Client.Integers.HorizontalOffsetOfLightStaminaHUD.get();
		int baseY = height - Minecraft.getInstance().gui.rightHeight + ParCoolConfig.Client.Integers.VerticalOffsetOfLightStaminaHUD.get();
		for (int i = 0; i < 10; i++) {
			int x = baseX - i * 8 - 9;
			int offsetY = 0;
			Type type;
			Size size;
            if (inexhaustible) {
                if (showStatus) {
                    if (statusScale > i + 0.9f) {
						type = Type.CHARGED;
                    } else {
						type = Type.NORMAL;
                    }
                } else {
					type = Type.INFINITE;
                }
            } else {
                if (exhausted) {
					type = Type.DEPLETED;
                } else if (statusScale > i + 0.9f) {
					type = Type.CHARGED;
                } else {
					type = Type.NORMAL;
                }
            }
			if (justBecameMax) {
				size = Size.MAX;
			} else if (staminaScale < i) {
				size = Size.EMPTY;
			} else if (staminaScale < i + 0.5f) {
				size = Size.HALF;
			} else {
				size = Size.FULL;
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

			graphics.blitSprite(RenderType::guiTextured, type.getTexture(size), 9, 9, 0, 0, x, baseY + offsetY, 9, 9);
		}
		Minecraft.getInstance().gui.rightHeight += 10;
	}
}
