package com.alrex.parcool.api.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class RenderParCoolHUDEvent extends Event {
    public static class Render extends RenderParCoolHUDEvent {
        private final ForgeGui gui;
        private final GuiGraphics graphics;
        private final float partialTick;
        private final float screenWidth;
        private final float screenHeight;

        public Render(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
            this.gui = gui;
            this.graphics = graphics;
            this.partialTick = partialTick;
            this.screenWidth = width;
            this.screenHeight = height;
        }

        public ForgeGui getGui() {
            return gui;
        }

        public GuiGraphics getGraphics() {
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

        public static class Stamina extends Render {
            private final StaminaDisplayContext currentContext;
            private final StaminaDisplayContext oldContext;

            public Stamina(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
                super(gui, graphics, partialTick, width, height);
                this.currentContext = currentContext;
                this.oldContext = oldContext;
            }

            public StaminaDisplayContext getCurrentContext() {
                return currentContext;
            }

            public StaminaDisplayContext getOldContext() {
                return oldContext;
            }

            @Cancelable
            public static class Pre extends Stamina {
                public Pre(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
                    super(gui, graphics, partialTick, width, height, currentContext, oldContext);
                }
            }

            public static class Post extends Stamina {
                public Post(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
                    super(gui, graphics, partialTick, width, height, currentContext, oldContext);
                }
            }
        }
    }

    public static class Update extends RenderParCoolHUDEvent {
        public static class StaminaContext extends Update {
            private final StaminaDisplayContext currentContext;
            private final StaminaDisplayContext oldContext;

            public StaminaContext(StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
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
    }
}
