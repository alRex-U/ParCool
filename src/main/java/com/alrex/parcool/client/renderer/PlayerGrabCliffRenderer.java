package com.alrex.parcool.client.renderer;

import com.alrex.parcool.common.capability.IGrabCliff;
import com.alrex.parcool.utilities.RenderUtil;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerGrabCliffRenderer {
    public static void onRender(RenderPlayerEvent.Pre event) {
        if (!(event.getPlayer() instanceof AbstractClientPlayerEntity)) return;
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) event.getPlayer();

        IGrabCliff grabCliff;
        {
            LazyOptional<IGrabCliff> grabCliffOptional = player.getCapability(IGrabCliff.GrabCliffProvider.GRAB_CLIFF_CAPABILITY);
            if (!grabCliffOptional.isPresent()) return;
            grabCliff = grabCliffOptional.resolve().get();
        }
        if (grabCliff.isGrabbing()) {
            PlayerRenderer renderer = event.getRenderer();
            PlayerModel<AbstractClientPlayerEntity> model = renderer.getEntityModel();

            model.bipedRightArm.showModel = true;
            RenderUtil.rotateRightArm(player, model.bipedRightArm,
                    (float) Math.toRadians(20.0F),
                    (float) -Math.toRadians(player.renderYawOffset),
                    (float) Math.toRadians(0.0F)
            );
            model.bipedRightArmwear.showModel = true;
            RenderUtil.rotateRightArm(player, model.bipedRightArmwear,
                    (float) Math.toRadians(20.0F),
                    (float) -Math.toRadians(player.renderYawOffset),
                    (float) Math.toRadians(0.0F)
            );
            model.bipedLeftArm.showModel = true;
            RenderUtil.rotateLeftArm(player, model.bipedLeftArm,
                    (float) Math.toRadians(20.0F),
                    (float) -Math.toRadians(player.renderYawOffset),
                    (float) Math.toRadians(0.0F)
            );
            model.bipedLeftArmwear.showModel = true;
            RenderUtil.rotateLeftArm(player, model.bipedLeftArmwear,
                    (float) Math.toRadians(20.0F),
                    (float) -Math.toRadians(player.renderYawOffset),
                    (float) Math.toRadians(0.0F)
            );
            ResourceLocation location = renderer.getEntityTexture(player);
            renderer.getRenderManager().textureManager.bindTexture(location);
            model.bipedRightArm.render(
                    event.getMatrixStack(),
                    event.getBuffers().getBuffer(RenderType.getEntitySolid(location)),
                    renderer.getPackedLight(player, event.getPartialRenderTick()),
                    0
            );
            model.bipedRightArmwear.render(
                    event.getMatrixStack(),
                    event.getBuffers().getBuffer(RenderType.getArmorEntityGlint()),
                    renderer.getPackedLight(player, event.getPartialRenderTick()),
                    0
            );
            model.bipedLeftArm.render(
                    event.getMatrixStack(),
                    event.getBuffers().getBuffer(RenderType.getEntitySolid(location)),
                    renderer.getPackedLight(player, event.getPartialRenderTick()),
                    0
            );
            model.bipedLeftArmwear.render(
                    event.getMatrixStack(),
                    event.getBuffers().getBuffer(RenderType.getArmorEntityGlint()),
                    renderer.getPackedLight(player, event.getPartialRenderTick()),
                    0
            );
            model.bipedRightArm.showModel = false;
            model.bipedRightArmwear.showModel = false;
            model.bipedLeftArm.showModel = false;
            model.bipedLeftArmwear.showModel = false;
        }
    }
}
