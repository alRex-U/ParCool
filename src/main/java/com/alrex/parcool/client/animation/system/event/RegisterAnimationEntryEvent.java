package com.alrex.parcool.client.animation.system.event;

import com.alrex.parcool.client.animation.system.registration.AnimationProgresses;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.registration.BlendingFactors;
import com.alrex.parcool.client.animation.system.registration.CodedAnimationComponents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

@OnlyIn(Dist.CLIENT)
public class RegisterAnimationEntryEvent extends Event implements IModBusEvent {
    public RegisterAnimationEntryEvent() {
    }

    public void finish() {
        CodedAnimationComponents.getInstance().freeze();
        BlendingFactors.getInstance().freeze();
        AnimationProgresses.getInstance().freeze();
        AnimationSets.getInstance().freeze();
    }
}
