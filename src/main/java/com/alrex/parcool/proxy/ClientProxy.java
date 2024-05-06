package com.alrex.parcool.proxy;

import com.alrex.parcool.common.network.*;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.simple.SimpleChannel;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
	@Override
	public boolean ParCoolIsActive() {
		return ParCoolConfig.Client.Booleans.ParCoolIsActive.get();
	}

	@Override
	public void registerMessages(SimpleChannel instance) {
		instance.registerMessage(
				3,
				StartBreakfallMessage.class,
				StartBreakfallMessage::encode,
				StartBreakfallMessage::decode,
				StartBreakfallMessage::handleClient
		);
		instance.registerMessage(
				10,
				SyncStaminaMessage.class,
				SyncStaminaMessage::encode,
				SyncStaminaMessage::decode,
				SyncStaminaMessage::handleClient
		);
		instance.registerMessage(
				12,
				SyncLimitationMessage.class,
				SyncLimitationMessage::encode,
				SyncLimitationMessage::decode,
				SyncLimitationMessage::handle
		);
		instance.registerMessage(
				15,
				SyncActionStateMessage.class,
				SyncActionStateMessage::encode,
				SyncActionStateMessage::decode,
				SyncActionStateMessage::handleClient
		);
		instance.registerMessage(
				17,
				SyncClientInformationMessage.class,
				SyncClientInformationMessage::encode,
				SyncClientInformationMessage::decode,
				SyncClientInformationMessage::handleClient
		);
	}
}
