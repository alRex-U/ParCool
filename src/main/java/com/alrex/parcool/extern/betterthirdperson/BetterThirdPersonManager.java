package com.alrex.parcool.extern.betterthirdperson;

import com.alrex.parcool.api.compatibility.ClientPlayerWrapper;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.extern.ModManager;
import com.alrex.parcool.utilities.MathUtil;
import io.socol.betterthirdperson.BetterThirdPerson;
import io.socol.betterthirdperson.api.CustomCamera;
import net.minecraft.client.Minecraft;

public class BetterThirdPersonManager extends ModManager {
    public BetterThirdPersonManager() {
        super("betterthirdperson");
    }

    public Dodge.DodgeDirection handleCustomCameraRotationForDodge(Dodge.DodgeDirection direction) {
        if (!isInstalled()) return direction;
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) return direction;
        ClientPlayerWrapper player = ClientPlayerWrapper.get();
        if (player == null) return direction;
        if (!BetterThirdPerson.getCameraManager().hasCustomCamera()) return direction;
        CustomCamera camera = BetterThirdPerson.getCameraManager().getCustomCamera();
        float yaw = MathUtil.normalizeDegree(camera.getPlayerRotation().getYaw() - camera.getCameraRotation().getYaw());
        float yawAbs = Math.abs(yaw);
        if (yawAbs < 45) return direction;
        if (yawAbs > 135) direction.inverse();
        if (yaw < 0) direction.right();
        return direction.left();
    }
}
