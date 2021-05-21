package com.alrex.parcool.common.processor;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.ICrawl;
import com.alrex.parcool.common.network.SyncCrawlMessage;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CrawlLogic {
	//only in Client
	private static Vector3d slidingVec = null;

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		PlayerEntity player = event.player;

		LazyOptional<ICrawl> crawlOptional = player.getCapability(ICrawl.CrawlProvider.CRAWL_CAPABILITY);
		if (!crawlOptional.isPresent()) return;
		ICrawl crawl = crawlOptional.orElseThrow(NullPointerException::new);

		if (crawl.isCrawling() || crawl.isSliding()) {
			player.setPose(Pose.SWIMMING);
		}
		if (crawl.isCrawling()) {
			player.setSprinting(false);
		}

		if (!event.player.world.isRemote) return;


		if (!ParCool.isActive()) return;
		ClientPlayerEntity playerClient = Minecraft.getInstance().player;
		if (playerClient != player || event.phase != TickEvent.Phase.START) return;

		boolean oldCrawling = crawl.isCrawling();
		crawl.setCrawling(crawl.canCrawl(playerClient));
		boolean oldSliding = crawl.isSliding();
		crawl.setSliding(crawl.canSliding(playerClient));
		crawl.updateSlidingTime(playerClient);

		if (crawl.isCrawling() != oldCrawling || crawl.isSliding() != oldSliding) {
			SyncCrawlMessage.sync(playerClient);
		}
		if (!oldSliding && crawl.isSliding()) {
			Vector3d vec = player.getMotion();
			slidingVec = new Vector3d(vec.getX(), 0, vec.getZ()).scale(3.0);
		}
		if (crawl.isSliding()) {
			if (playerClient.collidedVertically) player.setMotion(slidingVec);
			slidingVec = slidingVec.scale(0.9);
		}
		if (crawl.isSliding()) {
			player.rotationYaw = (float) (Math.atan2(slidingVec.getZ(), slidingVec.getX()) * 180.0 / Math.PI - 90.0);
		}
	}

	@SubscribeEvent
	public static void onRender(TickEvent.RenderTickEvent event) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null || !ParCool.isActive()) return;

		LazyOptional<ICrawl> crawlOptional = player.getCapability(ICrawl.CrawlProvider.CRAWL_CAPABILITY);
		if (!crawlOptional.isPresent()) return;
		ICrawl crawl = crawlOptional.orElseThrow(NullPointerException::new);

		if (crawl.isSliding()) {
			player.rotationYaw = (float) VectorUtil.toYawDegree(slidingVec);
		}
	}
}
