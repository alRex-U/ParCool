package com.alrex.parcool.common.stamina;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.stamina.RegisterParCoolStaminaTypeEvent;
import com.alrex.parcool.api.stamina.StaminaTypeEntry;
import com.alrex.parcool.common.stamina.impl.HungerStamina;
import com.alrex.parcool.common.stamina.impl.NoneStamina;
import com.alrex.parcool.common.stamina.impl.ParCoolStamina;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;

public class StaminaTypes {
    private static final ResourceLocation NONE_ID = ParCool.resourceLocation("none");
    private static final ResourceLocation PARCOOL_ID = ParCool.resourceLocation("parcool");
    private static final ResourceLocation HUNGER_ID = ParCool.resourceLocation("hunger");

    public static final StaminaTypeEntry<NoneStamina> NONE_STAMINA = new StaminaTypeEntry<>(NONE_ID, NONE_ID.getPath(), NoneStamina::new);
    public static final StaminaTypeEntry<ParCoolStamina> PARCOOL_STAMINA = new StaminaTypeEntry<>(PARCOOL_ID, PARCOOL_ID.getPath(), ParCoolStamina::new);
    public static final StaminaTypeEntry<HungerStamina> HUNGER_STAMINA = new StaminaTypeEntry<>(HUNGER_ID, HUNGER_ID.getPath(), HungerStamina::new);

    @SubscribeEvent
    public static void onRegister(RegisterParCoolStaminaTypeEvent event) {
        event.register(NONE_STAMINA);
        event.register(PARCOOL_STAMINA);
        event.register(HUNGER_STAMINA);
    }
}
