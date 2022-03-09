package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.alrex.parcool.common.network.SyncCrawlMessage;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.EntityUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class Crawl extends Action {
	private int slidingTick = 0;
	private Vec3 slidingVec = null;
	private boolean crawling = false;
	private boolean sliding = false;

	@Override
	public void onTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (sliding) slidingTick++;
		else slidingTick = 0;
		if (crawling || sliding) {
			player.setSprinting(false);
			player.setPose(Pose.SWIMMING);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer()) {
			if (
					parkourability.getPermission().canCrawl()
							&& !crawling
							&& KeyRecorder.keyCrawlState.isPressed()
							&& !parkourability.getRoll().isRolling()
							&& !player.isInWaterOrBubble()
							&& player.isOnGround()
			) {
				//sliding
				if (parkourability.getFastRun().getDashTick(parkourability.getAdditionalProperties()) > 5) {
					sliding = true;
					Vec3 lookVec = player.getLookAngle();
					slidingVec = new Vec3(lookVec.x(), 0, lookVec.z()).normalize();
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
				if (player.isOnGround()) {
					Vec3 vec = slidingVec.scale(0.2);
					EntityUtil.addVelocity(player, vec);
				}
			}
			if (slidingTick >= parkourability.getActionInfo().getMaxSlidingTick()) {
				sliding = false;
				slidingTick = 0;
				crawling = true;
				slidingVec = null;
			}
			if (crawling && !KeyBindings.getKeyCrawl().isDown()) {
				crawling = false;
				sliding = false;
				slidingVec = null;
			}
		}
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {
		if (slidingVec == null || !sliding) return;
		player.setYRot((float) VectorUtil.toYawDegree(slidingVec));
	}

	@Override
	public boolean needSynchronization(ByteBuffer savedInstanceState) {
		return this.crawling != BufferUtil.getBoolean(savedInstanceState)
				|| this.sliding != BufferUtil.getBoolean(savedInstanceState);
	}


	@Override
	public void sendSynchronization(Player player) {
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
