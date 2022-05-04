package com.alrex.parcool.client.animation;

import com.alrex.parcool.common.capability.impl.Animation;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AnimationHandler {
	boolean visibilityLeftArm = false;
	boolean visibilityRightArm = false;
	boolean visibilityLeftLeg = false;
	boolean visibilityRightLeg = false;
	boolean visibilityLeftSleeve = false;
	boolean visibilityRightSleeve = false;
	boolean visibilityLeftPants = false;
	boolean visibilityRightPants = false;

	@SubscribeEvent
	public void onRender(RenderPlayerEvent.Pre event) {
		Animation animation = Animation.get(event.getPlayer());
		if (animation == null) return;
		PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();
		visibilityLeftArm = model.leftArm.visible;
		visibilityRightArm = model.rightArm.visible;
		visibilityLeftLeg = model.leftLeg.visible;
		visibilityRightLeg = model.rightLeg.visible;
		visibilityLeftPants = model.leftPants.visible;
		visibilityRightPants = model.rightPants.visible;
		visibilityLeftSleeve = model.leftSleeve.visible;
		visibilityRightSleeve = model.rightSleeve.visible;
		event.getPoseStack().pushPose();
		animation.animate(event);
	}

	@SubscribeEvent
	public void onRenderPost(RenderPlayerEvent.Post event) {
		event.getPoseStack().popPose();
		PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();
		model.rightArm.visible = visibilityRightArm;
		model.leftArm.visible = visibilityLeftArm;
		model.leftLeg.visible = visibilityLeftLeg;
		model.rightLeg.visible = visibilityRightLeg;
		model.leftSleeve.visible = visibilityLeftSleeve;
		model.leftPants.visible = visibilityLeftPants;
		model.rightSleeve.visible = visibilityRightSleeve;
		model.rightPants.visible = visibilityRightPants;
	}
}
