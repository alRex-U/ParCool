package com.alrex.parcool.client.hud.stamina;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.client.gui.StaminaDisplayContext;
import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.alrex.parcool.client.architectury.event.RenderParCoolHUDArchEvent;
import com.alrex.parcool.client.architectury.event.StaminaDisplayUpdateArchEvent;
import com.alrex.parcool.common.Parkourability;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.AbstractClientPlayer;

@Environment(EnvType.CLIENT)
public class StaminaHUDController {
    LightStaminaHUD lightStaminaHUD;

    private StaminaDisplayContext currentContext = StaminaDisplayContext.DEFAULT;
    private StaminaDisplayContext oldContext = StaminaDisplayContext.DEFAULT;
    private int tickValueNotChange;

    public StaminaHUDController() {
        lightStaminaHUD = new LightStaminaHUD();
    }

    public void onTick() {
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

        StaminaDisplayUpdateArchEvent.EVENT.invoker().onUpdateStaminaDisplay(currentContext, oldContext);
    }

    public void render(Gui gui, PoseStack poseStack, float partialTick, int width, int height) {
        AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        Parkourability parkourability = Parkourability.get(player);

        if (tickValueNotChange > 40 && !ParCool.getConfig().client().staminaHud().showAlways().get()) {
            return;
        }
        if (parkourability.getStamina() instanceof AbstractLocalStamina localStamina && !localStamina.showHud()) {
            return;
        }

        if (RenderParCoolHUDArchEvent.Stamina.Pre.EVENT.invoker().onRenderPre(gui, poseStack, partialTick, width, height, currentContext, oldContext).isFalse())
            return;

        if (ParCool.getConfig().client().staminaHud().type().get() == HUDType.Light) {
            lightStaminaHUD.render(gui, poseStack, parkourability, currentContext, oldContext, partialTick, width, height);
        }

        RenderParCoolHUDArchEvent.Stamina.Post.EVENT.invoker().onRenderPost(gui, poseStack, partialTick, width, height, currentContext, oldContext);
    }
}
