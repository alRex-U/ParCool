package com.alrex.parcool.client;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.fabric.RenderFrameEvent;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.ViewportEvent;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * Кадровая часть ActionProcessor вынесена сюда: у NeoForge клиентские классы Minecraft лежат и на
 * выделенном сервере, у Fabric их там нет, и один только `Player p =
 * Minecraft.getInstance().player` ронял загрузку ActionProcessor на сервере (NoClassDefFoundError
 * net/minecraft/class_746).
 */
@Environment(EnvType.CLIENT)
public class ClientRenderProcessor {
    public static void onRenderTick(RenderFrameEvent.Pre event) {
        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null) return;
        for (Player player : clientPlayer.getCommandSenderWorld().players()) {
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) return;
            List<Action> actions = parkourability.getList();
            for (Action action : actions) {
                action.onRenderTick(event, player, parkourability);
            }
            Animation animation = Animation.get(player);
            if (animation == null) return;
            animation.onRenderTick(event, player, parkourability);
        }
    }

    public static void onViewRender(ViewportEvent.ComputeCameraAngles event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        Animation animation = Animation.get(player);
        if (animation == null) return;
        animation.cameraSetup(event, player, parkourability);
    }
}
