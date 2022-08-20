package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.FastRunningAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.alrex.parcool.utilities.BufferUtil;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;
import java.util.UUID;

public class FastRun extends Action {
	private static final String FAST_RUNNING_MODIFIER_NAME = "parcool.modifier.fastrunnning";
	private static final UUID FAST_RUNNING_MODIFIER_UUID = UUID.randomUUID();
	private static final AttributeModifier FAST_RUNNING_MODIFIER
			= new AttributeModifier(
			FAST_RUNNING_MODIFIER_UUID,
			FAST_RUNNING_MODIFIER_NAME,
			ParCoolConfig.CONFIG_CLIENT.fastRunningModifier.get() / 100d,
			AttributeModifier.Operation.ADDITION
	);

	private int runningTick = 0;
	private int notRunningTick = 0;
	private boolean fastRunning = false;

	@Override
	public void onTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (fastRunning) {
			runningTick++;
			notRunningTick = 0;
		} else {
			runningTick = 0;
			notRunningTick++;
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer()) {
			fastRunning = parkourability.getPermission().canFastRunning()
					&& !stamina.isExhausted()
					&& player.isSprinting()
					&& !player.isVisuallyCrawling()
					&& !player.isSwimming()
					&& !parkourability.getCrawl().isCrawling()
					&& (KeyBindings.getKeyFastRunning().isDown() || ParCoolConfig.CONFIG_CLIENT.replaceSprintWithFastRun.get());
		}
		AttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
		if (attr == null) return;

		if (isRunning()) {
			if (!attr.hasModifier(FAST_RUNNING_MODIFIER)) attr.addTransientModifier(FAST_RUNNING_MODIFIER);
			stamina.consume(parkourability.getActionInfo().getStaminaConsumptionFastRun(), player);

			Animation animation = Animation.get(player);
			if (animation != null && !animation.hasAnimator()) {
				animation.setAnimator(new FastRunningAnimator());
			}
		} else {
			if (attr.hasModifier(FAST_RUNNING_MODIFIER)) attr.removeModifier(FAST_RUNNING_MODIFIER);
		}
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {

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
	public boolean canActWithRunning(Player player) {
		return ParCoolConfig.CONFIG_CLIENT.substituteSprintForFastRun.get() ? player.isSprinting() : this.isRunning();
	}

	//return sprinting tick if substitute sprint is on
	@OnlyIn(Dist.CLIENT)
	public int getDashTick(AdditionalProperties properties) {
		return ParCoolConfig.CONFIG_CLIENT.substituteSprintForFastRun.get() ? properties.getSprintingTick() : this.getRunningTick();
	}
}
