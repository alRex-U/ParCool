package com.alrex.parcool.common.network;

public class MultiStaminaPacket extends MultiComposablePacket<StaminaPacket> {
    public static final IHandler<MultiStaminaPacket> HANDLER = getDefaultHandler(MultiStaminaPacket::new, StaminaPacket.HANDLER);
}
