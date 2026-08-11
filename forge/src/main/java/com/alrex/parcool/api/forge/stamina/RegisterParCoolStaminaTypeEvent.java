package com.alrex.parcool.api.forge.stamina;

import com.alrex.parcool.api.stamina.StaminaTypeEntry;
import com.alrex.parcool.common.stamina.StaminaTypeRegistry;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class RegisterParCoolStaminaTypeEvent extends Event implements IModBusEvent {
    private final StaminaTypeRegistry registry;

    public RegisterParCoolStaminaTypeEvent(StaminaTypeRegistry registry) {
        this.registry = registry;
    }

    public void register(StaminaTypeEntry<?> entry) {
        registry.register(entry);
    }
}
