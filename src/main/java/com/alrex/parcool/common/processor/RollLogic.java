package com.alrex.parcool.common.processor;

import com.alrex.parcool.common.capability.IRoll;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.network.StartRollMessage;
import com.alrex.parcool.common.network.SyncRollReadyMessage;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RollLogic {
	@OnlyIn(Dist.CLIENT)
	private static Vector3d rollDirection = null;

	@OnlyIn(Dist.CLIENT)
	public static void rollStart() {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null) return;

		IRoll roll;
		{
			LazyOptional<IRoll> rollOptional = player.getCapability(IRoll.RollProvider.ROLL_CAPABILITY);
			if (!rollOptional.isPresent()) return;
			roll = rollOptional.orElseThrow(NullPointerException::new);
		}
		roll.setRollReady(false);
		roll.setRolling(true);

		Vector3d lookVec = player.getLookVec();
		Vector3d motionVec = player.getMotion();
		lookVec = new Vector3d(lookVec.getX(), 0, lookVec.getZ()).normalize();
		motionVec = new Vector3d(motionVec.getX(), 0, motionVec.getZ());
		double speed = motionVec.length();
		if (speed < 0.8) speed = 0.8;
		rollDirection = lookVec.add(motionVec.normalize()).normalize().scale(speed / 1.4);
	}

	@SubscribeEvent
	public static void onTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		if (event.side != LogicalSide.CLIENT) return;
		IStamina stamina;
		IRoll roll;
		{
			LazyOptional<IStamina> staminaOptional = event.player.getCapability(IStamina.StaminaProvider.STAMINA_CAPABILITY);
			LazyOptional<IRoll> rollOptional = event.player.getCapability(IRoll.RollProvider.ROLL_CAPABILITY);
			if (!staminaOptional.isPresent() || !rollOptional.isPresent()) return;
			stamina = staminaOptional.orElseThrow(NullPointerException::new);
			roll = rollOptional.orElseThrow(NullPointerException::new);
		}
		roll.updateRollingTime();

		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != event.player) return;

		boolean oldReady = roll.isRollReady();
		if (roll.isRollReady()) {
			roll.setRollReady(roll.canContinueRollReady(player));
			stamina.consume(roll.getStaminaConsumption());
		} else roll.setRollReady(roll.canRollReady(player));

		if (roll.isRollReady() != oldReady) {
			SyncRollReadyMessage.sync(player);
		}

		if (roll.isRolling()) {
			if (rollDirection == null) return;
			rollDirection.scale(0.7);
			Vector3d motion = player.getMotion();
			player.setMotion(rollDirection.getX(), motion.getY(), rollDirection.getZ());
		}
		if (roll.getRollingTime() >= roll.getRollAnimateTime()) {
			roll.setRolling(false);
			rollDirection = null;
		}
	}

	@SubscribeEvent
	public static void onRender(RenderPlayerEvent event) {
		if (event.getPlayer() != Minecraft.getInstance().player) return;

		if (rollDirection != null) event.getPlayer().rotationYaw = (float) VectorUtil.toYawDegree(rollDirection);
	}

	@SubscribeEvent
	public static void onDamage(LivingDamageEvent event) {
		if (!(event.getEntityLiving() instanceof ServerPlayerEntity) || !event.getSource().getDamageType().equals(DamageSource.FALL.getDamageType()))
			return;
		ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();

		IRoll roll;
		{
			LazyOptional<IRoll> rollOptional = player.getCapability(IRoll.RollProvider.ROLL_CAPABILITY);
			if (!rollOptional.isPresent()) return;
			roll = rollOptional.orElseThrow(NullPointerException::new);
		}
		if (roll.isRollReady()) {
			roll.setRollReady(false);
			StartRollMessage.send(player);
			float damage = event.getAmount();
			if (damage < 2) {
				event.setCanceled(true);
			} else {
				event.setAmount((damage - 2) / 2);
			}
		}
	}
}
