package com.alrex.parcool.common.compat.shoulderSurfing;

import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.FastRun;
import com.github.exopandora.shouldersurfing.api.callback.ICameraCouplingCallback;
import net.minecraft.client.Minecraft;

/**
 * Compatibility class for the "Should Surfing" mod
 */
public class ShoulderSurfingDecoupledCamera implements ICameraCouplingCallback {

    @Override
    public boolean isForcingCameraCoupling(Minecraft arg0) {
        return Parkourability.get(arg0.player).isDoingAny(FastRun.class, ClingToCliff.class);
    }
}