package com.alrex.parcool.api.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class ParCoolHUDEvent extends Event {
    @Cancelable
    public static class RenderEvent extends ParCoolHUDEvent {
        private final ForgeGui gui;
        private final PoseStack posestack;
        private final float partialTick;
        private final float screenWidth;
        private final float screenHeight;

        public RenderEvent(ForgeGui e, PoseStack s, float partialTick, int width, int height) {
            this.gui = e;
            this.posestack = s;
            this.partialTick = partialTick;
            this.screenWidth = width;
            this.screenHeight = height;
        }

        public ForgeGui getGui() {
            return gui;
        }

        public PoseStack getPosestack() {
            return posestack;
        }

        public float getPartialTick() {
            return partialTick;
        }

        public float getScreenWidth() {
            return screenWidth;
        }

        public float getScreenHeight() {
            return screenHeight;
        }
    }
}
