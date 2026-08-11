package com.alrex.parcool.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class InputUtil {
    public static Vec3 getInputVectorInWorld(Player player, Input input) {
        return new Vec3(input.leftImpulse, 0, input.forwardImpulse).yRot((float) Math.toRadians(-player.getYRot()));
    }
}
