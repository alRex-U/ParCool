package com.alrex.parcool.client.renderer;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerCustomRenderer {
    @SubscribeEvent
    public static void onPlayerRenderPre(RenderPlayerEvent.Pre event){
        PlayerEntity player= event.getPlayer();

    }
    @SubscribeEvent
    public static void onPlayerRenderPost(RenderPlayerEvent.Post event){
        AbstractClientPlayerEntity player= (AbstractClientPlayerEntity)event.getPlayer();

    }
}
