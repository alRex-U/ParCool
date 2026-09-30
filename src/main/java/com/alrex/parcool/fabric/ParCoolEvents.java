package com.alrex.parcool.fabric;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Шина только для публичных событий самого ParCool (ParCoolActionEvent, ParCoolHUDEvent,
 * ParCoolAnimationInfoEvent). Слушателей регистрирует чужой код через ParCoolEvents.register.
 *
 * <p>ponytail: подписка без фильтра по типу -- слушатель сам проверяет instanceof. Разложить по
 * типам, если в паке появится мод, который вешает на эти события что-то горячее.
 */
public final class ParCoolEvents {
    private static final List<Consumer<Event>> LISTENERS = new CopyOnWriteArrayList<>();

    private ParCoolEvents() {}

    public static void register(Consumer<Event> listener) {
        LISTENERS.add(listener);
    }

    public static <T extends Event> T post(T event) {
        for (Consumer<Event> listener : LISTENERS) {
            listener.accept(event);
        }
        return event;
    }
}
