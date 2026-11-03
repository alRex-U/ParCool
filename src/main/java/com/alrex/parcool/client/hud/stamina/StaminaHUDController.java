package com.alrex.parcool.client.hud.stamina;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.client.gui.ParCoolHUDEvent;
import com.alrex.parcool.api.client.gui.StaminaDisplayContext;
import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.AbstractClientPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class StaminaHUDController implements LayeredDraw.Layer {
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

    public void onTick(ClientTickEvent event) {
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
		NeoForge.EVENT_BUS.post(staminaUpdateEvent);
		currentContext = staminaUpdateEvent.getCurrentContext();

		if (ParCool.getConfig().client().showTargetIndicator.get()) {
			var indicatorUpdateEvent = new ParCoolHUDEvent.Update.StatusIndicatorValue();
			for (var provider : parkourability.getActions().getExtensionListeners(ActionExtension.IndicatorStatusProvider.class)) {
				if (provider.requestStatusIndicator() && provider instanceof Action action && parkourability.permit(action.getEntry()))
					indicatorUpdateEvent.setStatusValue(provider.getStatusIndicatorValue());
			}
			NeoForge.EVENT_BUS.post(indicatorUpdateEvent);
			showStatus = indicatorUpdateEvent.shouldShowIndicator();
			statusValue = (indicatorUpdateEvent.getStatusValue() + oldStatusValue) / 2f;
		} else {
			showStatus = false;
			statusValue = 0;
		}
	}

	@Override
    public void render(@Nonnull GuiGraphics guiGraphics, @Nonnull DeltaTracker deltaTracker) {
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

		if (NeoForge.EVENT_BUS.post(new ParCoolHUDEvent.Render.Stamina.Pre(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true), currentContext, oldContext)).isCanceled())
			return;

		if (config.type().get() == HUDType.Light) {
			lightStaminaHUD.render(
					guiGraphics,
					parkourability,
					currentContext,
					oldContext,
					showStatus,
					statusValue,
					oldStatusValue,
					deltaTracker
			);
		}

		NeoForge.EVENT_BUS.post(new ParCoolHUDEvent.Render.Stamina.Post(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true), currentContext, oldContext));
	}
}
