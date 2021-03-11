package com.alrex.parcool.common.processor;

import com.alrex.parcool.common.capability.ICrawl;
import com.alrex.parcool.common.capability.IJumpBoost;
import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.FORGE)
public class JumpBoostLogic {
    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event){
        if (!event.getEntityLiving().world.isRemote) return;//client only

        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != event.getEntityLiving())return;
        LazyOptional<ICrawl> crawlOptional=player.getCapability(ICrawl.CrawlProvider.CRAWL_CAPABILITY);
        LazyOptional<IJumpBoost> jumpBoostOptional=player.getCapability(IJumpBoost.JumpBoostProvider.JUMP_BOOST_CAPABILITY);
        if (!jumpBoostOptional.isPresent() || !crawlOptional.isPresent())return;
        IJumpBoost jumpBoost=jumpBoostOptional.resolve().get();
        ICrawl crawl=crawlOptional.resolve().get();

        if (jumpBoost.canJumpBoost(player) && !player.isSneaking())player.addVelocity(0,jumpBoost.getBoostValue(player),0);
        if (crawl.isSliding()){
            Vector3d vec=player.getMotion();
            player.setMotion(vec.getX(),0,vec.getZ());
        }
    }
}
