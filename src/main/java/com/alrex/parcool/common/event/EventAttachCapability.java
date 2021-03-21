package com.alrex.parcool.common.event;

import com.alrex.parcool.common.capability.*;
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
        event.addCapability(IStamina.StaminaProvider.CAPABILITY_LOCATION,new IStamina.StaminaProvider());
        event.addCapability(IWallJump.WallJumpProvider.CAPABILITY_LOCATION,new IWallJump.WallJumpProvider());
        event.addCapability(ICatLeap.CatLeapProvider.CAPABILITY_LOCATION,new ICatLeap.CatLeapProvider());
        event.addCapability(IGrabCliff.GrabCliffProvider.CAPABILITY_LOCATION,new IGrabCliff.GrabCliffProvider());
        event.addCapability(IVault.VaultProvider.CAPABILITY_LOCATION,new IVault.VaultProvider());
        event.addCapability(IDodge.DodgeProvider.CAPABILITY_LOCATION,new IDodge.DodgeProvider());
    }
}
