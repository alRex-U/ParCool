package com.alrex.parcool.client.renderer;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

//only in Client
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerRenderEventHandler {
	@SubscribeEvent
	public static void onPlayerRenderPre(RenderPlayerEvent.Pre event) {
		event.getMatrixStack().push();

		PlayerDodgeRenderer.onRender(event);
		PlayerRollRenderer.onRender(event);
		PlayerGrabCliffRenderer.onRender(event);
	}

	@SubscribeEvent
	public static void onPlayerRenderPost(RenderPlayerEvent.Post event) {
		PlayerRenderer renderer = event.getRenderer();
		PlayerModel<AbstractClientPlayerEntity> model = renderer.getEntityModel();

		event.getMatrixStack().pop();
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
