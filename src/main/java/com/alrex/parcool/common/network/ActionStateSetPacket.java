package com.alrex.parcool.common.network;

public class ActionStateSetPacket extends MultiComposablePacket<ActionGroupStatePacket> {
    public static IHandler<ActionStateSetPacket> HANDLER = getDefaultHandler(ActionStateSetPacket::new, ActionGroupStatePacket.HANDLER);
}
