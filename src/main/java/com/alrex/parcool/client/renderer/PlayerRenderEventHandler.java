package com.alrex.parcool.client.renderer;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IDodge;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import java.util.logging.Logger;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerRenderEventHandler {
    @SubscribeEvent
    public static void onPlayerRenderPre(RenderPlayerEvent.Pre event){
        event.getMatrixStack().push();

        PlayerDodgeRenderer.onRender(event);
    }
    @SubscribeEvent
    public static void onPlayerRenderPost(RenderPlayerEvent.Post event){
        event.getMatrixStack().pop();
    }
}
