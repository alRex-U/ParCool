package com.alrex.parcool.api.unstable.action;

import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.common.action.Action;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class ParCoolActionEvent extends Event {
    private final PlayerWrapper player;
    private final Action action;

    public PlayerWrapper getPlayer() {
        return player;
    }

    public Action getAction() {
        return action;
    }

    public ParCoolActionEvent(PlayerWrapper player, Action action) {
        this.player = player;
        this.action = action;
    }

    @Cancelable
    public static class TryToStartEvent extends ParCoolActionEvent {

        @Override
        public boolean isCancelable() {
            return true;
        }

        public TryToStartEvent(PlayerWrapper player, Action action) {
            super(player, action);
        }
    }

    @Cancelable
    public static class TryToContinueEvent extends ParCoolActionEvent {

        @Override
        public boolean isCancelable() {
            return true;
        }

        public TryToContinueEvent(PlayerWrapper player, Action action) {
            super(player, action);
        }
    }

    public static class StartEvent extends ParCoolActionEvent {
        public StartEvent(PlayerWrapper player, Action action) {
            super(player, action);
        }
    }

    public static class StopEvent extends ParCoolActionEvent {
        public StopEvent(PlayerWrapper player, Action action) {
            super(player, action);
        }
    }
}
