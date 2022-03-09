package com.alrex.parcool.common.action;

import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.nio.ByteBuffer;
import java.util.List;

public class ActionProcessor {
	public final ByteBuffer buffer = ByteBuffer.allocate(128);

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onTickInClient(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;
		Player player = event.player;
		Animation animation = Animation.get(player);
		if (animation == null) return;
		animation.tick();
	}

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;
		Player player = event.player;
		Parkourability parkourability = Parkourability.get(player);
		Stamina stamina = Stamina.get(player);
		if (parkourability == null || stamina == null) return;
		List<Action> actions = parkourability.getList();
		stamina.onTick();
		if (stamina.getRecoveryCoolTime() == 0) stamina.recover(stamina.getMaxStamina() / 60);
		boolean needSync = event.side == LogicalSide.CLIENT && player.isLocalPlayer();

		for (Action action : actions) {
			if (needSync) {
				buffer.clear();
				action.saveState(buffer);
				buffer.flip();
			}
			action.onTick(player, parkourability, stamina);

			if (event.side == LogicalSide.CLIENT) {
				action.onClientTick(player, parkourability, stamina);
			}
			if (needSync) {
				if (action.needSynchronization(buffer)) {
					action.sendSynchronization(player);
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		Player player = Minecraft.getInstance().player;
		if (player == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		List<Action> actions = parkourability.getList();
		for (Action action : actions) {
			action.onRender(event, player, parkourability);
		}
	}
}
