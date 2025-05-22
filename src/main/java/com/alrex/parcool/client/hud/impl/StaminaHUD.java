package com.alrex.parcool.client.hud.impl;


import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@OnlyIn(Dist.CLIENT)
public class StaminaHUD {
	public static final ResourceLocation STAMINA = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "textures/gui/stamina_bar.png");

	public StaminaHUD() {
	}

	private float shadowScale = 1f;
	//0,1,2
	private int renderGageType = 0;
	private int renderGageTick = 0;
    private float statusValue = 0f;
    private float oldStatusValue = 0f;
    private boolean showStatus = false;

	public void onTick(ClientTickEvent.Post event, LocalPlayer player) {
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
		if (++renderGageTick >= 5) {
			renderGageTick = 0;
			if (++renderGageType > 2) {
				renderGageType = 0;
			}
		}
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

	public void render(GuiGraphics graphics, Parkourability parkourability, ReadonlyStamina stamina, float partialTick) {
		Position position = new Position(
				ParCoolConfig.Client.getInstance().AlignHorizontalStaminaHUD.get(),
				ParCoolConfig.Client.getInstance().AlignVerticalStaminaHUD.get(),
                ParCoolConfig.Client.Integers.HorizontalOffsetOfStaminaHUD.get(),
                ParCoolConfig.Client.Integers.VerticalOffsetOfStaminaHUD.get()
		);
		final int boxWidth = 91;
		final int boxHeight = 17;
		final int width = graphics.guiWidth();
		final int height = graphics.guiHeight();
		final Tuple<Integer, Integer> pos = position.calculate(boxWidth, boxHeight, width, height);

		float staminaScale = (float) stamina.value() / stamina.max();
		float statusScale = showStatus ? MathUtil.lerp(oldStatusValue, statusValue, partialTick) : 0f;

		if (staminaScale < 0) staminaScale = 0;
		if (staminaScale > 1) staminaScale = 1;

		graphics.blit(STAMINA, pos.getA(), pos.getB(), 0, 0, 93, 17, 128, 128);
		if (!stamina.isExhausted()) {
            graphics.blit(STAMINA, pos.getA(), pos.getB(), 0, 102, (int) Math.ceil(92 * statusScale), 17, 128, 128);
			graphics.blit(STAMINA, pos.getA(), pos.getB(), 0, 85, Math.round(16 + 69 * shadowScale) + 1, 12, 128, 128);
			graphics.blit(STAMINA, pos.getA(), pos.getB(), 0, 17 * (renderGageType + 1), Math.round(16 + 69 * staminaScale) + 1, 12, 128, 128);
		} else {
			graphics.blit(STAMINA, pos.getA(), pos.getB(), 0, 68, Math.round(16 + 69 * staminaScale) + 1, 17, 128, 128);
		}
		shadowScale = staminaScale - (staminaScale - shadowScale) / 1.1f;
	}
}
