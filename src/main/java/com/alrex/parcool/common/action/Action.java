package com.alrex.parcool.common.action;

import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.common.network.SyncActionStateMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public abstract class Action {
	private static ByteBuffer explicitlySyncBuffer = ByteBuffer.allocate(128);
	public abstract void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina);

	@OnlyIn(Dist.CLIENT)
	public abstract void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina);

	@OnlyIn(Dist.CLIENT)
	public abstract void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability);

	public abstract void restoreState(ByteBuffer buffer);

	/**
	 * save state needed to be synchronized
	 */
	public abstract void saveState(ByteBuffer buffer);

	protected final void synchronizeExplicitly(PlayerEntity player) {
		explicitlySyncBuffer.clear();
		saveState(explicitlySyncBuffer);
		explicitlySyncBuffer.flip();
		SyncActionStateMessage.sync(player, this, explicitlySyncBuffer);
	}
}
