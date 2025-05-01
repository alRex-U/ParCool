package com.alrex.parcool.utilities;

import com.alrex.parcool.api.compatibility.PlayerWrapper;
import net.minecraft.util.HandSide;

public class BipedModelUtil {
    public static HandSide getAttackArm(PlayerWrapper player) {
        HandSide handside = player.getMainArm();
        return player.isSwingingMainHand() ? handside : handside.getOpposite();
    }
}
