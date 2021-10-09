package com.alrex.parcool.common.processor;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.ActionPermissions;
import com.alrex.parcool.common.capability.ICatLeap;
import com.alrex.parcool.common.capability.ICrawl;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.network.SyncCatLeapMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class JumpBoostLogic {
	@SubscribeEvent
	public static void onTick(TickEvent.PlayerTickEvent event) {
		if (event.side == LogicalSide.SERVER || event.phase != TickEvent.Phase.START) return;
		PlayerEntity player = event.player;
		ICatLeap catLeap = ICatLeap.get(player);
		IStamina stamina = IStamina.get(player);
		if (catLeap == null || stamina == null) return;

		if (!player.isUser()) return;
		if (!ActionPermissions.isAllowedCatLeap()) return;


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

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof PlayerEntity)) return;
		PlayerEntity playerEntity = (PlayerEntity) entity;

		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != entity) return;
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
