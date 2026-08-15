package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import javax.annotation.Nonnull;

public class MultiStaminaPacket extends MultiComposablePacket<StaminaPacket> {
    public static final Type<MultiStaminaPacket> TYPE = new Type<>(ParCool.resourceLocation("stamina.compose"));

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final IHandler<MultiStaminaPacket> HANDLER = getDefaultHandler(MultiStaminaPacket::new, StaminaPacket.HANDLER);
}
