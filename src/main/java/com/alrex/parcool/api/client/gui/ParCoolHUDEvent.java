package com.alrex.parcool.api.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class ParCoolHUDEvent extends Event {
    public static class RenderEvent extends ParCoolHUDEvent implements ICancellableEvent {
        private final GuiGraphics graphics;
        private final DeltaTracker partialTick;

        public RenderEvent(GuiGraphics s, DeltaTracker partialTick) {
            this.graphics = s;
            this.partialTick = partialTick;
        }

        public GuiGraphics getGuiGraphics() {
            return graphics;
        }

        public DeltaTracker getDeltaTracker() {
            return partialTick;
        }

    }
}
