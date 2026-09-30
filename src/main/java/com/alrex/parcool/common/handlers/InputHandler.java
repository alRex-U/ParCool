package com.alrex.parcool.common.handlers;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.action.impl.RideZipline;
import com.alrex.parcool.common.action.impl.WallSlide;
import com.alrex.parcool.common.attachment.common.Parkourability;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.InputEvent;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class InputHandler {
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
                if (KeyBindingHelper.getBoundKeyOf(event.getKeyMapping())
                        .equals(KeyBindingHelper.getBoundKeyOf(KeyBindings.getKeyGrabWall()))) {
                    event.setSwingHand(false);
                    event.setCanceled(true);
                    return;
                }
            }
            if (parkourability.get(RideZipline.class).isDoing()) {
                if (KeyBindingHelper.getBoundKeyOf(event.getKeyMapping())
                        .equals(KeyBindingHelper.getBoundKeyOf(KeyBindings.getKeyRideZipline()))) {
                    event.setSwingHand(false);
                    event.setCanceled(true);
                    return;
                }
            }
            if (parkourability.get(WallSlide.class).isDoing()) {
                if (KeyBindingHelper.getBoundKeyOf(event.getKeyMapping())
                        .equals(KeyBindingHelper.getBoundKeyOf(KeyBindings.getKeyWallSlide()))) {
                    event.setSwingHand(false);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }
}
