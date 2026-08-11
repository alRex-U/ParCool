package com.alrex.parcool.forge.extern.feathers;

import com.alrex.parcool.api.stamina.StaminaTypeEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FeathersModLoadEventHandler {
    @SubscribeEvent
    public void onRegisterStaminaType(RegisterParCoolStaminaTypeEvent event) {
        event.register(new StaminaTypeEntry<>(
                new ResourceLocation("feathers", "feathers"),
                "feathers",
                (owner, old) -> new FeathersStamina(owner)
        ));
    }
}
