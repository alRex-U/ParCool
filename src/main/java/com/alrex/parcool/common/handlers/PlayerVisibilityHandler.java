package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.attachment.common.Parkourability;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class PlayerVisibilityHandler {
    public static double modifyVisibility(Entity entity, double visibility) {
        if (entity instanceof Player player) {
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) return visibility;
            if (parkourability.get(HideInBlock.class).isDoing()) {
                return visibility * 0.1;
            }
        }
        return visibility;
    }
}
