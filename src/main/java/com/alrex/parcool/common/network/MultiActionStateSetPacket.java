package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import javax.annotation.Nonnull;

public class MultiActionStateSetPacket extends MultiComposablePacket<ActionStateSetPacket> {
    public static final Type<MultiActionStateSetPacket> TYPE = new Type<>(ParCool.resourceLocation("action.compose"));

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final IHandler<MultiActionStateSetPacket> HANDLER = getDefaultHandler(MultiActionStateSetPacket::new, ActionStateSetPacket.HANDLER);
}
