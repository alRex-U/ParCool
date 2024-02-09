package com.alrex.parcool.client.sound;

import com.alrex.parcool.ParCool;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundEvents {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ParCool.MOD_ID);
    public static final SoundEvent VAULT = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.vault"));
    public static final SoundEvent VERTICAL_WALL_RUN = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.v_wall_run"));
    public static final SoundEvent HORIZONTAL_WALL_RUN = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.h_wall_run"));
    public static final SoundEvent BREAKFALL_JUST_TIME = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.breakfall.just"));
    public static final SoundEvent ROLL = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.breakfall.roll"));
    public static final SoundEvent SAFETY_TAP = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.breakfall.tap"));
    public static final SoundEvent CATLEAP = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.catleap"));
    public static final SoundEvent WALL_JUMP = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.wall_jump"));
    public static final SoundEvent CLING_TO_CLIFF = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.cling_to_cliff.grab"));
    public static final SoundEvent CLING_TO_CLIFF_JUMP = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.cling_to_cliff.jump"));
    public static final SoundEvent HANG_DOWN = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.hang_down.grab"));
    public static final SoundEvent HANG_DOWN_JUMP = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.hang_down.jump"));
    public static final SoundEvent SLIDE = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.slide"));
    public static final SoundEvent DODGE = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.dodge"));
    public static final SoundEvent PARCOOL_ENABLE = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "parcool.enable"));
    public static final SoundEvent PARCOOL_DISABLE = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "parcool.disable"));

    private static final RegistryObject<SoundEvent> REGISTRY_VAULT = SOUNDS.register("parcool.action.vault", () -> VAULT);
    private static final RegistryObject<SoundEvent> REGISTRY_VERTICAL_WALL_RUN = SOUNDS.register("parcool.action.v_wall_run", () -> VERTICAL_WALL_RUN);
    private static final RegistryObject<SoundEvent> REGISTRY_HORIZONTAL_WALL_RUN = SOUNDS.register("parcool.action.h_wall_run", () -> HORIZONTAL_WALL_RUN);
    private static final RegistryObject<SoundEvent> REGISTRY_BREAKFALL_JUST_TIME = SOUNDS.register("parcool.action.breakfall.just", () -> BREAKFALL_JUST_TIME);
    private static final RegistryObject<SoundEvent> REGISTRY_ROLL = SOUNDS.register("parcool.action.breakfall.roll", () -> ROLL);
    private static final RegistryObject<SoundEvent> REGISTRY_SAFETY_TAP = SOUNDS.register("parcool.action.breakfall.tap", () -> SAFETY_TAP);
    private static final RegistryObject<SoundEvent> REGISTRY_CATLEAP = SOUNDS.register("parcool.action.catleap", () -> CATLEAP);
    private static final RegistryObject<SoundEvent> REGISTRY_WALL_JUMP = SOUNDS.register("parcool.action.wall_jump", () -> WALL_JUMP);
    private static final RegistryObject<SoundEvent> REGISTRY_CLING_TO_CLIFF = SOUNDS.register("parcool.action.cling_to_cliff.grab", () -> CLING_TO_CLIFF);
    private static final RegistryObject<SoundEvent> REGISTRY_CLING_TO_CLIFF_JUMP = SOUNDS.register("parcool.action.cling_to_cliff.jump", () -> CLING_TO_CLIFF_JUMP);
    private static final RegistryObject<SoundEvent> REGISTRY_HANG_DOWN = SOUNDS.register("action.hang_down.grab", () -> HANG_DOWN);
    private static final RegistryObject<SoundEvent> REGISTRY_HANG_DOWN_JUMP = SOUNDS.register("parcool.hang_down.jump", () -> HANG_DOWN_JUMP);
    private static final RegistryObject<SoundEvent> REGISTRY_SLIDE = SOUNDS.register("parcool.action.slide", () -> SLIDE);
    private static final RegistryObject<SoundEvent> REGISTRY_DODGE = SOUNDS.register("parcool.action.dodge", () -> DODGE);
    private static final RegistryObject<SoundEvent> REGISTRY_PARCOOL_ENABLE = SOUNDS.register("parcool.action.enable", () -> PARCOOL_ENABLE);
    private static final RegistryObject<SoundEvent> REGISTRY_PARCOOL_DISABLE = SOUNDS.register("parcool.action.disable", () -> PARCOOL_DISABLE);

    public static void registerAll(IEventBus modBus) {
        SOUNDS.register(modBus);
    }
}
