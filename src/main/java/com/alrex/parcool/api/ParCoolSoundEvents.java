package com.alrex.parcool.api;

import com.alrex.parcool.ParCool;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ParCoolSoundEvents {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ParCool.MOD_ID);

    public static final RegistryObject<SoundEvent> BREAKFALL = register("action.breakfall");
    public static final RegistryObject<SoundEvent> DODGE = register("action.dodge");
    public static final RegistryObject<SoundEvent> VAULT = register("action.vault");
    public static final RegistryObject<SoundEvent> HANG_ON = register("action.hang_on");
    public static final RegistryObject<SoundEvent> CLIMB_UP = register("action.climb_up");
    public static final RegistryObject<SoundEvent> WALL_RUN = register("action.wall_run");
    public static final RegistryObject<SoundEvent> WALL_JUMP = register("action.wall_jump");
    public static final RegistryObject<SoundEvent> CHARGE_JUMP = register("action.charge_jump");
    public static final RegistryObject<SoundEvent> HANG_DOWN = register("action.hang_down");
    public static final RegistryObject<SoundEvent> POLE_CLIMB = register("action.pole_climb");
    public static final RegistryObject<SoundEvent> SLIDE = register("action.slide");
    public static final RegistryObject<SoundEvent> HORIZONTAL_WALL_RUN = register("action.h_wall_run");
    public static final RegistryObject<SoundEvent> SLIDE_DOWN = register("action.slide_down");
    public static final RegistryObject<SoundEvent> DIVE = register("action.dive");
    public static final RegistryObject<SoundEvent> ZIPLINE_USE = register("action.zipline");
    public static final RegistryObject<SoundEvent> ZIPLINE_SET = register("zipline.set");
    public static final RegistryObject<SoundEvent> ZIPLINE_REMOVE = register("zipline.remove");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> new SoundEvent(new ResourceLocation(ParCool.MOD_ID, name)));
    }

    public static void register(IEventBus modBus) {
        SOUNDS.register(modBus);
    }
}
