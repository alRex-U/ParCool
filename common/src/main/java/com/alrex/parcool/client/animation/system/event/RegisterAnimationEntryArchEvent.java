package com.alrex.parcool.client.animation.system.event;

import com.alrex.parcool.client.animation.system.registration.AnimationProgresses;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.registration.BlendingFactors;
import com.alrex.parcool.client.animation.system.registration.CodedAnimationComponents;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface RegisterAnimationEntryArchEvent {
    Event<RegisterAnimationEntryArchEvent> EVENT = EventFactory.createLoop();

    void onRegisterAnimationEntries();

    static void finish() {
        CodedAnimationComponents.getInstance().freeze();
        BlendingFactors.getInstance().freeze();
        AnimationProgresses.getInstance().freeze();
        AnimationSets.getInstance().freeze();
    }
}
