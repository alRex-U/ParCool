package com.alrex.parcool.client.hud.impl;


import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.MathUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class StaminaHUD extends GuiComponent {
	public static final ResourceLocation STAMINA = new ResourceLocation("parcool", "textures/gui/stamina_bar.png");

	public StaminaHUD() {
	}

	private float shadowScale = 1f;
	//0,1,2
	private int renderGageType = 0;
	private int renderGageTick = 0;
	private float statusValue = 0f;
	private float oldStatusValue = 0f;
	private boolean showStatus = false;

	public void onTick(TickEvent.ClientTickEvent event, LocalPlayer player) {
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

	public void render(ForgeGui gui, PoseStack stack, float partialTick, int width, int height) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return;
		if (player.isCreative()) return;

		IStamina stamina = IStamina.get(player);
		Parkourability parkourability = Parkourability.get(player);
		if (stamina == null || parkourability == null) return;

		if (ParCoolConfig.Client.Booleans.HideStaminaHUDWhenStaminaIsInfinite.get() &&
				parkourability.getActionInfo().isStaminaInfinite(player.isCreative() || player.isSpectator())
		) return;

		var window = Minecraft.getInstance().getWindow();
		Position position = new Position(
				ParCoolConfig.Client.AlignHorizontalStaminaHUD.get(),
				ParCoolConfig.Client.AlignVerticalStaminaHUD.get(),
				ParCoolConfig.Client.Integers.HorizontalOffsetOfStaminaHUD.get(),
				ParCoolConfig.Client.Integers.VerticalOffsetOfStaminaHUD.get()
		);
		final int boxWidth = 91;
		final int boxHeight = 17;
		final Tuple<Integer, Integer> pos = position.calculate(boxWidth, boxHeight, width, height);

		float staminaScale = (float) stamina.get() / stamina.getActualMaxStamina();
		float statusScale = showStatus ? MathUtil.lerp(oldStatusValue, statusValue, partialTick) : 0f;

		if (staminaScale < 0) staminaScale = 0;
		if (staminaScale > 1) staminaScale = 1;

		RenderSystem.setShaderTexture(0, StaminaHUD.STAMINA);
		blit(stack, pos.getA(), pos.getB(), 0, 0, 93, 17, 128, 128);
		if (!stamina.isExhausted()) {
			blit(stack, pos.getA(), pos.getB(), 0, 102, (int) Math.ceil(92 * statusScale), 17, 128, 128);
			blit(stack, pos.getA(), pos.getB(), 0, 85, Math.round(16 + 69 * shadowScale) + 1, 12, 128, 128);
			blit(stack, pos.getA(), pos.getB(), 0, 17 * (renderGageType + 1), Math.round(16 + 69 * staminaScale) + 1, 12, 128, 128);
		} else {
			blit(stack, pos.getA(), pos.getB(), 0, 68, Math.round(16 + 69 * staminaScale) + 1, 17, 128, 128);
		}
		shadowScale = staminaScale - (staminaScale - shadowScale) / 1.1f;
	}
}
