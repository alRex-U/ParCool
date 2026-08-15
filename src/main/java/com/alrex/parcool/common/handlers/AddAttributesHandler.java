package com.alrex.parcool.common.handlers;


import com.alrex.parcool.api.ParCoolAttributes;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

public class AddAttributesHandler {
    @SubscribeEvent
    public static void onAddAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ParCoolAttributes.MAX_STAMINA);
        event.add(EntityType.PLAYER, ParCoolAttributes.STAMINA_RECOVERY);
        event.add(EntityType.PLAYER, ParCoolAttributes.BREAKFALL_DAMAGE_REDUCTION);
        event.add(EntityType.PLAYER, ParCoolAttributes.FAST_RUN_SPEED);
        event.add(EntityType.PLAYER, ParCoolAttributes.FAST_SWIM_SPEED);
        event.add(EntityType.PLAYER, ParCoolAttributes.SLIDE_DOWN_DECELERATION);
        event.add(EntityType.PLAYER, ParCoolAttributes.HORIZONTAL_WALL_RUN_DURATION);
    }
}
