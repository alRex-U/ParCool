package com.alrex.parcool.api.stamina;

import com.alrex.parcool.common.stamina.StaminaTypeRegistry;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class RegisterParCoolStaminaTypeEvent extends Event implements IModBusEvent {
    private final StaminaTypeRegistry registry;

    public RegisterParCoolStaminaTypeEvent(StaminaTypeRegistry registry) {
        this.registry = registry;
    }

    public void register(StaminaTypeEntry<?> entry) {
        registry.register(entry);
    }
}
