package com.alrex.parcool.common.network;

import net.minecraft.resources.ResourceLocation;

public class MultiActionStateSetPacket extends MultiComposablePacket<ActionStateSetPacket> {
    public static final ResourceLocation ID = getComposedID(ActionStateSetPacket.HANDLER);

    public MultiActionStateSetPacket() {
        super(ActionStateSetPacket.HANDLER);
    }
}
