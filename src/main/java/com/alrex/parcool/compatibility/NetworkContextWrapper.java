package com.alrex.parcool.compatibility;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketDirection;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class NetworkContextWrapper {
    private Context context;

    private NetworkContextWrapper(Context context) {
        this.context = context;
    }

    public ServerPlayerEntity getSender() {
        return context.getSender();
    }

    public static Supplier<NetworkContextWrapper> getSupplier(Supplier<Context> contextSupplier) {
        return () -> new NetworkContextWrapper(contextSupplier.get());
    }

    public void enqueueWork(Runnable runnable) {
        context.enqueueWork(runnable);
    }

    public LogicalSide getReceptionSide() {
        return context.getDirection().getReceptionSide();
    }

    public void setPacketHandled(boolean b) {
        context.setPacketHandled(b);
    }

    public PacketDirection getDirection() {
        return context.getNetworkManager().getDirection();
    }
}
