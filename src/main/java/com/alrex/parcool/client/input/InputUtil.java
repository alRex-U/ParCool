package com.alrex.parcool.client.input;

import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InputUtil {
    public static Vec3 getInputVectorInWorld(Player player, Input input) {
        return new Vec3(input.leftImpulse, 0, input.forwardImpulse).yRot((float) Math.toRadians(-player.getYRot()));
    }
}
