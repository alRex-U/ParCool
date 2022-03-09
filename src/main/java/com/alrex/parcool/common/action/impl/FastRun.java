package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.alrex.parcool.common.network.SyncFastRunningMessage;
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
	private static final String FAST_RUNNING_MODIFIER_NAME = "parCool.modifier.fastrunnning";
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
					&& player.isSprinting()
					&& KeyBindings.getKeyFastRunning().isDown()
					&& !stamina.isExhausted();
		}
		AttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
		if (attr == null) return;

		if (isRunning()) {
			if (!attr.hasModifier(FAST_RUNNING_MODIFIER)) attr.addTransientModifier(FAST_RUNNING_MODIFIER);
			stamina.consume(parkourability.getActionInfo().getStaminaConsumptionFastRun(), parkourability.getActionInfo());
		} else {
			if (attr.hasModifier(FAST_RUNNING_MODIFIER)) attr.removeModifier(FAST_RUNNING_MODIFIER);
		}
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {

	}

	@Override
	public boolean needSynchronization(ByteBuffer savedInstanceState) {
		return fastRunning != BufferUtil.getBoolean(savedInstanceState);
	}

	@Override
	public void sendSynchronization(Player player) {
		SyncFastRunningMessage.sync(player, this);
	}


	@Override
	public void synchronize(Object message) {
		if (message instanceof SyncFastRunningMessage) {
			SyncFastRunningMessage correctMessage = (SyncFastRunningMessage) message;
			this.fastRunning = correctMessage.isFastRunning();
		}
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
