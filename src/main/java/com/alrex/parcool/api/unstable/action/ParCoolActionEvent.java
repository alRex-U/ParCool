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

    @Deprecated
    public static class TryToStartEvent extends ParCoolActionEvent implements ICancellableEvent {
        public TryToStartEvent(Player player, Action action) {
            super(player, action);
        }
    }

    @Deprecated
    public static class TryToContinueEvent extends ParCoolActionEvent implements ICancellableEvent {
        public TryToContinueEvent(Player player, Action action) {
            super(player, action);
        }
    }

    @Deprecated
    public static class StartEvent extends ParCoolActionEvent {
        public StartEvent(Player player, Action action) {
            super(player, action);
        }
    }

    @Deprecated
    public static class StopEvent extends ParCoolActionEvent {
        public StopEvent(Player player, Action action) {
            super(player, action);
        }
    }
    // ======

    public static class TryToStart extends ParCoolActionEvent implements ICancellableEvent {
        public TryToStart(Player player, Action action) {
            super(player, action);
        }
    }

    public static class TryToContinue extends ParCoolActionEvent implements ICancellableEvent {
        public TryToContinue(Player player, Action action) {
            super(player, action);
        }
    }

    public static class Start extends ParCoolActionEvent {
        private Start(Player player, Action action) {
            super(player, action);
        }

        public static class Pre extends Start {
            public Pre(Player player, Action action) {
                super(player, action);
            }
        }

        public static class Post extends Start {
            public Post(Player player, Action action) {
                super(player, action);
            }
        }
    }

    public static class Finish extends ParCoolActionEvent {
        private Finish(Player player, Action action) {
            super(player, action);
        }

        public static class Pre extends Finish {
            public Pre(Player player, Action action) {
                super(player, action);
            }
        }

        public static class Post extends Finish {
            public Post(Player player, Action action) {
                super(player, action);
            }
        }
    }

    public static class Tick extends ParCoolActionEvent {
        private Tick(Player player, Action action) {
            super(player, action);
        }

        public static class Pre extends Tick {
            public Pre(Player player, Action action) {
                super(player, action);
            }
        }

        public static class Post extends Tick {
            public Post(Player player, Action action) {
                super(player, action);
            }
        }
    }

}
