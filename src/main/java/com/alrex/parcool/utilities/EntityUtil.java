package com.alrex.parcool.utilities;

import com.alrex.parcool.compatibility.EntityWrapper;
import com.alrex.parcool.compatibility.Vec3Wrapper;

public class EntityUtil {
	public static void addVelocity(EntityWrapper entity, Vec3Wrapper vec) {
		entity.addToDeltaMovement(vec);
	}
}
