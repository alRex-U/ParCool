package com.alrex.parcool.utilities;

import com.alrex.parcool.api.compatibility.EntityWrapper;
import net.minecraft.util.math.vector.Vector3d;

public class EntityUtil {
	public static void addVelocity(EntityWrapper entity, Vector3d vec) {
		entity.addToDeltaMovement(vec);
	}
}
