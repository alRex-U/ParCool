package com.alrex.parcool.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {
    protected ClientPacketListenerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(method = "Lnet/minecraft/client/multiplayer/ClientPacketListener;handlePlayerInfoRemove(Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoRemovePacket;)V", at = @At("HEAD"))
    public void onHandlePlayerInfoRemove(ClientboundPlayerInfoRemovePacket packet, CallbackInfo ci) {
        for (var uuid : packet.profileIds()) {
        }
    }

    @Inject(method = "Lnet/minecraft/client/multiplayer/ClientPacketListener;handlePlayerInfoUpdate(Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoUpdatePacket;)V", at = @At("HEAD"))
    public void onHandlePlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket packet, CallbackInfo ci) {
        if (!packet.actions().contains(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER))
            return;
        for (var entry : packet.newEntries()) {
        }
    }
}
