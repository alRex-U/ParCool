package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.FastRunningAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.AdditionalProperties;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;
import java.util.UUID;

public class FastRun extends Action {
	private static final String FAST_RUNNING_MODIFIER_NAME = "parcool.modifier.fastrunnning";
	private static final UUID FAST_RUNNING_MODIFIER_UUID = UUID.randomUUID();
	private double speedModifier = 0;

	@Override
	public void onServerTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		ModifiableAttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
		if (attr == null) return;
		if (attr.getModifier(FAST_RUNNING_MODIFIER_UUID) != null) attr.removeModifier(FAST_RUNNING_MODIFIER_UUID);
		if (isDoing()) {
			player.setSprinting(true);
			attr.addTransientModifier(new AttributeModifier(
					FAST_RUNNING_MODIFIER_UUID,
					FAST_RUNNING_MODIFIER_NAME,
					speedModifier / 100d,
					AttributeModifier.Operation.ADDITION
			));
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnWorking;
	}

	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		startInfo.putDouble(ParCoolConfig.CONFIG_CLIENT.fastRunningModifier.get());
		return canContinue(player, parkourability, stamina);
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		return (parkourability.getActionInfo().can(FastRun.class)
				&& !stamina.isExhausted()
				&& player.isSprinting()
				&& !player.isVisuallyCrawling()
				&& !player.isSwimming()
				&& !parkourability.get(Crawl.class).isDoing()
				&& (KeyBindings.getKeyFastRunning().isDown() || ParCoolConfig.CONFIG_CLIENT.replaceSprintWithFastRun.get())
		);
	}

	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		Animation animation = Animation.get(player);
		if (animation != null && !animation.hasAnimator()) {
			animation.setAnimator(new FastRunningAnimator());
		}
	}

	@Override
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		Animation animation = Animation.get(player);
		if (animation != null && !animation.hasAnimator()) {
			animation.setAnimator(new FastRunningAnimator());
		}
	}

	@Override
	public void onStartInServer(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		speedModifier = startData.getDouble();
	}

	@OnlyIn(Dist.CLIENT)
	public boolean canActWithRunning(PlayerEntity player) {
		return ParCoolConfig.CONFIG_CLIENT.substituteSprintForFastRun.get() ? player.isSprinting() : this.isDoing();
	}

	//return sprinting tick if substitute sprint is on
	@OnlyIn(Dist.CLIENT)
	public int getDashTick(AdditionalProperties properties) {
		return ParCoolConfig.CONFIG_CLIENT.substituteSprintForFastRun.get() ? properties.getSprintingTick() : this.getDoingTick();
	}

	@OnlyIn(Dist.CLIENT)
	public int getNotDashTick(AdditionalProperties properties) {
		return ParCoolConfig.CONFIG_CLIENT.substituteSprintForFastRun.get() ? properties.getNotSprintingTick() : this.getNotDoingTick();
	}
}
