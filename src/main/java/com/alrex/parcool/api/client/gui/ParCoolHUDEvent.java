package com.alrex.parcool.api.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class ParCoolHUDEvent extends Event {
    @Cancelable
    public static class RenderEvent extends ParCoolHUDEvent {
        private final ForgeGui gui;
        private final GuiGraphics graphics;
        private final float partialTick;
        private final float screenWidth;
        private final float screenHeight;

        public RenderEvent(ForgeGui e, GuiGraphics s, float partialTick, int width, int height) {
            this.gui = e;
            this.graphics = s;
            this.partialTick = partialTick;
            this.screenWidth = width;
            this.screenHeight = height;
        }

        public ForgeGui getGui() {
            return gui;
        }

        public GuiGraphics getGuiGraphics() {
            return graphics;
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
