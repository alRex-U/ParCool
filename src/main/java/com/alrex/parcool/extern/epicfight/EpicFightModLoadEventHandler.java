package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.api.stamina.RegisterParCoolStaminaTypeEvent;
import com.alrex.parcool.api.stamina.StaminaTypeEntry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class EpicFightModLoadEventHandler {
    @SubscribeEvent
    public void onRegisterStaminaType(RegisterParCoolStaminaTypeEvent event) {
        event.register(new StaminaTypeEntry<>(
                ResourceLocation.fromNamespaceAndPath("epicfight", "epicfight"),
                "epicfight",
                (owner, old) -> new EpicFightStamina(owner)
        ));
    }

    @SubscribeEvent
    public void onRegisterPayload(RegisterPayloadHandlersEvent event) {
        var r = event.registrar("4.0.0.0");
        r.playToServer(
                EpicFightStaminaConsumePacket.TYPE,
                EpicFightStaminaConsumePacket.HANDLER.codec(),
                EpicFightStaminaConsumePacket.HANDLER.payloadHandler()
        );
    }
}
