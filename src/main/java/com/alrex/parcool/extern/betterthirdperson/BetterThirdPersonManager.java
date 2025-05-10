package com.alrex.parcool.extern.betterthirdperson;

import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.extern.ModManager;
import com.alrex.parcool.utilities.MathUtil;
import io.socol.betterthirdperson.BetterThirdPerson;
import io.socol.betterthirdperson.api.CustomCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class BetterThirdPersonManager extends ModManager {
    public BetterThirdPersonManager() {
        super("betterthirdperson");
    }

    public Dodge.DodgeDirection handleCustomCameraRotationForDodge(Dodge.DodgeDirection direction) {
        if (!isInstalled()) return direction;
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return direction;
            if (!BetterThirdPerson.getCameraManager().hasCustomCamera()) return direction;
            return Dodge.DodgeDirection.Front;
        }
        return direction;
    }

    public boolean isCameraDecoupled() {
        if (!isInstalled()) return false;
        if (!BetterThirdPerson.getCameraManager().hasCustomCamera()) return false;
        return true;
    }
}
