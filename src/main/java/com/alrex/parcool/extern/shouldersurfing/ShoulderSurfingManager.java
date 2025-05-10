package com.alrex.parcool.extern.shouldersurfing;

import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.Dodge.DodgeDirection;
import com.alrex.parcool.extern.ModManager;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.Minecraft;

public class ShoulderSurfingManager extends ModManager {
    public ShoulderSurfingManager() {
        super("shouldersurfing");
    }

    public Dodge.DodgeDirection handleCustomCameraRotationForDodge(Dodge.DodgeDirection direction) {
        return isCameraDecoupled() ? DodgeDirection.Front : direction;
    }

    public Boolean isCameraDecoupled() {
        return isInstalled()
            && !Minecraft.getInstance().options.getCameraType().isFirstPerson()
            && ShoulderSurfing.getInstance().isShoulderSurfing()
            && ShoulderSurfing.getInstance().isCameraDecoupled();
    }
}
