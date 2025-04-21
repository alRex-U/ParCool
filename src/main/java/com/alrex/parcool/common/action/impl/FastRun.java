package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.Animation;
import com.alrex.parcool.client.animation.impl.FastRunningAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.AdditionalProperties;
import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.compat.shoulderSurfing.ShoulderSurfingCompat;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class FastRun extends Action {
	public enum ControlType {
		PressKey, Toggle, Auto
	}

	private static final ResourceLocation FAST_RUNNING_MODIFIER = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "modifier.speed.fastrun");
	private double speedModifier = 0;
	private boolean toggleStatus = false;
	private int lastDashTick = 0;

	public double getSpeedModifier(ActionInfo info) {
        return Math.min(
                info.getClientSetting().get(ParCoolConfig.Client.Doubles.FastRunSpeedModifier),
                info.getServerLimitation().get(ParCoolConfig.Server.Doubles.MaxFastRunSpeedModifier)
        );
	}

	@Override
	public void onServerTick(Player player, Parkourability parkourability) {
		var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
		if (attr == null) return;
		if (attr.getModifier(FAST_RUNNING_MODIFIER) != null) attr.removeModifier(FAST_RUNNING_MODIFIER);
		if (isDoing()) {
			player.setSprinting(true);
			attr.addTransientModifier(new AttributeModifier(
					FAST_RUNNING_MODIFIER,
					speedModifier / 100d,
					AttributeModifier.Operation.ADD_VALUE
			));
		}
	}

	@Override
	public void onClientTick(Player player, Parkourability parkourability) {
		if (player.isLocalPlayer()) {
			if (ParCoolConfig.Client.FastRunControl.get() == ControlType.Toggle
					&& parkourability.getAdditionalProperties().getSprintingTick() > 3
			) {
				if (KeyRecorder.keyFastRunning.isPressed())
					toggleStatus = !toggleStatus;
			} else {
				toggleStatus = false;
			}
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnWorking;
	}

	@Override
	public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
		return canContinue(player, parkourability);
	}

	@Override
	public boolean canContinue(Player player, Parkourability parkourability) {
		return (!player.isInWaterOrBubble()
				&& player.getVehicle() == null
				&& !player.isFallFlying()
				&& player.isSprinting()
				&& !player.isVisuallyCrawling()
				&& !player.isSwimming()
				&& !parkourability.get(Crawl.class).isDoing()
				&& !parkourability.get(ClingToCliff.class).isDoing()
				&& !parkourability.get(HangDown.class).isDoing()
				&& ((ParCoolConfig.Client.FastRunControl.get() == ControlType.PressKey && KeyBindings.getKeyFastRunning().isDown())
				|| (ParCoolConfig.Client.FastRunControl.get() == ControlType.Toggle && toggleStatus)
				|| ParCoolConfig.Client.FastRunControl.get() == ControlType.Auto)
		);
	}

	@Override
	public void onWorkingTickInClient(Player player, Parkourability parkourability) {
		Animation animation = Animation.get(player);
		if (animation != null && !animation.hasAnimator()) {
			animation.setAnimator(new FastRunningAnimator());
		}
	}

	@Override
	public void onStartInServer(Player player, Parkourability parkourability, ByteBuffer startData) {
		speedModifier = getSpeedModifier(parkourability.getActionInfo());
	}

	@Override
	public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		super.onStartInLocalClient(player, parkourability, startData);
		ShoulderSurfingCompat.forceCoupledCamera();
	}

	@Override
	public void onStopInLocalClient(Player player) {
		Parkourability parkourability = Parkourability.get(player);
		ShoulderSurfingCompat.releaseCoupledCamera();
		if (parkourability == null) return;
		lastDashTick = getDashTick(parkourability.getAdditionalProperties());
	}

	@OnlyIn(Dist.CLIENT)
	public boolean canActWithRunning(Player player) {
		return ParCoolConfig.Client.Booleans.SubstituteSprintForFastRun.get() ? player.isSprinting() : this.isDoing();
	}

	//return sprinting tick if substitute sprint is on
	@OnlyIn(Dist.CLIENT)
	public int getDashTick(AdditionalProperties properties) {
		return ParCoolConfig.Client.Booleans.SubstituteSprintForFastRun.get() ? properties.getSprintingTick() : this.getDoingTick();
	}

	@OnlyIn(Dist.CLIENT)
	public int getNotDashTick(AdditionalProperties properties) {
		return ParCoolConfig.Client.Booleans.SubstituteSprintForFastRun.get() ? properties.getNotSprintingTick() : this.getNotDoingTick();
	}

	public int getLastDashTick() {
		return lastDashTick;
	}
}
