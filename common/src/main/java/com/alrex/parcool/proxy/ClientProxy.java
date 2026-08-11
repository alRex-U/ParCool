package com.alrex.parcool.proxy;

import com.alrex.parcool.api.client.skilltree.SkillTree;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.PassiveAnimationProcessor;
import com.alrex.parcool.client.animation.system.event.RegisterAnimationEntryArchEvent;
import com.alrex.parcool.client.animation.system.handle.AnimationSystemEventHandler;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.resource.AnimationResourceManager;
import com.alrex.parcool.client.architectury.event.PrepareParCoolSkillTreeArchEvent;
import com.alrex.parcool.client.gui.screen.ParCoolGuideScreen;
import com.alrex.parcool.client.gui.screen.SkillTreeScreen;
import com.alrex.parcool.client.hud.HUDRegistry;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.client.md.resource.GuideResourceManager;
import com.alrex.parcool.client.renderer.Renderers;
import com.alrex.parcool.client.renderer.entity.layers.ParCoolModelLayers;
import com.alrex.parcool.client.skilltree.ParCoolSkillTrees;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.item.ParCoolItems;
import com.alrex.parcool.common.network.*;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.LinkedList;

@Environment(EnvType.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();
        ClientLifecycleEvent.CLIENT_SETUP.register(mc -> {
            Renderers.register();
            ParCoolItems.registerColors(mc);
            AnimationSets.getInstance().freeze();
            ParCoolKeyBinds.registerAll();
            ParCoolModelLayers.register();
            ParCoolTextures.init();
            AnimationResourceManager.register();
            GuideResourceManager.register();
        });
        ClientGuiEvent.RENDER_HUD.register(HUDRegistry.getInstance()::renderHud);
        RegisterAnimationEntryArchEvent.EVENT.register(AnimationRegistries::register);

        ClientTickEvent.CLIENT_PRE.register(mc -> {
            ParCoolKeyBinds.tick();
            AnimationSystemEventHandler.onTick(mc);
        });
        ClientTickEvent.CLIENT_POST.register(mc -> {
            HUDRegistry.getInstance().onTick();
        });
        TickEvent.PLAYER_POST.register(PassiveAnimationProcessor::onTick);
        PrepareParCoolSkillTreeArchEvent.EVENT.register(ParCoolSkillTrees::onPrepareTrees);

        RegisterAnimationEntryArchEvent.EVENT.invoker().onRegisterAnimationEntries();
        RegisterAnimationEntryArchEvent.finish();
    }

    @Override
    public void registerMessages() {
        NetworkManager.registerReceiver(
                NetworkManager.clientToServer(),
                StaminaPacket.HANDLER.id(),
                StaminaPacket.HANDLER::receiveInPhysicalClient
        );
        NetworkManager.registerReceiver(
                NetworkManager.serverToClient(),
                MultiStaminaPacket.ID,
                (buf, context) -> MultiComposablePacket.receiveInPhysicalClient(MultiStaminaPacket::new, buf, context)
        );
        NetworkManager.registerReceiver(
                NetworkManager.clientToServer(),
                ActionStateSetPacket.HANDLER.id(),
                ActionStateSetPacket.HANDLER::receiveInPhysicalClient
        );
        NetworkManager.registerReceiver(
                NetworkManager.serverToClient(),
                ActionStateSetPacket.HANDLER.id(),
                ActionStateSetPacket.HANDLER::receiveInPhysicalClient
        );
        NetworkManager.registerReceiver(
                NetworkManager.serverToClient(),
                MultiActionStateSetPacket.ID,
                (buf, context) -> MultiComposablePacket.receiveInPhysicalClient(MultiActionStateSetPacket::new, buf, context)
        );
        NetworkManager.registerReceiver(
                NetworkManager.serverToClient(),
                ActionCapabilitiesPacket.HANDLER.id(),
                ActionCapabilitiesPacket.HANDLER::receiveInPhysicalClient
        );
        NetworkManager.registerReceiver(
                NetworkManager.clientToServer(),
                RequestUnlockActionPacket.HANDLER.id(),
                RequestUnlockActionPacket.HANDLER::receiveInPhysicalClient
        );
        NetworkManager.registerReceiver(
                NetworkManager.clientToServer(),
                EnableActionPacket.HANDLER.id(),
                EnableActionPacket.HANDLER::receiveInPhysicalClient
        );
    }

    @Override
    public void openSkillTreeGui(Player player) {
        var list = new LinkedList<SkillTree>();
        PrepareParCoolSkillTreeArchEvent.EVENT.invoker().onPrepareParCoolSkillTree(list);
        var parkourability = Parkourability.get(player);
        Minecraft.getInstance().setScreen(new SkillTreeScreen(
                parkourability.getCapabilities(),
                parkourability.getEnabledActions(),
                list
        ));
    }

    @Override
    public void openGuideGui() {
        Minecraft.getInstance().setScreen(new ParCoolGuideScreen(new ResourceLocation("parcool", "welcome.md")));
    }
}
