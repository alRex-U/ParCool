package com.alrex.parcool.common.compat.shoulderSurfing;

import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.FastRun;
import com.github.exopandora.shouldersurfing.api.callback.ICameraCouplingCallback;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;
import com.github.exopandora.shouldersurfing.plugin.ShoulderSurfingRegistrar;

import net.minecraft.client.Minecraft;

/**
 * Compatibility class for the "Should Surfing" mod
 */
public class ShoulderSurfingDecoupledCamera implements ICameraCouplingCallback, IShoulderSurfingPlugin {

    @Override
    public boolean isForcingCameraCoupling(Minecraft mc) {
        if (mc.player == null) return false;
        return Parkourability.get(mc.player).isDoingAny(FastRun.class, ClingToCliff.class);
    }

    @Override
    public void register(IShoulderSurfingRegistrar registrar) {
        registrar.registerCameraCouplingCallback(this);
    }
}