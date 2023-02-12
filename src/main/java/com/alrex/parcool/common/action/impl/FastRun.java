package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.FastRunningAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
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

	@Override
	public void onServerTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		ModifiableAttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
		if (attr == null) return;
		if (attr.getModifier(FAST_RUNNING_MODIFIER_UUID) != null) attr.removeModifier(FAST_RUNNING_MODIFIER_UUID);
		if (isDoing()) {
			attr.addTransientModifier(new AttributeModifier(
					FAST_RUNNING_MODIFIER_UUID,
					FAST_RUNNING_MODIFIER_NAME,
					ParCoolConfig.CONFIG_SERVER.fastRunningModifier.get() / 100d,
					AttributeModifier.Operation.ADDITION
			));
		}
	}

	@Override
	public void onWorkingTickInClient(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		stamina.consume(parkourability.getActionInfo().getStaminaConsumptionFastRun(), player);
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {

	}

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {

	}

	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startInfo) {
		return canContinue(player, parkourability, stamina);
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		return (parkourability.getPermission().canFastRunning()
				&& !stamina.isExhausted()
				&& player.isSprinting()
				&& !player.isVisuallyCrawling()
				&& !player.isSwimming()
				&& !parkourability.getCrawl().isDoing()
				&& (KeyBindings.getKeyFastRunning().isDown() || ParCoolConfig.CONFIG_CLIENT.replaceSprintWithFastRun.get())
		);
	}

	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startData) {
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
