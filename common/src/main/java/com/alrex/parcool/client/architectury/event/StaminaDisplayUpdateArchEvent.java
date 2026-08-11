package com.alrex.parcool.client.architectury.event;

import com.alrex.parcool.api.client.gui.StaminaDisplayContext;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface StaminaDisplayUpdateArchEvent {
    Event<StaminaDisplayUpdateArchEvent> EVENT = EventFactory.createLoop();

    void onUpdateStaminaDisplay(StaminaDisplayContext currentContext, StaminaDisplayContext oldContext);
}
