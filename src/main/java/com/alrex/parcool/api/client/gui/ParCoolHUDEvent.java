package com.alrex.parcool.api.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class ParCoolHUDEvent extends Event {
    @Cancelable
    public static class RenderEvent extends ParCoolHUDEvent {
        private final ForgeIngameGui ingameGui;
        private final PoseStack posestack;
        private final float partialTick;
        private final float screenWidth;
        private final float screenHeight;

        public RenderEvent(ForgeIngameGui e, PoseStack s, float partialTick, int width, int height) {
            this.ingameGui = e;
            this.posestack = s;
            this.partialTick = partialTick;
            this.screenWidth = width;
            this.screenHeight = height;
        }

        public ForgeIngameGui getInGameGui() {
            return ingameGui;
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
