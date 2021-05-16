package com.alrex.parcool.common.processor;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IGrabCliff;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.network.ResetFallDistanceMessage;
import com.alrex.parcool.common.network.SyncGrabCliffMessage;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GrabCliffLogic {
	@SubscribeEvent
	public static void onTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		if (event.side == LogicalSide.SERVER) return;
		PlayerEntity entity = event.player;
		IStamina stamina;
		IGrabCliff grabCliff;
		{
			LazyOptional<IGrabCliff> grabCliffOptional = entity.getCapability(IGrabCliff.GrabCliffProvider.GRAB_CLIFF_CAPABILITY);
			LazyOptional<IStamina> staminaOptional = entity.getCapability(IStamina.StaminaProvider.STAMINA_CAPABILITY);
			if (!staminaOptional.isPresent() || !grabCliffOptional.isPresent()) return;
			stamina = staminaOptional.orElseThrow(NullPointerException::new);
			grabCliff = grabCliffOptional.orElseThrow(NullPointerException::new);
		}
		grabCliff.updateTime();

		if (event.player != Minecraft.getInstance().player) return;
		if (!ParCool.isActive()) return;
		ClientPlayerEntity player = Minecraft.getInstance().player;

		boolean oldGrabbing = grabCliff.isGrabbing();
		grabCliff.setGrabbing(grabCliff.canGrabCliff(player));

		if (oldGrabbing != grabCliff.isGrabbing()) {
			SyncGrabCliffMessage.sync(player);
			ResetFallDistanceMessage.sync(player);
		}

		if (grabCliff.isGrabbing()) {
			Vector3d vec = player.getMotion();
			player.setMotion(vec.getX() / 10, vec.getY() > 0.1 ? vec.getY() / 10 : 0, vec.getZ() / 10);
			stamina.consume(grabCliff.getStaminaConsumptionGrab());
		}
		if (grabCliff.canJumpOnCliff(player)) {
			player.addVelocity(0, 0.6, 0);
			stamina.consume(grabCliff.getStaminaConsumptionClimbUp());
		}
	}

	@SubscribeEvent
	public static void onRender(TickEvent.RenderTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null) return;
		if (!ParCool.isActive()) return;

		LazyOptional<IGrabCliff> grabCliffOptional = player.getCapability(IGrabCliff.GrabCliffProvider.GRAB_CLIFF_CAPABILITY);
		if (!grabCliffOptional.isPresent()) return;
		IGrabCliff grabCliff = grabCliffOptional.orElseThrow(NullPointerException::new);

		if (grabCliff.isGrabbing()) {
			Vector3d wall = WorldUtil.getWall(player);
			Vector3d look = player.getLookVec();
			if (wall != null)
				player.rotationYaw = (float) VectorUtil.toYawDegree(wall.normalize().add(look.normalize()));
		}
	}
}
