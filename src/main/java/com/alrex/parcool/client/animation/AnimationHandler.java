package com.alrex.parcool.client.animation;

import com.alrex.parcool.common.capability.Animation;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AnimationHandler {
	@SubscribeEvent
	public void onRender(RenderPlayerEvent.Pre event) {
		Animation animation = Animation.get(event.getPlayer());
		if (animation == null) return;
		event.getMatrixStack().pushPose();
		animation.animate(event);
	}

	@SubscribeEvent
	public void onRenderPost(RenderPlayerEvent.Post event) {
		event.getMatrixStack().popPose();
		PlayerModel<AbstractClientPlayerEntity> model = event.getRenderer().getModel();
		model.rightArm.visible = true;
		model.leftArm.visible = true;
		model.leftLeg.visible = true;
		model.rightLeg.visible = true;
		model.leftSleeve.visible = true;
		model.leftPants.visible = true;
		model.rightSleeve.visible = true;
		model.rightPants.visible = true;
	}
}
