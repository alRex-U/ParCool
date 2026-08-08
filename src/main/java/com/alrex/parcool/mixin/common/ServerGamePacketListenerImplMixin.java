package com.alrex.parcool.mixin.common;

import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerPlayerConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin implements ServerPlayerConnection, TickablePacketListener, ServerGamePacketListener {
    @Shadow
    private boolean clientIsFloating;

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleMovePlayer", at = @At("RETURN"))
    private void onHandleMovePlayer(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
        if (clientIsFloating) {
            var parkourability = Parkourability.get(player);
            if (parkourability.get(ParCoolActions.RIDE_ZIPLINE).isDoing()) {
                clientIsFloating = false;
            }
        }
    }
}
