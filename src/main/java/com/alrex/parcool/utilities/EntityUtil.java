package com.alrex.parcool.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityUtil {
	private static Minecraft mc = Minecraft.getInstance();

	public static void addVelocity(Entity entity, Vec3 vec) {
		entity.setDeltaMovement(entity.getDeltaMovement().add(vec));
	}

	public static Vec3 GetCameraLookAngle() {
		return mc.player.getLookAngle();
	}
}
