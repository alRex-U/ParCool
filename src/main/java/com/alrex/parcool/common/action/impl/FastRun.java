package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.FastRunningAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.utilities.BufferUtil;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;
import java.util.UUID;

public class FastRun extends Action {
	private static final String FAST_RUNNING_MODIFIER_NAME = "parcool.modifier.fastrunnning";
	private static final UUID FAST_RUNNING_MODIFIER_UUID = UUID.randomUUID();
	private int runningTick = 0;
	private int notRunningTick = 0;
	private boolean fastRunning = false;

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (fastRunning) {
			runningTick++;
			notRunningTick = 0;
		} else {
			runningTick = 0;
			notRunningTick++;
		}

		if (player.level.isClientSide()) return;

		ModifiableAttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
		if (attr == null) return;
		if (attr.getModifier(FAST_RUNNING_MODIFIER_UUID) != null) attr.removeModifier(FAST_RUNNING_MODIFIER_UUID);
		if (fastRunning) {
			attr.addTransientModifier(new AttributeModifier(
					FAST_RUNNING_MODIFIER_UUID,
					FAST_RUNNING_MODIFIER_NAME,
					ParCoolConfig.CONFIG_SERVER.fastRunningModifier.get() / 100d,
					AttributeModifier.Operation.ADDITION
			));
			stamina.consume(parkourability.getActionInfo().getStaminaConsumptionFastRun(), player);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer()) {
			fastRunning = parkourability.getPermission().canFastRunning()
					&& !stamina.isExhausted()
					&& player.isSprinting()
					&& !player.isVisuallyCrawling()
					&& !player.isSwimming()
					&& !parkourability.getCrawl().isCrawling()
					&& (KeyBindings.getKeyFastRunning().isDown() || ParCoolConfig.CONFIG_CLIENT.replaceSprintWithFastRun.get());
		}
		if (isRunning()) {
			Animation animation = Animation.get(player);
			if (animation != null && !animation.hasAnimator()) {
				animation.setAnimator(new FastRunningAnimator());
			}
		}
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {

	}


	@Override
	public void restoreState(ByteBuffer buffer) {
		fastRunning = BufferUtil.getBoolean(buffer);
	}

	@Override
	public void saveState(ByteBuffer buffer) {
		BufferUtil.wrap(buffer).putBoolean(fastRunning);
	}

	public int getRunningTick() {
		return runningTick;
	}

	public int getNotRunningTick() {
		return notRunningTick;
	}

	public boolean isRunning() {
		return fastRunning;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean canActWithRunning(PlayerEntity player) {
		return ParCoolConfig.CONFIG_CLIENT.substituteSprintForFastRun.get() ? player.isSprinting() : this.isRunning();
	}

	//return sprinting tick if substitute sprint is on
	@OnlyIn(Dist.CLIENT)
	public int getDashTick(AdditionalProperties properties) {
		return ParCoolConfig.CONFIG_CLIENT.substituteSprintForFastRun.get() ? properties.getSprintingTick() : this.getRunningTick();
	}

	@OnlyIn(Dist.CLIENT)
	public int getNotDashTick(AdditionalProperties properties) {
		return ParCoolConfig.CONFIG_CLIENT.substituteSprintForFastRun.get() ? properties.getNotSprintingTick() : this.getNotRunningTick();
	}
}
