package com.alrex.parcool.common.handlers;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.action.impl.RideZipline;
import com.alrex.parcool.common.action.impl.WallSlide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;

public class InputHandler {
    @SubscribeEvent
    public static void onInput(InputEvent.InteractionKeyMappingTriggered event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        if (parkourability.get(HideInBlock.class).isDoing()) {
            event.setSwingHand(false);
            event.setCanceled(true);
            return;
        }
        if (event.isUseItem()) {
            if (parkourability.get(ClingToCliff.class).isDoing()) {
                if (event.getKeyMapping().getKey().equals(KeyBindings.getKeyGrabWall().getKey())) {
                    event.setSwingHand(false);
                    event.setCanceled(true);
                    return;
                }
            }
            if (parkourability.get(RideZipline.class).isDoing()) {
                if (event.getKeyMapping().getKey().equals(KeyBindings.getKeyRideZipline().getKey())) {
                    event.setSwingHand(false);
                    event.setCanceled(true);
                    return;
                }
            }
            if (parkourability.get(WallSlide.class).isDoing()) {
                if (event.getKeyMapping().getKey().equals(KeyBindings.getKeyWallSlide().getKey())) {
                    event.setSwingHand(false);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }
}
