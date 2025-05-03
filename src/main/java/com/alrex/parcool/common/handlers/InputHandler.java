package com.alrex.parcool.common.handlers;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.action.impl.RideZipline;
import com.alrex.parcool.common.action.impl.WallSlide;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.compatibility.ClientPlayerWrapper;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InputHandler {
    @SubscribeEvent
    public static void onInput(InputEvent.ClickInputEvent event) {
        ClientPlayerWrapper player = ClientPlayerWrapper.get();
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
                if (event.getKeyBinding().getKey().equals(KeyBindings.getKeyGrabWall().getKey())) {
                    event.setSwingHand(false);
                    event.setCanceled(true);
                    return;
                }
            }
            if (parkourability.get(RideZipline.class).isDoing()) {
                if (event.getKeyBinding().getKey().equals(KeyBindings.getKeyBindRideZipline().getKey())) {
                    event.setSwingHand(false);
                    event.setCanceled(true);
                    return;
                }
            }
            if (parkourability.get(WallSlide.class).isDoing()) {
                if (event.getKeyBinding().getKey().equals(KeyBindings.getKeyWallSlide().getKey())) {
                    event.setSwingHand(false);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }
}
