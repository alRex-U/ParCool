package com.alrex.parcool.client.hud.stamina;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.client.gui.RenderParCoolHUDEvent;
import com.alrex.parcool.api.client.gui.StaminaDisplayContext;
import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.alrex.parcool.common.Parkourability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
	private int tickValueNotChange;

	public StaminaHUDController() {
		lightStaminaHUD = new LightStaminaHUD();
	}

	public void onTick(TickEvent.ClientTickEvent event) {
		var player = Minecraft.getInstance().player;
		if (player == null) return;
		oldContext = currentContext;
		if (Parkourability.get(player).getStamina() instanceof AbstractLocalStamina localStamina) {
			currentContext = oldContext.next(localStamina);
		}
		lightStaminaHUD.tick(player, currentContext, oldContext);
		if (currentContext.equals(oldContext)) {
			tickValueNotChange++;
		} else {
			tickValueNotChange = 0;
		}

		MinecraftForge.EVENT_BUS.post(new RenderParCoolHUDEvent.Update.StaminaContext(currentContext, oldContext));
	}

	@Override
    public void render(ForgeGui forgeGui, GuiGraphics guiGraphics, float partialTick, int width, int height) {
		AbstractClientPlayer player = Minecraft.getInstance().player;
		if (player == null) return;
		Parkourability parkourability = Parkourability.get(player);

		if (tickValueNotChange > 40 && !ParCool.getConfig().client().staminaHud.showAlways().get()) {
			return;
		}
		if (parkourability.getStamina() instanceof AbstractLocalStamina localStamina && !localStamina.showHud()) {
			return;
		}

        if (MinecraftForge.EVENT_BUS.post(new RenderParCoolHUDEvent.Render.Stamina.Pre(forgeGui, guiGraphics, partialTick, width, height, currentContext, oldContext)))
			return;

		if (ParCool.getConfig().client().staminaHud.type().get() == HUDType.Light) {
            lightStaminaHUD.render(forgeGui, guiGraphics, parkourability, currentContext, oldContext, partialTick, width, height);
		}

        MinecraftForge.EVENT_BUS.post(new RenderParCoolHUDEvent.Render.Stamina.Post(forgeGui, guiGraphics, partialTick, width, height, currentContext, oldContext));
	}
}
