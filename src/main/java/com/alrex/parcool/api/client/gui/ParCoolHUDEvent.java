package com.alrex.parcool.api.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class ParCoolHUDEvent extends Event {
    public static class Render extends ParCoolHUDEvent {
        private final ForgeGui gui;
        private final PoseStack posestack;
        private final float partialTick;
        private final float screenWidth;
        private final float screenHeight;

        public Render(ForgeGui gui, PoseStack stack, float partialTick, int width, int height) {
            this.gui = gui;
            this.posestack = stack;
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

        public static class Stamina extends Render {
            private final StaminaDisplayContext currentContext;
            private final StaminaDisplayContext oldContext;

            public Stamina(ForgeGui gui, PoseStack stack, float partialTick, int width, int height, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
                super(gui, stack, partialTick, width, height);
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
                public Pre(ForgeGui gui, PoseStack stack, float partialTick, int width, int height, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
                    super(gui, stack, partialTick, width, height, currentContext, oldContext);
                }

                @Override
                public boolean isCancelable() {
                    return true;
                }
            }

            public static class Post extends Stamina {
                public Post(ForgeGui gui, PoseStack stack, float partialTick, int width, int height, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
                    super(gui, stack, partialTick, width, height, currentContext, oldContext);
                }
            }
        }
    }

    public static class Update extends ParCoolHUDEvent {
        public static class StaminaContext extends Update {
            private StaminaDisplayContext currentContext;
            private final StaminaDisplayContext oldContext;

            public StaminaContext(StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
                this.currentContext = currentContext;
                this.oldContext = oldContext;
            }

            public void setCurrentContext(StaminaDisplayContext currentContext) {
                this.currentContext = currentContext;
            }

            public StaminaDisplayContext getCurrentContext() {
                return currentContext;
            }

            public StaminaDisplayContext getOldContext() {
                return oldContext;
            }
        }

        public static class StatusIndicatorValue extends Update {
            private float statusValue = -1f;

            public void setStatusValue(float value) {
                statusValue = value;
            }

            public float getStatusValue() {
                return Mth.clamp(statusValue, 0f, 1f);
            }

            public boolean shouldShowIndicator() {
                return statusValue >= 0f;
            }
        }
    }
}
