package com.alrex.parcool.client.architectury.event;

import com.alrex.parcool.api.client.gui.StaminaDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.client.gui.Gui;

public interface RenderParCoolHUDArchEvent {
    interface Stamina {
        interface Pre extends Stamina {
            Event<Pre> EVENT = EventFactory.createEventResult();

            EventResult onRenderPre(Gui gui, PoseStack stack, float partialTick, int width, int height, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext);
        }

        interface Post extends Stamina {
            Event<Post> EVENT = EventFactory.createLoop();

            void onRenderPost(Gui gui, PoseStack stack, float partialTick, int width, int height, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext);
        }
    }
}
