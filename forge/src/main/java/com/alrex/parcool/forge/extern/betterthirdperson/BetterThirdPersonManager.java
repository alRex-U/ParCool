package com.alrex.parcool.forge.extern.betterthirdperson;

import com.alrex.parcool.client.input.LogicalMovement;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.forge.extern.ModManager;
import io.socol.betterthirdperson.BetterThirdPerson;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;

public class BetterThirdPersonManager extends ModManager {
    public BetterThirdPersonManager() {
        super("betterthirdperson");
    }

    private static final double THRESHOLD_COS = 1. / Math.sqrt(2.);

    @Environment(EnvType.CLIENT)
    @Nullable
    public ParCoolKeyBinds.LogicalInput getLogicalKey(LogicalMovement movement) {
        if (!isInstalled()) return null;
        if (!BetterThirdPerson.getCameraManager().hasCustomCamera()) return null;
        var cameraEntity = Minecraft.getInstance().getCameraEntity();
        if (cameraEntity == null) return null;

        var camera = BetterThirdPerson.getCameraManager().getCustomCamera();
        var cameraEntityYRot = cameraEntity.getYRot();
        var cameraYRot = camera.getYaw();

        var cos = Math.cos(Math.toRadians(cameraEntityYRot - cameraYRot));
        if (cos > THRESHOLD_COS) {
            return ParCoolKeyBinds.getStandardInput(movement);
        }
        if (cos < -THRESHOLD_COS) {
            return ParCoolKeyBinds.getStandardInput(movement.inverse());
        }
        var sin = Math.sin(cameraEntityYRot - cameraYRot);
        if (sin > 0) {
            return ParCoolKeyBinds.getStandardInput(movement.right());
        }
        return ParCoolKeyBinds.getStandardInput(movement.left());
    }
}
