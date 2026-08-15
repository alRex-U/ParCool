package com.alrex.parcool.api.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class RenderParCoolHUDEvent extends Event {
    public static class Render extends RenderParCoolHUDEvent {
        private final GuiGraphics graphics;
        private final float partialTick;

        public Render(GuiGraphics graphics, float partialTick) {
            this.graphics = graphics;
            this.partialTick = partialTick;
        }

        public GuiGraphics getGraphics() {
            return graphics;
        }

        public float getPartialTick() {
            return partialTick;
        }

        public static class Stamina extends Render {
            private final StaminaDisplayContext currentContext;
            private final StaminaDisplayContext oldContext;

            public Stamina(GuiGraphics graphics, float partialTick, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
                super(graphics, partialTick);
                this.currentContext = currentContext;
                this.oldContext = oldContext;
            }

            public StaminaDisplayContext getCurrentContext() {
                return currentContext;
            }

            public StaminaDisplayContext getOldContext() {
                return oldContext;
            }

            public static class Pre extends Stamina implements ICancellableEvent {
                public Pre(GuiGraphics graphics, float partialTick, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
                    super(graphics, partialTick, currentContext, oldContext);
                }
            }

            public static class Post extends Stamina {
                public Post(GuiGraphics graphics, float partialTick, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
                    super(graphics, partialTick, currentContext, oldContext);
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
