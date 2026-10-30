package com.alrex.parcool.client.hud.stamina;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.client.gui.ParCoolHUDEvent;
import com.alrex.parcool.api.client.gui.StaminaDisplayContext;
import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class StaminaHUDController implements IGuiOverlay {
	LightStaminaHUD lightStaminaHUD;

	private StaminaDisplayContext currentContext = StaminaDisplayContext.DEFAULT;
	private StaminaDisplayContext oldContext = StaminaDisplayContext.DEFAULT;
	private boolean showStatus = false;
	private float statusValue = 0f;
	private float oldStatusValue = 0f;
	private int tickValueNotChange;

	public StaminaHUDController() {
		lightStaminaHUD = new LightStaminaHUD();
	}

	public void onTick(TickEvent.ClientTickEvent event) {
		var player = Minecraft.getInstance().player;
		if (player == null) return;
		oldContext = currentContext;
		oldStatusValue = statusValue;
		var parkourability = Parkourability.get(player);
		if (parkourability.getStamina() instanceof AbstractLocalStamina localStamina) {
			currentContext = oldContext.next(localStamina);
		}
		lightStaminaHUD.tick(player, currentContext, oldContext);
		if (currentContext.equals(oldContext)) {
			tickValueNotChange++;
		} else {
			tickValueNotChange = 0;
		}

		var staminaUpdateEvent = new ParCoolHUDEvent.Update.StaminaContext(currentContext, oldContext);
		MinecraftForge.EVENT_BUS.post(staminaUpdateEvent);
		currentContext = staminaUpdateEvent.getCurrentContext();

		if (ParCool.getConfig().client().showTargetIndicator.get()) {
			var indicatorUpdateEvent = new ParCoolHUDEvent.Update.StatusIndicatorValue();
			for (var provider : parkourability.getActions().getExtensionListeners(ActionExtension.IndicatorStatusProvider.class)) {
				if (provider.requestStatusIndicator())
					indicatorUpdateEvent.setStatusValue(provider.getStatusIndicatorValue());
			}
			MinecraftForge.EVENT_BUS.post(indicatorUpdateEvent);
			showStatus = indicatorUpdateEvent.shouldShowIndicator();
			statusValue = indicatorUpdateEvent.getStatusValue();
		} else {
			showStatus = false;
			statusValue = 0;
		}
	}

	@Override
	public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height) {
		AbstractClientPlayer player = Minecraft.getInstance().player;
		if (player == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (!(parkourability.getStamina() instanceof AbstractLocalStamina localStamina)) return;

		var config = ParCool.getConfig().client().staminaHud;
		if (!config.showAlways().get()) {
			if (!showStatus) {
				if (config.hideAutomatically().get() && tickValueNotChange > 40) {
					return;
				}
				if (localStamina.isInfinite()) return;
			}
		}
		if (!localStamina.showHud()) return;

		if (MinecraftForge.EVENT_BUS.post(new ParCoolHUDEvent.Render.Stamina.Pre(gui, poseStack, partialTick, width, height, currentContext, oldContext)))
			return;

		if (config.type().get() == HUDType.Light) {
			lightStaminaHUD.render(
					gui,
					poseStack,
					parkourability,
					currentContext,
					oldContext,
					showStatus,
					statusValue,
					oldStatusValue,
					partialTick,
					width,
					height
			);
		}

		MinecraftForge.EVENT_BUS.post(new ParCoolHUDEvent.Render.Stamina.Post(gui, poseStack, partialTick, width, height, currentContext, oldContext));
	}
}
