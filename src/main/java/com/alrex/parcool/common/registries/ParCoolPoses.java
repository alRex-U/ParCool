package com.alrex.parcool.common.registries;

import net.minecraft.world.entity.Pose;

public enum ParCoolPoses {
    ROLLING,
    VAULTING;

    public Pose get() {
        return Pose.valueOf(this.name());
    }
}
