package com.alrex.parcool.common.event;


import com.alrex.parcool.api.Attributes;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventAddAttributes {
    @SubscribeEvent
    public static void onAddAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, Attributes.MAX_STAMINA.get());
        event.add(EntityType.PLAYER, Attributes.STAMINA_RECOVERY.get());
    }
}
