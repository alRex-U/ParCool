package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import javax.annotation.Nonnull;

public class ActionStateSetPacket extends MultiComposablePacket<ActionGroupStatePacket> implements CustomPacketPayload {
    public static final Type<ActionStateSetPacket> TYPE = new Type<>(ParCool.resourceLocation("action.set"));

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static IHandler<ActionStateSetPacket> HANDLER = getDefaultHandler(ActionStateSetPacket::new, ActionGroupStatePacket.HANDLER);
}
