package com.alrex.parcool.extern.betterthirdperson;

import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.extern.ModManager;
import io.socol.betterthirdperson.BetterThirdPerson;
import net.minecraft.client.Minecraft;

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
