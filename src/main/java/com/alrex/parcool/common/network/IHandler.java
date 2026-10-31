package com.alrex.parcool.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface IHandler<MSG> {
    void encode(MSG msg, FriendlyByteBuf packet);

    MSG decode(FriendlyByteBuf packet);

    @OnlyIn(Dist.DEDICATED_SERVER)
    default void handleInPhysicalServer(MSG msg, Supplier<NetworkEvent.Context> contextSupplier) {
        handleInLogicalServer(msg, contextSupplier);
    }

    @OnlyIn(Dist.CLIENT)
    default void handleInPhysicalClient(MSG msg, Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            handleInLogicalClient(msg, contextSupplier);
        } else {
            handleInLogicalServer(msg, contextSupplier);
        }
    }

    void handleInLogicalServer(MSG msg, Supplier<NetworkEvent.Context> contextSupplier);

    @OnlyIn(Dist.CLIENT)
    void handleInLogicalClient(MSG msg, Supplier<NetworkEvent.Context> contextSupplier);
}
