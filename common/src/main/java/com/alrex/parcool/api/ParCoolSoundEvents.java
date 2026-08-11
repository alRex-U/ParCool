package com.alrex.parcool.api;

import com.alrex.parcool.ParCool;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;

public class ParCoolSoundEvents {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ParCool.MOD_ID, Registry.SOUND_EVENT_REGISTRY);

    public static final RegistrySupplier<SoundEvent> BREAKFALL = register("action.breakfall");
    public static final RegistrySupplier<SoundEvent> DODGE = register("action.dodge");
    public static final RegistrySupplier<SoundEvent> VAULT = register("action.vault");
    public static final RegistrySupplier<SoundEvent> HANG_ON = register("action.hang_on");
    public static final RegistrySupplier<SoundEvent> CLIMB_UP = register("action.climb_up");
    public static final RegistrySupplier<SoundEvent> WALL_RUN = register("action.wall_run");
    public static final RegistrySupplier<SoundEvent> WALL_JUMP = register("action.wall_jump");
    public static final RegistrySupplier<SoundEvent> CHARGE_JUMP = register("action.charge_jump");
    public static final RegistrySupplier<SoundEvent> HANG_DOWN = register("action.hang_down");
    public static final RegistrySupplier<SoundEvent> POLE_CLIMB = register("action.pole_climb");
    public static final RegistrySupplier<SoundEvent> SLIDE = register("action.slide");
    public static final RegistrySupplier<SoundEvent> HORIZONTAL_WALL_RUN = register("action.h_wall_run");
    public static final RegistrySupplier<SoundEvent> SLIDE_DOWN = register("action.slide_down");
    public static final RegistrySupplier<SoundEvent> DIVE = register("action.dive");
    public static final RegistrySupplier<SoundEvent> ZIPLINE_USE = register("action.ride_zipline");
    public static final RegistrySupplier<SoundEvent> ZIPLINE_SET = register("zipline.set");
    public static final RegistrySupplier<SoundEvent> ZIPLINE_REMOVE = register("zipline.remove");
    public static final RegistrySupplier<SoundEvent> SKILLTREE_UNLOCK = register("skilltree.unlock");

    private static RegistrySupplier<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> new SoundEvent(ParCool.resourceLocation(name)));
    }

    public static void register() {
        SOUNDS.register();
    }
}
