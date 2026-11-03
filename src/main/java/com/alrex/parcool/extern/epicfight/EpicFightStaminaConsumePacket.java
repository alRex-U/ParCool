package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.network.IHandler;
import com.alrex.parcool.extern.AdditionalMods;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record EpicFightStaminaConsumePacket(float value) implements CustomPacketPayload {
    public static final Type<EpicFightStaminaConsumePacket> TYPE = new Type<>(ParCool.resourceLocation("epicfight.stamina"));

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final IHandler<EpicFightStaminaConsumePacket> HANDLER = new IHandler<>() {
        @Override
        public void encode(ByteBuf packet, EpicFightStaminaConsumePacket staminaPacket) {
            packet.writeFloat(staminaPacket.value);
        }

        @Override
        public EpicFightStaminaConsumePacket decode(ByteBuf packet) {
            return new EpicFightStaminaConsumePacket(packet.readFloat());
        }

        @Override
        public void handleInLogicalServer(EpicFightStaminaConsumePacket staminaPacket, IPayloadContext context) {
            var player = context.player();
            var patch = AdditionalMods.epicFight().getPlayerPatch(player);
            if (patch == null) return;
            patch.resetActionTick();
            patch.setStamina(patch.getStamina() - staminaPacket.value);
        }

        @Override
        public void handleInLogicalClient(EpicFightStaminaConsumePacket staminaPacket, IPayloadContext context) {
        }
    };
}
