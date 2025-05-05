package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.RollAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class Roll extends Action {
	private int creativeCoolTime = 0;
	private boolean startRequired = false;
    private static final BehaviorEnforcer.ID ID_JUMP_CANCEL = BehaviorEnforcer.newID();

	public enum Direction {Front, Back, Left, Right}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(Player player, Parkourability parkourability) {
		if (player.isLocalPlayer()) {
			if (KeyBindings.getKeyBreakfall().isDown()
                    && KeyBindings.isKeyForwardDown()
					&& !parkourability.get(Dodge.class).isDoing()
                    && !parkourability.get(Crawl.class).isDoing()
                    && !player.isVisuallyCrawling()
                    && !player.isVisuallySwimming()
					&& ParCoolConfig.Client.Booleans.EnableRollWhenCreative.get()
					&& player.isCreative()
					&& parkourability.getAdditionalProperties().getLandingTick() <= 1
					&& player.onGround()
					&& !isDoing()
					&& creativeCoolTime == 0
			) {
				startRequired = true;
				creativeCoolTime = 20;
			}
			if (creativeCoolTime > 0) creativeCoolTime--;
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}

	@Override
	public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
		LocalPlayer clientPlayer = (LocalPlayer) player;
		Direction rollDirection = Direction.Front;
		if (clientPlayer.input.getMoveVector().x < -0.5) {
			rollDirection = Direction.Right;
		} else if (clientPlayer.input.getMoveVector().x > 0.5) {
			rollDirection = Direction.Left;
		} else if (clientPlayer.input.getMoveVector().y < -0.5) {
			rollDirection = Direction.Back;
		}
		startInfo.putInt(rollDirection.ordinal());

		return startRequired;
	}

	@Override
	public boolean canContinue(Player player, Parkourability parkourability) {
		return getDoingTick() <= getRollMaxTick();
	}

	@Override
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		startRequired = false;
		Direction direction = Direction.values()[startData.getInt()];
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new RollAnimator(direction));
	}

	@Override
	public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		startRequired = false;
		Direction direction = Direction.values()[startData.getInt()];
		double modifier = Math.sqrt(player.getBbWidth());
		Vec3 vec = VectorUtil.fromYawDegree(player.yBodyRot).scale(modifier);
		switch (direction) {
			case Back:
				vec = vec.reverse();
				break;
			case Right:
				vec = vec.yRot((float) (-Math.PI / 2));
				break;
			case Left:
				vec = vec.yRot((float) (Math.PI / 2));
				break;
		}
		player.setDeltaMovement(vec.x(), 0, vec.z());
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new RollAnimator(direction));
        parkourability.getBehaviorEnforcer().addMarkerCancellingJump(ID_JUMP_CANCEL, this::isDoing);
	}

	public void startRoll(Player player) {
		startRequired = true;
	}

	public int getRollMaxTick() {
		return 9;
	}
}
