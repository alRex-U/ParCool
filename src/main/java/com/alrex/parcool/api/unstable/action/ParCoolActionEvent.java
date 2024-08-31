package com.alrex.parcool.api.unstable.action;

import com.alrex.parcool.common.action.Action;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class ParCoolActionEvent extends Event {
    private final PlayerEntity player;
    private final Action action;

    public PlayerEntity getPlayer() {
        return player;
    }

    public Action getAction() {
        return action;
    }

    public ParCoolActionEvent(PlayerEntity player, Action action) {
        this.player = player;
        this.action = action;
    }

    @Cancelable
    public static class TryToStartEvent extends ParCoolActionEvent {

        @Override
        public boolean isCancelable() {
            return true;
        }

        public TryToStartEvent(PlayerEntity player, Action action) {
            super(player, action);
        }
    }

    public static class StartEvent extends ParCoolActionEvent {
        public StartEvent(PlayerEntity player, Action action) {
            super(player, action);
        }
    }

    public static class StopEvent extends ParCoolActionEvent {
        public StopEvent(PlayerEntity player, Action action) {
            super(player, action);
        }
    }
}
