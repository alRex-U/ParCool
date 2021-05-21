package com.alrex.parcool.client.particle;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Random;

//only in Client
public class ParticleProvider {
	public static void spawnEffectActivateParCool(AbstractClientPlayerEntity player) {
		ClientWorld world = player.worldClient;
		final double x = player.getPosX();
		final double y = player.getPosY() + 0.1;
		final double z = player.getPosZ();

		Vector3d motion = player.getMotion();
		Vector3d vec = new Vector3d(0, 0, 0.3);
		for (int i = 0; i < 16; i++) {
			Vector3d direction = vec.rotateYaw((float) (Math.PI / 8 * i)).add(motion);
			world.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, direction.getX(), 0, direction.getZ());
		}
	}

	public static void spawnEffectSweat(AbstractClientPlayerEntity player) {
		ClientWorld world = player.worldClient;
		Random random = player.getRNG();
		final double x = player.getPosX();
		final double y = player.getPosY();
		final double z = player.getPosZ();
		world.addParticle(
				ParticleTypes.DRIPPING_WATER,
				x + random.nextInt(10) / 10d - 0.5,
				y + random.nextInt(20) / 10d,
				z + random.nextInt(10) / 10d - 0.5,
				0, 0, 0
		);
	}
}
