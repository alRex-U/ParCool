package com.alrex.parcool.client.renderer;

import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class PlayerRollRenderer {
	public static void onRender(RenderPlayerEvent.Pre event) {
		if (!(event.getPlayer() instanceof AbstractClientPlayerEntity)) return;
		AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) event.getPlayer();
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		Roll roll = parkourability.getRoll();

		if (roll.isRolling()) {
			ClientPlayerEntity mainPlayer = Minecraft.getInstance().player;
			if (mainPlayer == null) return;

			Vector3d lookVec = player.getLookVec().rotateYaw((float) Math.PI / 2);
			Vector3f vec = new Vector3f((float) lookVec.getX(), 0, (float) lookVec.getZ());

			event.getMatrixStack().translate(0, player.getHeight() / 2, 0);
			event.getMatrixStack().rotate(vec.rotationDegrees((roll.getRollingTick() + event.getPartialRenderTick()) * (360 / roll.getRollMaxTick())));
			event.getMatrixStack().translate(0, -player.getHeight() / 2, 0);

			PlayerRenderer renderer = event.getRenderer();
			PlayerModel<AbstractClientPlayerEntity> model = renderer.getEntityModel();

			event.getMatrixStack().push();
			Vector3d posOffset = RenderUtil.getPlayerOffset(mainPlayer, player, event.getPartialRenderTick());
			event.getMatrixStack().translate(posOffset.getX(), posOffset.getY(), posOffset.getZ());

			model.bipedRightArm.showModel = true;
			RenderUtil.rotateRightArm(player, model.bipedRightArm,
					(float) Math.toRadians(110.0F),
					(float) -Math.toRadians(player.renderYawOffset),
					(float) Math.toRadians(20.0F)
			);
			model.bipedRightArmwear.showModel = true;
			RenderUtil.rotateRightArm(player, model.bipedRightArmwear,
					(float) Math.toRadians(110.0F),
					(float) -Math.toRadians(player.renderYawOffset),
					(float) Math.toRadians(20.0F)
			);
			model.bipedLeftArm.showModel = true;
			RenderUtil.rotateLeftArm(player, model.bipedLeftArm,
					(float) Math.toRadians(110.0F),
					(float) -Math.toRadians(player.renderYawOffset),
					(float) Math.toRadians(-20.0F)
			);
			model.bipedLeftArmwear.showModel = true;
			RenderUtil.rotateLeftArm(player, model.bipedLeftArmwear,
					(float) Math.toRadians(110.0F),
					(float) -Math.toRadians(player.renderYawOffset),
					(float) Math.toRadians(-20.0F)
			);
			model.bipedLeftLeg.showModel = true;
			RenderUtil.rotateLeftLeg(player, model.bipedLeftLeg,
					(float) Math.toRadians(90.0f),
					(float) -Math.toRadians(player.renderYawOffset),
					(float) Math.toRadians(0F)
			);
			model.bipedLeftLegwear.showModel = true;
			RenderUtil.rotateLeftLeg(player, model.bipedLeftLegwear,
					(float) Math.toRadians(90.0f),
					(float) -Math.toRadians(player.renderYawOffset),
					(float) Math.toRadians(0F)
			);
			model.bipedRightLeg.showModel = true;
			RenderUtil.rotateRightLeg(player, model.bipedRightLeg,
					(float) Math.toRadians(90.0f),
					(float) -Math.toRadians(player.renderYawOffset),
					(float) Math.toRadians(0F)
			);
			model.bipedRightLegwear.showModel = true;
			RenderUtil.rotateRightLeg(player, model.bipedRightLegwear,
					(float) Math.toRadians(90.0f),
					(float) -Math.toRadians(player.renderYawOffset),
					(float) Math.toRadians(0F)
			);
			ResourceLocation location = player.getLocationSkin();
			int light = renderer.getPackedLight(player, event.getPartialRenderTick());

			renderer.getRenderManager().textureManager.bindTexture(location);
			model.bipedRightArm.render(
					event.getMatrixStack(),
					event.getBuffers().getBuffer(RenderType.getEntitySolid(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.bipedRightArmwear.render(
					event.getMatrixStack(),
					event.getBuffers().getBuffer(RenderType.getEntityTranslucent(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.bipedLeftArm.render(
					event.getMatrixStack(),
					event.getBuffers().getBuffer(RenderType.getEntitySolid(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.bipedLeftArmwear.render(
					event.getMatrixStack(),
					event.getBuffers().getBuffer(RenderType.getEntityTranslucent(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.bipedLeftLeg.render(
					event.getMatrixStack(),
					event.getBuffers().getBuffer(RenderType.getEntitySolid(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.bipedLeftLegwear.render(
					event.getMatrixStack(),
					event.getBuffers().getBuffer(RenderType.getEntityTranslucent(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.bipedRightLeg.render(
					event.getMatrixStack(),
					event.getBuffers().getBuffer(RenderType.getEntitySolid(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.bipedRightLegwear.render(
					event.getMatrixStack(),
					event.getBuffers().getBuffer(RenderType.getEntityTranslucent(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			event.getMatrixStack().pop();
			model.bipedRightArm.showModel = false;
			model.bipedRightArmwear.showModel = false;
			model.bipedLeftArm.showModel = false;
			model.bipedLeftArmwear.showModel = false;
			model.bipedLeftLeg.showModel = false;
			model.bipedLeftLegwear.showModel = false;
			model.bipedRightLeg.showModel = false;
			model.bipedRightLegwear.showModel = false;
			model.bipedRightLeg.showModel = true;
		}
	}
}
