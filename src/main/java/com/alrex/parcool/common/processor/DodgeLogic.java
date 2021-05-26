package com.alrex.parcool.common.processor;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IDodge;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.network.SyncDodgeMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class DodgeLogic {
	@SubscribeEvent
	public static void onTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;
		PlayerEntity player = event.player;
		IStamina stamina = IStamina.get(player);
		IDodge dodge = IDodge.get(player);
		if (dodge == null || stamina == null) return;

		dodge.updateDodgingTime();
		if (event.side == LogicalSide.SERVER) return;

		if (!player.isUser() || !ParCool.isActive()) return;

		boolean start = dodge.canDodge(player);
		boolean oldDodging = dodge.isDodging();
		dodge.setDodging(start || (dodge.isDodging() && dodge.canContinueDodge(player)));
		if (start) {
			Vector3d vec = dodge.getAndSetDodgeDirection(player);
			if (vec != null) {
				vec = vec.scale(0.57);
				IDodge.DodgeDirection direction = dodge.getDirection();
				if (direction == IDodge.DodgeDirection.Back || direction == IDodge.DodgeDirection.Front) {
					player.setMotion(vec.getX(), 0.4, vec.getZ());
				} else {
					player.setMotion(vec.getX(), 0.23, vec.getZ());
				}
				stamina.consume(dodge.getStaminaConsumption());
			}
		}
		if (oldDodging != dodge.isDodging()) {
			SyncDodgeMessage.sync(player);
		}
	}
}
