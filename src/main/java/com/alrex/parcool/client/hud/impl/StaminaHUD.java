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
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Tuple;
import net.neoforged.neoforge.client.event.ClientTickEvent;

//@OnlyIn(Dist.CLIENT)
public class StaminaHUD {
    public static final Identifier STAMINA_CHARGING = Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "hud/large_stamina/stamina_bar_charging");
    public static final Identifier STAMINA_DEPLETED = Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "hud/large_stamina/stamina_bar_depleted");
    public static final Identifier STAMINA_EMPTY_CHARGE = Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "hud/large_stamina/stamina_bar_empty");
    public static final Identifier STAMINA_EMPTY = Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "hud/large_stamina/stamina_bar_empty_no_charge");
    public static final Identifier[] STAMINA_FULL = new Identifier[]{
            Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "hud/large_stamina/stamina_bar_full_1"),
            Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "hud/large_stamina/stamina_bar_full_2"),
            Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "hud/large_stamina/stamina_bar_full_3")
	};

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

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, STAMINA_EMPTY, 93, 17, 0, 0, pos.getA(), pos.getB(), 93, 17);
		if (!stamina.isExhausted()) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, STAMINA_EMPTY_CHARGE, 93, 17, 0, 0, pos.getA(), pos.getB(), (int) Math.ceil(92 * statusScale), 17);
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, STAMINA_CHARGING, 93, 17, 0, 0, pos.getA(), pos.getB(), Math.round(16 + 69 * shadowScale) + 1, 12);
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, STAMINA_FULL[renderGageType], 93, 17, 0, 0, pos.getA(), pos.getB(), Math.round(16 + 69 * staminaScale) + 1, 12);
		} else {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, STAMINA_DEPLETED, 93, 17, 0, 0, pos.getA(), pos.getB(), Math.round(16 + 69 * staminaScale) + 1, 17);
		}
		shadowScale = staminaScale - (staminaScale - shadowScale) / 1.1f;
	}
}
