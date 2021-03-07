package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.ICrawl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncCrawlMessage {

    private boolean isCrawling=false;
    private boolean isSliding=false;
    private UUID playerID=null;
    private void encode(PacketBuffer packet) {
        packet.writeBoolean(this.isCrawling);
        packet.writeBoolean(this.isSliding);
        packet.writeLong(this.playerID.getMostSignificantBits());
        packet.writeLong(this.playerID.getLeastSignificantBits());
    }
    private static SyncCrawlMessage decode(PacketBuffer packet) {
        SyncCrawlMessage message = new SyncCrawlMessage();
        message.isCrawling=packet.readBoolean();
        message.isSliding=packet.readBoolean();
        message.playerID=new UUID(packet.readLong(), packet.readLong());
        return message;
    }
    private void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(()->{
            PlayerEntity player;
            if (contextSupplier.get().getNetworkManager().getDirection() == PacketDirection.CLIENTBOUND){
                player= Minecraft.getInstance().world.getPlayerByUuid(playerID);

            }else {
                player = contextSupplier.get().getSender();
            }
            LazyOptional<ICrawl> crawlOptional=player.getCapability(ICrawl.CrawlProvider.CRAWL_CAPABILITY);
            if (!crawlOptional.isPresent())return;
            ICrawl crawl=crawlOptional.resolve().get();
            crawl.setCrawling(this.isCrawling);
            crawl.setSliding(this.isSliding);
        });
        contextSupplier.get().setPacketHandled(true);
    }
    @OnlyIn(Dist.CLIENT)
    public static void sync(ClientPlayerEntity player){
        LazyOptional<ICrawl> crawlOptional=player.getCapability(ICrawl.CrawlProvider.CRAWL_CAPABILITY);
        if (!crawlOptional.isPresent())return;
        ICrawl crawl=crawlOptional.resolve().get();

        SyncCrawlMessage message=new SyncCrawlMessage();
        message.isCrawling= crawl.isCrawling();
        message.isSliding= crawl.isSliding();
        message.playerID=player.getUniqueID();

        ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(),message);
        ParCool.CHANNEL_INSTANCE.sendToServer(message);
    }
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class MessageRegistry{
        private static final int ID=1;
        @SubscribeEvent
        public static void register(FMLCommonSetupEvent event){
            ParCool.CHANNEL_INSTANCE.registerMessage(
                    ID,
                    SyncCrawlMessage.class,
                    SyncCrawlMessage::encode,
                    SyncCrawlMessage::decode,
                    SyncCrawlMessage::handle
            );
        }
    }
}
