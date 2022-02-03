package com.alrex.parcool.common.action;

import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.nio.ByteBuffer;
import java.util.List;

public class ActionProcessor {
	public final ByteBuffer buffer = ByteBuffer.allocate(128);

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;
		PlayerEntity player = event.player;
		Parkourability parkourability = Parkourability.get(player);
		Stamina stamina = Stamina.get(player);
		Animation animation = Animation.get(player);
		if (parkourability == null || stamina == null || animation == null) return;
		List<Action> actions = parkourability.getList();
		stamina.onTick();
		if (stamina.getRecoveryCoolTime() == 0) stamina.recover(stamina.getMaxStamina() / 60);
		animation.tick();
		boolean needSync = event.side == LogicalSide.CLIENT && player.isUser();

		for (Action action : actions) {
			if (needSync) {
				buffer.clear();
				action.saveState(buffer);
				buffer.flip();
			}
			action.onTick(player, parkourability, stamina);

			if (needSync) {
				action.onClientTick(player, parkourability, stamina);
				if (action.needSynchronization(buffer)) {
					action.sendSynchronization(player);
				}
			}
		}
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		PlayerEntity player = Minecraft.getInstance().player;
		if (player == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		List<Action> actions = parkourability.getList();
		for (Action action : actions) {
			action.onRender(event, player, parkourability);
		}
	}
}
