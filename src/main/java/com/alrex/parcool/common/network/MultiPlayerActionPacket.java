package com.alrex.parcool.common.network;

public class MultiPlayerActionPacket extends MultiComposablePacket<PlayerActionPacket.StoC> {
    public static final IHandler<MultiPlayerActionPacket> HANDLER = getDefaultHandler(MultiPlayerActionPacket::new, PlayerActionPacket.StoC.HANDLER);
}
