package com.alrex.parcool.extern.shouldersurfing;

import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.capability.Parkourability;
import com.github.exopandora.shouldersurfing.api.callback.ICameraCouplingCallback;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;

import net.minecraft.client.Minecraft;

/**
 * Compatibility class for the "Should Surfing" mod
 */
public class ShoulderSurfingDecoupledCamera implements ICameraCouplingCallback, IShoulderSurfingPlugin {

    @Override
    public boolean isForcingCameraCoupling(Minecraft mc) {
        if (mc.player == null) return false;
        return Parkourability.get(mc.player).isDoingAny(ClingToCliff.class);
    }

    @Override
    public void register(IShoulderSurfingRegistrar registrar) {
        registrar.registerCameraCouplingCallback(this);
    }
}