package com.alrex.parcool.forge.common.handlers;

import com.alrex.parcool.common.Parkourability;
import net.minecraftforge.client.event.MovementInputUpdateEvent;

public class InputHandler {
    public static void onPlayerInputUpdated(MovementInputUpdateEvent event) {
        if (!event.getEntity().isLocalPlayer()) return;

        var parkourability = Parkourability.get(event.getEntity());
        if (parkourability == null) return;

        if (parkourability.getBehaviorEnforcer().enforceNoSneak()) {
            event.getInput().shiftKeyDown = false;
        }
    }
}
