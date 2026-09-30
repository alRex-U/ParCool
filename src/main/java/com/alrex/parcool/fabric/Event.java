package com.alrex.parcool.fabric;

/**
 * Аналог net.neoforged.bus.api.Event для собственных событий ParCool (api.unstable,
 * api.client.gui).
 */
public abstract class Event {
    private boolean canceled = false;

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
