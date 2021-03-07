package com.alrex.parcool.common.event;

import com.alrex.parcool.common.capability.ICrawl;
import com.alrex.parcool.common.capability.IFastRunning;
import com.alrex.parcool.common.capability.IJumpBoost;
import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventAttachCapability {

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event){
        if (!(event.getObject() instanceof PlayerEntity))return;
        event.addCapability(ICrawl.CrawlProvider.CAPABILITY_LOCATION,new ICrawl.CrawlProvider());
        event.addCapability(IFastRunning.FastRunningProvider.CAPABILITY_LOCATION,new IFastRunning.FastRunningProvider());
        event.addCapability(IJumpBoost.JumpBoostProvider.CAPABILITY_LOCATION,new IJumpBoost.JumpBoostProvider());
        event.addCapability(IStamina.StaminaProvider.CAPABILITY_LOCATION,new IStamina.StaminaProvider());
    }
}
