package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.FastRunningAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.AdditionalProperties;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;
import java.util.UUID;

public class FastRun extends Action {
	public enum ControlType {
		PressKey, Toggle, Auto
	}

	private static final String FAST_RUNNING_MODIFIER_NAME = "parcool.modifier.fastrunnning";
	private static final UUID FAST_RUNNING_MODIFIER_UUID = UUID.randomUUID();
	private double speedModifier = 0;
	private boolean toggleStatus = false;
	private int lastDashTick = 0;

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
	public void onClientTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
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
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		return canContinue(player, parkourability, stamina);
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		return (!stamina.isExhausted()
				&& player.isSprinting()
				&& !player.isVisuallyCrawling()
				&& !player.isSwimming()
				&& !parkourability.get(Crawl.class).isDoing()
				&& ((ParCoolConfig.Client.FastRunControl.get() == ControlType.PressKey && KeyBindings.getKeyFastRunning().isDown())
				|| (ParCoolConfig.Client.FastRunControl.get() == ControlType.Toggle && toggleStatus)
				|| ParCoolConfig.Client.FastRunControl.get() == ControlType.Auto)
		);
	}

	@Override
	public void onWorkingTickInClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		Animation animation = Animation.get(player);
		if (animation != null && !animation.hasAnimator()) {
			animation.setAnimator(new FastRunningAnimator());
		}
	}

	@Override
	public void onStartInServer(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		speedModifier = parkourability.getClientInfo().get(ParCoolConfig.Client.Doubles.FastRunSpeedModifier);
	}

	@Override
	public void onStopInLocalClient(PlayerEntity player) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		lastDashTick = getDashTick(parkourability.getAdditionalProperties());
	}

	@OnlyIn(Dist.CLIENT)
	public boolean canActWithRunning(PlayerEntity player) {
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
