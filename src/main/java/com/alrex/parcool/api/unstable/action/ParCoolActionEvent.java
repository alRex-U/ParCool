package com.alrex.parcool.api.unstable.action;

import com.alrex.parcool.common.action.Action;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class ParCoolActionEvent extends Event {
    private final Player player;
    private final Action action;

    public Player getPlayer() {
        return player;
    }

    public Action getAction() {
        return action;
    }

    public ParCoolActionEvent(Player player, Action action) {
        this.player = player;
        this.action = action;
    }

    public static class TryToStartEvent extends ParCoolActionEvent implements ICancellableEvent {

        public TryToStartEvent(Player player, Action action) {
            super(player, action);
        }
    }

    public static class TryToContinueEvent extends ParCoolActionEvent implements ICancellableEvent {

        public TryToContinueEvent(Player player, Action action) {
            super(player, action);
        }
    }

    public static class StartEvent extends ParCoolActionEvent {
        public StartEvent(Player player, Action action) {
            super(player, action);
        }
    }

    public static class StopEvent extends ParCoolActionEvent {
        public StopEvent(Player player, Action action) {
            super(player, action);
        }
    }
}
