package com.alrex.parcool.common.architectury.event;

import com.alrex.parcool.common.stamina.StaminaTypeRegistry;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface RegisterParCoolStaminaArchEvent {
    Event<RegisterParCoolStaminaArchEvent> EVENT = EventFactory.createLoop();

    void onRegisterParCoolStamina(StaminaTypeRegistry registry);
}
