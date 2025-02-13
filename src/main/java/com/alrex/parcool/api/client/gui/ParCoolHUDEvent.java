package com.alrex.parcool.api.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class ParCoolHUDEvent extends Event {
    @Cancelable
    public static class RenderEvent extends ParCoolHUDEvent {
        private final RenderGameOverlayEvent internalEvent;
        private final MatrixStack stack;

        public RenderEvent(RenderGameOverlayEvent e, MatrixStack s) {
            this.internalEvent = e;
            this.stack = s;
        }

        public RenderGameOverlayEvent getInternalInformation() {
            return internalEvent;
        }

        public MatrixStack getMatrixStack() {
            return stack;
        }
    }
}
