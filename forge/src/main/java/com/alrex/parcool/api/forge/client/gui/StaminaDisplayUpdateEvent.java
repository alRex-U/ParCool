package com.alrex.parcool.api.forge.client.gui;

import com.alrex.parcool.api.client.gui.StaminaDisplayContext;

public class StaminaDisplayUpdateEvent {
    private final StaminaDisplayContext currentContext;
    private final StaminaDisplayContext oldContext;

    public StaminaDisplayUpdateEvent(StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
        this.currentContext = currentContext;
        this.oldContext = oldContext;
    }

    public StaminaDisplayContext getCurrentContext() {
        return currentContext;
    }

    public StaminaDisplayContext getOldContext() {
        return oldContext;
    }
}
