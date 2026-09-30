package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.attachment.common.Parkourability;

import net.minecraft.world.entity.player.Player;

public class PlayerJumpHandler {
    public static void onJump(Player player) {
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        parkourability.getAdditionalProperties().onJump();
        if (!player.isLocalPlayer()) return;
        parkourability.get(Dive.class).onJump(player, parkourability);
        parkourability.get(Flipping.class).onJump(player, parkourability);
        parkourability.get(ChargeJump.class).onJump(player, parkourability);
    }
}
