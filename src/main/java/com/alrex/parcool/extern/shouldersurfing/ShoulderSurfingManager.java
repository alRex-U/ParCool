package com.alrex.parcool.extern.shouldersurfing;

import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.Dodge.DodgeDirection;
import com.alrex.parcool.extern.ModManager;
import com.alrex.parcool.utilities.MathUtil;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public class ShoulderSurfingManager extends ModManager {
    public ShoulderSurfingManager() {
        super("shouldersurfing");
    }

    public Dodge.DodgeDirection handleCustomCameraRotationForDodge(Dodge.DodgeDirection direction) {
        if (!isInstalled()) return direction;
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) return direction;
        if (!ShoulderSurfing.getInstance().isShoulderSurfing()) return direction;
        if (isCameraDecoupled()) return DodgeDirection.Front;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return direction;
        IShoulderSurfingCamera camera = ShoulderSurfing.getInstance().getCamera();
        float yaw = MathUtil.normalizeDegree(camera.getYRot() - player.getYRot());
        float yawAbs = Math.abs(yaw);
        if (yawAbs < 45) return direction;
        if (yawAbs > 135) return direction.inverse();
        if (yaw < 0) return direction.left();
        return direction.right();
    }

    public Boolean isCameraDecoupled() {
        if (!isInstalled()) return false;
        return ShoulderSurfing.getInstance().isCameraDecoupled();
    }

    public float getCameraAngle() {
        if (!isInstalled()) return 0;
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) return 0;
        if (!ShoulderSurfing.getInstance().isShoulderSurfing()) return 0;
        IShoulderSurfingCamera camera = ShoulderSurfing.getInstance().getCamera();
        return camera.getYRot();
    }
}
