package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.skilltree.SkillTree;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public record SkilltreePacket(List<SkillTree> skillTrees) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SkilltreePacket> TYPE = new CustomPacketPayload.Type<>(ParCool.resourceLocation("skilltree"));

    @Nonnull
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final IHandler<SkilltreePacket> HANDLER = new IHandler<>() {

        @Override
        public void encode(ByteBuf buf, SkilltreePacket skilltreePacket) {
            buf.writeByte(skilltreePacket.skillTrees.size());
            for (var skilltree : skilltreePacket.skillTrees) {
                skilltree.saveTo(buf);
            }
        }

        @Override
        public SkilltreePacket decode(ByteBuf packet) {
            var size = packet.readByte();
            var list = new ArrayList<SkillTree>(size);
            for (var i = 0; i < size; i++) {
                list.add(SkillTree.readFrom(ParCool.getActionRegistry(), packet));
            }
            return new SkilltreePacket(list);
        }

        @OnlyIn(Dist.DEDICATED_SERVER)
        @Override
        public void handleInLogicalServer(SkilltreePacket packet, IPayloadContext context) {
            throw new UnsupportedOperationException();
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void handleInLogicalClient(SkilltreePacket packet, IPayloadContext context) {
            var player = Minecraft.getInstance().player;
            if (player == null) return;
            Parkourability.get(player).syncSkillTree(packet.skillTrees);
        }
    };
}
