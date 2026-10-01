package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.skilltree.SkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record SkilltreePacket(List<SkillTree> skillTrees) {
    public static final IHandler<SkilltreePacket> HANDLER = new IHandler<>() {
        @Override
        public void encode(SkilltreePacket skilltreePacket, FriendlyByteBuf buf) {
            buf.writeByte(skilltreePacket.skillTrees.size());
            for (var skilltree : skilltreePacket.skillTrees) {
                skilltree.saveTo(buf);
            }
        }

        @Override
        public SkilltreePacket decode(FriendlyByteBuf packet) {
            var size = packet.readByte();
            var list = new ArrayList<SkillTree>(size);
            for (var i = 0; i < size; i++) {
                list.add(SkillTree.readFrom(ParCool.getActionRegistry(), packet));
            }
            return new SkilltreePacket(list);
        }

        @OnlyIn(Dist.DEDICATED_SERVER)
        @Override
        public void handleInPhysicalServer(SkilltreePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            throw new UnsupportedOperationException();
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void handleInPhysicalClient(SkilltreePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            var player = Minecraft.getInstance().player;
            if (player == null) return;
            Parkourability.get(player).syncSkillTree(packet.skillTrees);
        }
    };
}
