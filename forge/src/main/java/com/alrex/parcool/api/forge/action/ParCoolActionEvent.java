package com.alrex.parcool.api.forge.action;

import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ContinuableAction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

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

    @Cancelable
    public static class TryToStart extends ParCoolActionEvent {
        @Override
        public boolean isCancelable() {
            return true;
        }

        public TryToStart(Player player, Action action) {
            super(player, action);
        }
    }

    @Cancelable
    public static class TryToContinue extends ParCoolActionEvent {

        @Override
        public boolean isCancelable() {
            return true;
        }

        public TryToContinue(Player player, ContinuableAction action) {
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
        private Finish(Player player, ContinuableAction action) {
            super(player, action);
        }

        public static class Pre extends Finish {
            public Pre(Player player, ContinuableAction action) {
                super(player, action);
            }
        }

        public static class Post extends Finish {
            public Post(Player player, ContinuableAction action) {
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
