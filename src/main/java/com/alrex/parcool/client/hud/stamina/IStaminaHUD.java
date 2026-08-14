package com.alrex.parcool.client.hud.stamina;

import com.alrex.parcool.api.client.gui.StaminaDisplayContext;
import com.alrex.parcool.common.Parkourability;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;

@OnlyIn(Dist.CLIENT)
public interface IStaminaHUD {
    void render(
            ForgeGui gui,
            GuiGraphics graphics,
            Parkourability parkourability,
            StaminaDisplayContext currentContext, StaminaDisplayContext oldContext,
            float partialTick, int width, int height
    );

    default void tick(Player player, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
    }
}
