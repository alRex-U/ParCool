package com.alrex.parcool.common.architectury.event;

import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ContinuableAction;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.world.entity.player.Player;

public class ParCoolActionArchEvent {
    public interface TryToStart {
        Event<TryToStart> EVENT = EventFactory.createEventResult();

        EventResult onTryToStart(Player player, Action action);
    }

    public interface TryToContinue {
        Event<TryToContinue> EVENT = EventFactory.createEventResult();

        EventResult onTryToContinue(Player player, ContinuableAction action);
    }

    public interface Start {
        interface Pre {
            Event<Pre> EVENT = EventFactory.createLoop();

            void onPre(Player player, Action action);
        }

        interface Post {
            Event<Post> EVENT = EventFactory.createLoop();

            void onPost(Player player, Action action);
        }
    }

    public interface Finish {
        interface Pre {
            Event<Pre> EVENT = EventFactory.createLoop();

            void onPre(Player player, ContinuableAction action);
        }

        interface Post {
            Event<Post> EVENT = EventFactory.createLoop();

            void onPost(Player player, ContinuableAction action);
        }
    }

    public interface Tick {
        interface Pre {
            Event<Pre> EVENT = EventFactory.createLoop();

            void onPre(Player player, Action action);
        }

        interface Post {
            Event<Post> EVENT = EventFactory.createLoop();

            void onPost(Player player, Action action);
        }
    }
}
