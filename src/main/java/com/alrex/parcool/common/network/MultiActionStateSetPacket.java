package com.alrex.parcool.common.network;

public class MultiActionStateSetPacket extends MultiComposablePacket<ActionStateSetPacket> {
    public static final IHandler<MultiActionStateSetPacket> HANDLER = getDefaultHandler(MultiActionStateSetPacket::new, ActionStateSetPacket.HANDLER);
}
