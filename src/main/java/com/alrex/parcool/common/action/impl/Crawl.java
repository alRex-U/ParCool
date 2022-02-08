package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.common.network.SyncCrawlMessage;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class Crawl extends Action {
	private int slidingTick = 0;
	private Vector3d slidingVec = null;
	private boolean crawling = false;
	private boolean sliding = false;

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (sliding) slidingTick++;
		else slidingTick = 0;
		if (crawling || sliding) {
			player.setSprinting(false);
			player.setPose(Pose.SWIMMING);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (player.isUser()) {
			if (
					parkourability.getPermission().canCrawl()
							&& !crawling
							&& KeyRecorder.keyCrawlState.isPressed()
							&& !parkourability.getRoll().isRolling()
							&& !player.isInWaterOrBubbleColumn()
							&& player.collidedVertically
			) {
				//sliding
				if (parkourability.getFastRun().getDashTick(parkourability.getAdditionalProperties()) > 5) {
					sliding = true;
					Vector3d lookVec = player.getLookVec();
					slidingVec = new Vector3d(lookVec.getX(), 0, lookVec.getZ()).normalize();
				}
				//crawl
				else {
					sliding = false;
					slidingVec = null;
				}
				crawling = true;
			}
			if (slidingVec == null) {
				sliding = false;
			}
			if (sliding) {
				if (player.collidedVertically) {
					Vector3d vec = slidingVec.scale(0.2);
					player.addVelocity(vec.getX(), vec.getY(), vec.getZ());
				}
			}
			if (slidingTick >= parkourability.getActionInfo().getMaxSlidingTick()) {
				sliding = false;
				slidingTick = 0;
				crawling = true;
				slidingVec = null;
			}
			if (crawling && !KeyBindings.getKeyCrawl().isKeyDown()) {
				crawling = false;
				sliding = false;
				slidingVec = null;
			}
		}
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
		if (slidingVec == null || !sliding) return;
		player.rotationYaw = (float) VectorUtil.toYawDegree(slidingVec);
	}

	@Override
	public boolean needSynchronization(ByteBuffer savedInstanceState) {
		return this.crawling != BufferUtil.getBoolean(savedInstanceState)
				|| this.sliding != BufferUtil.getBoolean(savedInstanceState);
	}


	@Override
	public void sendSynchronization(PlayerEntity player) {
		SyncCrawlMessage.sync(player, this);
	}

	@Override
	public void synchronize(Object message) {
		if (message instanceof SyncCrawlMessage) {
			this.sliding = ((SyncCrawlMessage) message).isSliding();
			this.crawling = ((SyncCrawlMessage) message).isCrawling();
		}
	}

	@Override
	public void saveState(ByteBuffer buffer) {
		BufferUtil.wrap(buffer)
				.putBoolean(crawling)
				.putBoolean(sliding);
	}

	public boolean isCrawling() {
		return crawling;
	}

	public boolean isSliding() {
		return sliding;
	}
}
