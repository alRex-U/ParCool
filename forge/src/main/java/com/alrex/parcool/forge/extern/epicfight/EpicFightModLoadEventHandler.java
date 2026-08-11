package com.alrex.parcool.forge.extern.epicfight;

import com.alrex.parcool.api.stamina.StaminaTypeEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EpicFightModLoadEventHandler {
    @SubscribeEvent
    public void onRegisterStaminaType(RegisterParCoolStaminaTypeEvent event) {
        event.register(new StaminaTypeEntry<>(
                new ResourceLocation("epicfight", "epicfight"),
                "epicfight",
                (owner, old) -> new EpicFightStamina(owner)
        ));
    }
}
