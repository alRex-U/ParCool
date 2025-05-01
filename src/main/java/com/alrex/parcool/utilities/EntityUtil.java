package com.alrex.parcool.utilities;

import com.alrex.parcool.api.compatibility.EntityWrapper;
import com.alrex.parcool.api.compatibility.Vec3Wrapper;

public class EntityUtil {
	public static void addVelocity(EntityWrapper entity, Vec3Wrapper vec) {
		entity.addToDeltaMovement(vec);
	}
}
