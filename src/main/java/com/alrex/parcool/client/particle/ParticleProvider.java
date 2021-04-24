package com.alrex.parcool.client.particle;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleProvider {
	public static void spawnEffectActivateParCool(AbstractClientPlayerEntity player) {
		ClientWorld world = player.worldClient;
		final double x = player.getPosX();
		final double y = player.getPosY() + 0.1;
		final double z = player.getPosZ();

		Vector3d vec = new Vector3d(0, 0, 0.3);
		for (int i = 0; i < 16; i++) {
			Vector3d direction = vec.rotateYaw((float) (Math.PI / 8 * i));
			world.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, direction.getX(), 0, direction.getZ());
		}
	}
}
