package com.alrex.parcool.client.hud.impl;


import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class StaminaHUD extends AbstractGui {
	public static final ResourceLocation STAMINA = new ResourceLocation("parcool", "textures/gui/stamina_bar.png");

	public StaminaHUD() {
	}

	private float shadowScale = 1f;
	//0,1,2
	private int renderGageType = 0;
	private int renderGageTick = 0;

	public void onTick(TickEvent.ClientTickEvent event, ClientPlayerEntity player) {
		if (++renderGageTick >= 5) {
			renderGageTick = 0;
			if (++renderGageType > 2) {
				renderGageType = 0;
			}
		}
	}

	public void render(RenderGameOverlayEvent.Post event, MatrixStack stack) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null) return;
		if (player.isCreative()) return;

		IStamina stamina = IStamina.get(player);
		Parkourability parkourability = Parkourability.get(player);
		if (stamina == null || parkourability == null) return;

		if (ParCoolConfig.Client.Booleans.HideStaminaHUDWhenStaminaIsInfinite.get() &&
				parkourability.getActionInfo().isStaminaInfinite(player.isCreative() || player.isSpectator())
		) return;

		MainWindow window = Minecraft.getInstance().getWindow();
		Position position = new Position(
				ParCoolConfig.Client.AlignHorizontalStaminaHUD.get(),
				ParCoolConfig.Client.AlignVerticalStaminaHUD.get(),
				ParCoolConfig.Client.Integers.HorizontalMarginOfStaminaHUD.get(),
				ParCoolConfig.Client.Integers.VerticalMarginOfStaminaHUD.get()
		);
		final int width = window.getGuiScaledWidth();
		final int height = window.getGuiScaledHeight();
		final int boxWidth = 91;
		final int boxHeight = 17;
		final Tuple<Integer, Integer> pos = position.calculate(boxWidth, boxHeight, width, height);

		float staminaScale = (float) stamina.get() / stamina.getActualMaxStamina();
		float coolTimeScale =
				Math.min(
						parkourability.get(Dodge.class).getCoolDownPhase(),
						parkourability.get(CatLeap.class).getCoolDownPhase()
				);
		if (staminaScale < 0) staminaScale = 0;
		if (staminaScale > 1) staminaScale = 1;

		Minecraft.getInstance().getTextureManager().bind(STAMINA);
		blit(stack, pos.getA(), pos.getB(), 0, 0, 93, 17, 128, 128);
		if (!stamina.isExhausted()) {
			blit(stack, pos.getA(), pos.getB(), 0, 102, (int) Math.ceil(92 * coolTimeScale), 17, 128, 128);
			blit(stack, pos.getA(), pos.getB(), 0, 85, Math.round(16 + 69 * shadowScale) + 1, 12, 128, 128);
			blit(stack, pos.getA(), pos.getB(), 0, 17 * (renderGageType + 1), Math.round(16 + 69 * staminaScale) + 1, 12, 128, 128);
		} else {
			blit(stack, pos.getA(), pos.getB(), 0, 68, Math.round(16 + 69 * staminaScale) + 1, 17, 128, 128);
		}
		shadowScale = staminaScale - (staminaScale - shadowScale) / 1.1f;
	}
}
