package com.alrex.parcool.common.handlers;


import com.alrex.parcool.api.ParCoolAttributes;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public class AddAttributesHandler {
    public static void addAttributes() {
        EntityAttributeRegistry.register(() -> EntityType.PLAYER, () -> Mob.createMobAttributes()
                .add(ParCoolAttributes.MAX_STAMINA.get())
                .add(ParCoolAttributes.MAX_STAMINA.get())
                .add(ParCoolAttributes.STAMINA_RECOVERY.get())
                .add(ParCoolAttributes.BREAKFALL_DAMAGE_REDUCTION.get())
                .add(ParCoolAttributes.FAST_RUN_SPEED.get())
                .add(ParCoolAttributes.FAST_SWIM_SPEED.get())
                .add(ParCoolAttributes.SLIDE_DOWN_DECELERATION.get())
                .add(ParCoolAttributes.HORIZONTAL_WALL_RUN_DURATION.get())
        );
    }
}
