package com.alrex.parcool.forge.common.handlers;

import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerEventHandler {
    public static void onAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        var parkourability = Parkourability.get(player);
        parkourability.getAdditionalProperties().onJump();
        for (var listener : parkourability.getActions().getExtensionListeners(ActionExtension.AttackedListener.class)) {
            listener.onAttacked(event);
        }
    }

    public static void onFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        var parkourability = Parkourability.get(player);
        parkourability.getAdditionalProperties().onJump();
        for (var listener : parkourability.getActions().getExtensionListeners(ActionExtension.LandListener.class)) {
            listener.onLand(event);
        }
    }

    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        var parkourability = Parkourability.get(player);
        parkourability.getAdditionalProperties().onJump();
        for (var listener : parkourability.getActions().getExtensionListeners(ActionExtension.JumpListener.class)) {
            listener.onJump();
        }
    }

    public static void onLivingVisibilityEvent(LivingEvent.LivingVisibilityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        var parkourability = Parkourability.get(player);
        parkourability.getAdditionalProperties().onJump();
        for (var listener : parkourability.getActions().getExtensionListeners(ActionExtension.VisibilityListener.class)) {
            listener.onUpdateVisibility(event);
        }
    }

    public static void onSizeRefresh(EntityEvent.Size sizeEvent) {
        if (!(sizeEvent.getEntity() instanceof Player player)) return;
        var parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        var enforceValue = parkourability.getBehaviorEnforcer().getEnforcedEyeHeight();
        if (enforceValue != null) {
            sizeEvent.setNewEyeHeight(enforceValue);
        }
    }

    public static void onClone(PlayerEvent.Clone event) {
        Player player = event.getEntity();
        if (event.isWasDeath() && player instanceof ServerPlayer) {
            Player from = event.getOriginal();
            Parkourability pFrom = Parkourability.get(from);
            Parkourability pTo = Parkourability.get(player);
            if (pFrom != null && pTo != null) {
                pTo.copyFrom(pFrom);
            }
        }
    }
}
