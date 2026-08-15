package com.alrex.parcool.api;

import com.alrex.parcool.ParCool;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ParCoolSoundEvents {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, ParCool.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> BREAKFALL = register("action.breakfall");
    public static final DeferredHolder<SoundEvent, SoundEvent> DODGE = register("action.dodge");
    public static final DeferredHolder<SoundEvent, SoundEvent> VAULT = register("action.vault");
    public static final DeferredHolder<SoundEvent, SoundEvent> HANG_ON = register("action.hang_on");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLIMB_UP = register("action.climb_up");
    public static final DeferredHolder<SoundEvent, SoundEvent> WALL_RUN = register("action.wall_run");
    public static final DeferredHolder<SoundEvent, SoundEvent> WALL_JUMP = register("action.wall_jump");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHARGE_JUMP = register("action.charge_jump");
    public static final DeferredHolder<SoundEvent, SoundEvent> HANG_DOWN = register("action.hang_down");
    public static final DeferredHolder<SoundEvent, SoundEvent> POLE_CLIMB = register("action.pole_climb");
    public static final DeferredHolder<SoundEvent, SoundEvent> SLIDE = register("action.slide");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORIZONTAL_WALL_RUN = register("action.h_wall_run");
    public static final DeferredHolder<SoundEvent, SoundEvent> SLIDE_DOWN = register("action.slide_down");
    public static final DeferredHolder<SoundEvent, SoundEvent> DIVE = register("action.dive");
    public static final DeferredHolder<SoundEvent, SoundEvent> ZIPLINE_USE = register("action.ride_zipline");
    public static final DeferredHolder<SoundEvent, SoundEvent> ZIPLINE_SET = register("zipline.set");
    public static final DeferredHolder<SoundEvent, SoundEvent> ZIPLINE_REMOVE = register("zipline.remove");
    public static final DeferredHolder<SoundEvent, SoundEvent> SKILLTREE_UNLOCK = register("skilltree.unlock");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(ParCool.resourceLocation(name)));
    }

    public static void register(IEventBus modBus) {
        SOUNDS.register(modBus);
    }
}
