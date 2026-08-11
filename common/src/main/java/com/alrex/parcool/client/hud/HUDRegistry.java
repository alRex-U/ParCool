package com.alrex.parcool.client.hud;

import com.alrex.parcool.client.hud.stamina.StaminaHUDController;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class HUDRegistry {
    private static HUDRegistry instance = null;

    private final StaminaHUDController staminaHUD = new StaminaHUDController();


    public static HUDRegistry getInstance() {
        if (instance == null) instance = new HUDRegistry();
        return instance;
    }

    public void renderHud(PoseStack stack, float partial) {
        var mc = Minecraft.getInstance();
        staminaHUD.render(mc.gui, stack, partial, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
    }

    public void onTick() {
        staminaHUD.onTick();
    }
}
