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
		event.getMatrixStack().push();
		animation.animate(event);
	}

	@SubscribeEvent
	public void onRenderPost(RenderPlayerEvent.Post event) {
		event.getMatrixStack().pop();
		PlayerModel<AbstractClientPlayerEntity> model = event.getRenderer().getEntityModel();
		model.bipedRightArm.showModel = true;
		model.bipedLeftArm.showModel = true;
		model.bipedLeftLeg.showModel = true;
		model.bipedRightLeg.showModel = true;
		model.bipedLeftArmwear.showModel = true;
		model.bipedRightArmwear.showModel = true;
		model.bipedLeftLegwear.showModel = true;
		model.bipedRightLegwear.showModel = true;
	}
}
