package com.alrex.parcool.client.animation.system.event;

import com.alrex.parcool.client.animation.system.registration.AnimationProgresses;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.registration.BlendingFactors;
import com.alrex.parcool.client.animation.system.registration.CodedAnimationComponents;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

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
