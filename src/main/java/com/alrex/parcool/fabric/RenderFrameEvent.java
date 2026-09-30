package com.alrex.parcool.fabric;

import net.minecraft.client.DeltaTracker;

/**
 * Аналог net.neoforged.neoforge.client.event.RenderFrameEvent: Fabric API кадрового события не
 * даёт, а Porting Lib его не портировал. Шлёт MinecraftRenderFrameMixin вокруг gameRenderer.render,
 * ровно там же, где его шлёт NeoForge.
 */
public abstract class RenderFrameEvent {
    private final DeltaTracker partialTick;

    protected RenderFrameEvent(DeltaTracker partialTick) {
        this.partialTick = partialTick;
    }

    public DeltaTracker getPartialTick() {
        return partialTick;
    }

    public static class Pre extends RenderFrameEvent {
        public Pre(DeltaTracker partialTick) {
            super(partialTick);
        }
    }

    public static class Post extends RenderFrameEvent {
        public Post(DeltaTracker partialTick) {
            super(partialTick);
        }
    }
}
