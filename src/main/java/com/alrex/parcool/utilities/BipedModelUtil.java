package com.alrex.parcool.utilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;

public class BipedModelUtil {
    public static HandSide getAttackArm(PlayerEntity player) {
        HandSide handside = player.getMainArm();
        return player.swingingArm == Hand.MAIN_HAND ? handside : handside.getOpposite();
    }
}
