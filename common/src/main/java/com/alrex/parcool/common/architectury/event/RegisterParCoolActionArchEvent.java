package com.alrex.parcool.common.architectury.event;

import com.alrex.parcool.common.action.ActionRegistry;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface RegisterParCoolActionArchEvent {
    Event<RegisterParCoolActionArchEvent> EVENT = EventFactory.createLoop();

    void onRegisterParCoolAction(ActionRegistry registry);
}
