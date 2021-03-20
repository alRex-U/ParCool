package com.alrex.parcool.common.event;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.capability.*;
import com.alrex.parcool.common.network.SyncCatLeapMessage;
import com.alrex.parcool.common.network.SyncCrawlMessage;
import com.alrex.parcool.common.network.SyncFastRunningMessage;
import com.alrex.parcool.common.network.SyncGrabCliffMessage;
import com.alrex.parcool.constants.TranslateKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Level;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventActivateParCool {
    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event){
        if (event.phase != TickEvent.Phase.START)return;
        if (KeyRecorder.keyActivateParCoolState.isPressed()){
            ClientPlayerEntity player=Minecraft.getInstance().player;
            if (player==null)return;
            IStamina stamina;
            {
                LazyOptional<IStamina> staminaOptional=player.getCapability(IStamina.StaminaProvider.STAMINA_CAPABILITY);
                if (!staminaOptional.isPresent())return;
                stamina=staminaOptional.resolve().get();
            }
            if (stamina.isExhausted()){
                player.sendChatMessage(I18n.format(TranslateKeys.WARNING_ACTIVATION_EXHAUSTED));
                return;
            }
            boolean active=!ParCool.isActive();
            ParCool.setActivation(active);
            if (I18n.hasKey(TranslateKeys.MESSAGE_ACTIVATION_ACTIVE)){
                player.sendChatMessage(
                        "ParCool:" + I18n.format(active? TranslateKeys.MESSAGE_ACTIVATION_ACTIVE:TranslateKeys.MESSAGE_ACTIVATION_INACTIVE)
                );
            }
            if (active)activate();else inactivate();
        }
    }
    public static void activate(){

    }
    public static void inactivate(){
        ClientPlayerEntity player=Minecraft.getInstance().player;

        ICatLeap catLeap;
        ICrawl crawl;
        IFastRunning fastRunning;
        IGrabCliff grabCliff;
        IVault vault;
        IWallJump wallJump;
        {
            LazyOptional<ICatLeap> catLeapOptional=player.getCapability(ICatLeap.CatLeapProvider.CAT_LEAP_CAPABILITY);
            LazyOptional<ICrawl> crawlOptional=player.getCapability(ICrawl.CrawlProvider.CRAWL_CAPABILITY);
            LazyOptional<IFastRunning> fastRunningOptional=player.getCapability(IFastRunning .FastRunningProvider.FAST_RUNNING_CAPABILITY);
            LazyOptional<IGrabCliff> grabCliffOptional=player.getCapability(IGrabCliff.GrabCliffProvider.GRAB_CLIFF_CAPABILITY);
            LazyOptional<IVault> vaultOptional=player.getCapability(IVault.VaultProvider.VAULT_CAPABILITY);
            LazyOptional<IWallJump> wallJumpOptional=player.getCapability(IWallJump.WallJumpProvider.WALL_JUMP_CAPABILITY);
            if (!catLeapOptional.isPresent() || !crawlOptional.isPresent() || !fastRunningOptional.isPresent() || !grabCliffOptional.isPresent() || !vaultOptional.isPresent() || wallJumpOptional.isPresent())return;
            catLeap=catLeapOptional.resolve().get();
            crawl=crawlOptional.resolve().get();
            fastRunning=fastRunningOptional.resolve().get();
            grabCliff=grabCliffOptional.resolve().get();
            vault=vaultOptional.resolve().get();
            wallJump=wallJumpOptional.resolve().get();
        }
        catLeap.setReady(false);
        catLeap.setLeaping(false);
        crawl.setCrawling(false);
        crawl.setSliding(false);
        fastRunning.setFastRunning(false);
        grabCliff.setGrabbing(false);
        vault.setVaulting(false);

        SyncCatLeapMessage.sync(player);
        SyncCrawlMessage.sync(player);
        SyncGrabCliffMessage.sync(player);
        SyncFastRunningMessage.sync(player);
    }
}
