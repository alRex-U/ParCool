package com.alrex.parcool.extern.shouldersurfing;

import com.alrex.parcool.client.input.LogicalMovement;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.extern.ModManager;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class ShoulderSurfingManager extends ModManager {
    public ShoulderSurfingManager() {
        super("shouldersurfing");
    }

    private static final double THRESHOLD_COS = 1. / Math.sqrt(2.);

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public ParCoolKeyBinds.LogicalInput getLogicalKey(LogicalMovement movement) {
        if (!isInstalled()) return null;
        if (!ShoulderSurfing.getInstance().isShoulderSurfing()) return null;
        var cameraEntity = Minecraft.getInstance().getCameraEntity();
        if (cameraEntity == null) return null;

        var camera = ShoulderSurfing.getInstance().getCamera();
        var cameraEntityYRot = cameraEntity.getYRot();
        var cameraYRot = camera.getYRot();

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
