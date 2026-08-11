package com.alrex.parcool.common.network;

import net.minecraft.resources.ResourceLocation;

public class MultiStaminaPacket extends MultiComposablePacket<StaminaPacket> {
    public static final ResourceLocation ID = getComposedID(StaminaPacket.HANDLER);

    public MultiStaminaPacket() {
        super(StaminaPacket.HANDLER);
    }
}
