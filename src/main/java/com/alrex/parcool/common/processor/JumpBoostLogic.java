package com.alrex.parcool.common.processor;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.ICatLeap;
import com.alrex.parcool.common.capability.ICrawl;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.network.SyncCatLeapMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class JumpBoostLogic {
	@SubscribeEvent
	public static void onTick(TickEvent.PlayerTickEvent event) {
		if (!event.player.world.isRemote || event.phase != TickEvent.Phase.START) return;
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != event.player) return;

		ICatLeap catLeap = ICatLeap.get(player);
		IStamina stamina = IStamina.get(player);
		if (catLeap == null || stamina == null) return;

		catLeap.updateReadyTime();

		boolean oldLeaping = catLeap.isLeaping();
		if (catLeap.canCatLeap(player)) {
			Vector3d motionVec = player.getMotion();
			Vector3d vec = new Vector3d(motionVec.getX(), 0, motionVec.getZ()).normalize();
			player.setMotion(vec.getX(), catLeap.getBoostValue(player), vec.getZ());
			stamina.consume(catLeap.getStaminaConsumption());
			catLeap.setLeaping(true);
			catLeap.setReady(false);
		} else if (catLeap.isLeaping() && (player.collidedHorizontally || player.collidedVertically || player.isInWaterOrBubbleColumn())) {
			catLeap.setLeaping(false);
		}
		catLeap.setReady(catLeap.canReadyLeap(player));
		if (oldLeaping != catLeap.isLeaping()) SyncCatLeapMessage.sync(player);
	}

	@SubscribeEvent
	public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
		if (!event.getEntityLiving().world.isRemote) return;

		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != event.getEntityLiving()) return;
		if (!ParCool.isActive()) return;

		ICrawl crawl = ICrawl.get(player);
		IStamina stamina = IStamina.get(player);
		if (crawl == null || stamina == null) return;

		if (stamina.isExhausted()) {
			Vector3d vec = player.getMotion();
			player.setMotion(vec.getX(), 0.3, vec.getZ());
			return;
		}
		if (crawl.isSliding()) {
			Vector3d vec = player.getMotion();
			player.setMotion(vec.getX(), 0, vec.getZ());
		}
	}
}
