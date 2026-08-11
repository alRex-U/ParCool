package com.alrex.parcool.api.forge.action;

import com.alrex.parcool.api.action.ActionGroup;
import com.alrex.parcool.common.action.ActionRegistry;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class RegisterParCoolActionEvent extends Event implements IModBusEvent {
    private final ActionRegistry registry;

    public RegisterParCoolActionEvent(ActionRegistry registry) {
        this.registry = registry;
    }

    public void register(ActionGroup group) {
        registry.register(group);
    }
}
