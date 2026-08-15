package com.alrex.parcool.client.hud.stamina;

import com.alrex.parcool.api.client.gui.StaminaDisplayContext;
import com.alrex.parcool.common.Parkourability;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IStaminaHUD {
    void render(
            GuiGraphics graphics,
            Parkourability parkourability,
            StaminaDisplayContext currentContext, StaminaDisplayContext oldContext,
            DeltaTracker deltaTracker
    );

    default void tick(Player player, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
    }
}
