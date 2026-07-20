package com.alrex.parcool.common.handlers;


import com.alrex.parcool.api.ParCoolAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AddAttributesHandler {
    @SubscribeEvent
    public static void onAddAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ParCoolAttributes.MAX_STAMINA.get());
        event.add(EntityType.PLAYER, ParCoolAttributes.STAMINA_RECOVERY.get());
        event.add(EntityType.PLAYER, ParCoolAttributes.BREAKFALL_DAMAGE_REDUCTION.get());
        event.add(EntityType.PLAYER, ParCoolAttributes.FAST_RUN_SPEED.get());
        event.add(EntityType.PLAYER, ParCoolAttributes.FAST_SWIM_SPEED.get());
        event.add(EntityType.PLAYER, ParCoolAttributes.SLIDE_DOWN_DECELERATION.get());
        event.add(EntityType.PLAYER, ParCoolAttributes.HORIZONTAL_WALL_RUN_DURATION.get());
    }
}
