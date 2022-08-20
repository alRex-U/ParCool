package com.alrex.parcool.common.action;

import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.alrex.parcool.common.network.SyncActionStateMessage;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public abstract class Action {
	private static final ByteBuffer explicitlySyncBuffer = ByteBuffer.allocate(128);

	public abstract void onTick(Player player, Parkourability parkourability, Stamina stamina);

	@OnlyIn(Dist.CLIENT)
	public abstract void onClientTick(Player player, Parkourability parkourability, Stamina stamina);

	@OnlyIn(Dist.CLIENT)
	public abstract void onRender(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability);

	public abstract void restoreState(ByteBuffer buffer);

	/**
	 * save state needed to be synchronized
	 */
	public abstract void saveState(ByteBuffer buffer);

	protected final void synchronizeExplicitly(Player player) {
		explicitlySyncBuffer.clear();
		saveState(explicitlySyncBuffer);
		explicitlySyncBuffer.flip();

		SyncActionStateMessage.Builder builder =
				SyncActionStateMessage.Builder.sub()
						.append(this, explicitlySyncBuffer);
		SyncActionStateMessage.sync(player, builder);
	}
}
