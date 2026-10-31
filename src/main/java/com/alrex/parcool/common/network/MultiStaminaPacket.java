package com.alrex.parcool.common.network;

public class MultiStaminaPacket extends MultiComposablePacket<StaminaPacket.StoC> {
    public static final IHandler<MultiStaminaPacket> HANDLER = getDefaultHandler(MultiStaminaPacket::new, StaminaPacket.StoC.HANDLER);
}
