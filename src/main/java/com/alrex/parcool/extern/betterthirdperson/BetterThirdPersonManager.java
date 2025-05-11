package com.alrex.parcool.extern.betterthirdperson;

import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.extern.ModManager;
import com.alrex.parcool.utilities.VectorUtil;

import io.socol.betterthirdperson.BetterThirdPerson;
import io.socol.betterthirdperson.api.CustomCameraManager;
import io.socol.betterthirdperson.api.util.AngleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public class BetterThirdPersonManager extends ModManager {
    public BetterThirdPersonManager() {
        super("betterthirdperson");
    }

    public Dodge.DodgeDirection handleCustomCameraRotationForDodge(Dodge.DodgeDirection direction) {
        return isCameraDecoupled() ? Dodge.DodgeDirection.Front : direction;
    }

    public boolean isCameraDecoupled() {
        return isInstalled()
            && !Minecraft.getInstance().options.getCameraType().isFirstPerson()
            && BetterThirdPerson.getCameraManager().hasCustomCamera();
    }
}
