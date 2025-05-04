package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.Animation;
import com.alrex.parcool.client.animation.impl.CrawlAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class Crawl extends Action {
	public enum ControlType {
		PressKey, Toggle
	}

	public boolean toggleStatus = false;

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        Pose pose = player.getPose();
        return isActionInvoked(player)
                && disambiguateCommands(player, pose)
                && !parkourability.isDoingAny(Roll.class, Tap.class, ClingToCliff.class, Dive.class)
				&& parkourability.get(Vault.class).getNotDoingTick() >= 8
                && !parkourability.get(HideInBlock.class).isDoing()
				&& player.getVehicle() == null
                && (pose == Pose.STANDING || pose == Pose.CROUCHING)
				&& !player.isInWaterOrBubble()
				&& !player.isFallFlying()
				&& !player.onClimbable()
				&& (player.onGround() || ParCoolConfig.Client.Booleans.EnableCrawlInAir.get());
	}

    private boolean isActionInvoked(Player player) {
        return ((ParCoolConfig.Client.CrawlControl.get() == ControlType.PressKey && KeyRecorder.keyCrawlState.isPressed())
                || (ParCoolConfig.Client.CrawlControl.get() == ControlType.Toggle && toggleStatus));
    }

    private boolean disambiguateCommands(Player player, Pose pose) {
        // If crawl and dodge are bound to the same key, we'll crawl only when crouching
        return pose == Pose.CROUCHING || !KeyRecorder.keyDodge.isPressed();
    }

    @Override
	public void onClientTick(Player player, Parkourability parkourability) {
		if (player.isLocalPlayer()) {
			if (ParCoolConfig.Client.CrawlControl.get() == Crawl.ControlType.Toggle) {
				if (KeyRecorder.keyCrawlState.isPressed())
					toggleStatus = !toggleStatus;
			} else {
				toggleStatus = false;
			}
		}
	}

	@Override
	public boolean canContinue(Player player, Parkourability parkourability) {
		switch (ParCoolConfig.Client.CrawlControl.get()) {
			case Toggle:
				if (!toggleStatus) return false;
				break;
			case PressKey:
				if (!KeyBindings.getKeyCrawl().isDown()) return false;
				break;
		}
		return !parkourability.get(Roll.class).isDoing()
				&& !parkourability.get(Tap.class).isDoing()
				&& !parkourability.get(ClingToCliff.class).isDoing()
				&& !parkourability.get(Dive.class).isDoing()
				&& parkourability.get(Vault.class).getNotDoingTick() >= 8
				&& player.getVehicle() == null
				&& !player.isInWaterOrBubble()
				&& !player.isFallFlying()
				&& !player.onClimbable()
				&& (player.onGround() || ParCoolConfig.Client.Booleans.EnableCrawlInAir.get());
	}

	@Override
	public void onWorkingTickInClient(Player player, Parkourability parkourability) {
		Animation animation = Animation.get(player);
		if (animation != null && !animation.hasAnimator()) {
			animation.setAnimator(new CrawlAnimator());
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.None;
	}

	@Override
	public void onWorkingTick(Player player, Parkourability parkourability) {
		player.setSprinting(false);
		if (player.getForcedPose() != Pose.SWIMMING) {
			player.setForcedPose(Pose.SWIMMING);
		}
	}

	@Override
	public void onStop(Player player) {
		player.setForcedPose(null);
        player.setPose(Pose.STANDING);
	}
}
