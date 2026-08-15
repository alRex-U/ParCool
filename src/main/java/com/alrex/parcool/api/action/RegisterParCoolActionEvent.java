package com.alrex.parcool.api.action;

import com.alrex.parcool.common.action.ActionRegistry;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class RegisterParCoolActionEvent extends Event implements IModBusEvent {
    private final ActionRegistry registry;

    public RegisterParCoolActionEvent(ActionRegistry registry) {
        this.registry = registry;
    }

    public void register(ActionGroup group) {
        registry.register(group);
    }
}
