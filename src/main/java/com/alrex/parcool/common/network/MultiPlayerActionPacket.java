package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import javax.annotation.Nonnull;

public class MultiPlayerActionPacket extends MultiComposablePacket<PlayerActionPacket.StoC> {
    public static Type<MultiPlayerActionPacket> TYPE = new Type<>(ParCool.resourceLocation("action.mp"));
    public static final IHandler<MultiPlayerActionPacket> HANDLER = getDefaultHandler(MultiPlayerActionPacket::new, PlayerActionPacket.StoC.HANDLER);

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
