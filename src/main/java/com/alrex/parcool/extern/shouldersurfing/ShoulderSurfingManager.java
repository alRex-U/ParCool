package com.alrex.parcool.extern.shouldersurfing;

import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.extern.ModManager;
import com.alrex.parcool.utilities.MathUtil;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;

public class ShoulderSurfingManager extends ModManager {
    public ShoulderSurfingManager() {
        super("shouldersurfing");
    }

    public Dodge.DodgeDirection handleCustomCameraRotationForDodge(Dodge.DodgeDirection direction) {
        if (!isInstalled()) return direction;
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player == null) return direction;
            IShoulderSurfingCamera camera = ShoulderSurfing.getInstance().getCamera();
            float yaw = MathUtil.normalizeDegree(camera.getYRot() - player.yRot);
            float yawAbs = Math.abs(yaw);
            if (yawAbs < 45) {
                return direction;
            } else if (yawAbs > 135) {
                return direction.inverse();
            } else if (yaw < 0) {
                return direction.left();
            } else {
                return direction.right();
            }
        }
        return direction;
    }
}
