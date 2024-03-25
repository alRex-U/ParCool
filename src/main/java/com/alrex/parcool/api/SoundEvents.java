package com.alrex.parcool.api;

import com.alrex.parcool.ParCool;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundEvents {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ParCool.MOD_ID);
    private static final SoundEvent VAULT_SOUND = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.vault"));
    private static final SoundEvent VERTICAL_WALL_RUN_SOUND = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.v_wall_run"));
    private static final SoundEvent HORIZONTAL_WALL_RUN_SOUND = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.h_wall_run"));
    private static final SoundEvent BREAKFALL_JUST_TIME_SOUND = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.breakfall.just"));
    private static final SoundEvent ROLL_SOUND = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.breakfall.roll"));
    private static final SoundEvent SAFETY_TAP_SOUND = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.breakfall.tap"));
    private static final SoundEvent CATLEAP_SOUND = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.catleap"));
    private static final SoundEvent WALL_JUMP_SOUND = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.wall_jump"));
    private static final SoundEvent CLING_TO_CLIFF_SOUND = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.cling_to_cliff.grab"));
    private static final SoundEvent CLING_TO_CLIFF_JUMP_SOUND = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.cling_to_cliff.jump"));
    private static final SoundEvent HANG_DOWN_SOUND = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.hang_down.grab"));
    private static final SoundEvent HANG_DOWN_JUMP_SOUND = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.hang_down.jump"));
    private static final SoundEvent SLIDE_SOUND = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.slide"));
    private static final SoundEvent DODGE_SOUND = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "action.dodge"));
    private static final SoundEvent PARCOOL_ENABLE_SOUND = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "parcool.enable"));
    private static final SoundEvent PARCOOL_DISABLE_SOUND = new SoundEvent(new ResourceLocation(ParCool.MOD_ID, "parcool.disable"));

    public static final RegistryObject<SoundEvent> VAULT = SOUNDS.register("action.vault", () -> VAULT_SOUND);
    public static final RegistryObject<SoundEvent> VERTICAL_WALL_RUN = SOUNDS.register("action.v_wall_run", () -> VERTICAL_WALL_RUN_SOUND);
    public static final RegistryObject<SoundEvent> HORIZONTAL_WALL_RUN = SOUNDS.register("action.h_wall_run", () -> HORIZONTAL_WALL_RUN_SOUND);
    public static final RegistryObject<SoundEvent> BREAKFALL_JUST_TIME = SOUNDS.register("action.breakfall.just", () -> BREAKFALL_JUST_TIME_SOUND);
    public static final RegistryObject<SoundEvent> ROLL = SOUNDS.register("action.breakfall.roll", () -> ROLL_SOUND);
    public static final RegistryObject<SoundEvent> SAFETY_TAP = SOUNDS.register("action.breakfall.tap", () -> SAFETY_TAP_SOUND);
    public static final RegistryObject<SoundEvent> CATLEAP = SOUNDS.register("action.catleap", () -> CATLEAP_SOUND);
    public static final RegistryObject<SoundEvent> WALL_JUMP = SOUNDS.register("action.wall_jump", () -> WALL_JUMP_SOUND);
    public static final RegistryObject<SoundEvent> CLING_TO_CLIFF = SOUNDS.register("action.cling_to_cliff.grab", () -> CLING_TO_CLIFF_SOUND);
    public static final RegistryObject<SoundEvent> CLING_TO_CLIFF_JUMP = SOUNDS.register("action.cling_to_cliff.jump", () -> CLING_TO_CLIFF_JUMP_SOUND);
    public static final RegistryObject<SoundEvent> HANG_DOWN = SOUNDS.register("action.hang_down.grab", () -> HANG_DOWN_SOUND);
    public static final RegistryObject<SoundEvent> HANG_DOWN_JUMP = SOUNDS.register("hang_down.jump", () -> HANG_DOWN_JUMP_SOUND);
    public static final RegistryObject<SoundEvent> SLIDE = SOUNDS.register("action.slide", () -> SLIDE_SOUND);
    public static final RegistryObject<SoundEvent> DODGE = SOUNDS.register("action.dodge", () -> DODGE_SOUND);
    public static final RegistryObject<SoundEvent> PARCOOL_ENABLE = SOUNDS.register("action.enable", () -> PARCOOL_ENABLE_SOUND);
    public static final RegistryObject<SoundEvent> PARCOOL_DISABLE = SOUNDS.register("action.disable", () -> PARCOOL_DISABLE_SOUND);

    public static void registerAll(IEventBus modBus) {
        SOUNDS.register(modBus);
    }
}
