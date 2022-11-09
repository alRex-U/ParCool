package com.alrex.parcool.proxy;

import com.alrex.parcool.common.network.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerProxy extends CommonProxy {
	@Override
	public void registerMessages(SimpleChannel instance) {
		instance.registerMessage(
				0,
				ResetFallDistanceMessage.class,
				ResetFallDistanceMessage::encode,
				ResetFallDistanceMessage::decode,
				ResetFallDistanceMessage::handle
		);
		instance.registerMessage(
				3,
				StartBreakfallMessage.class,
				StartBreakfallMessage::encode,
				StartBreakfallMessage::decode,
				StartBreakfallMessage::handleServer
		);
		instance.registerMessage(
				10,
				SyncStaminaMessage.class,
				SyncStaminaMessage::encode,
				SyncStaminaMessage::decode,
				SyncStaminaMessage::handleServer
		);
		instance.registerMessage(
				12,
				ActionPermissionsMessage.class,
				ActionPermissionsMessage::encode,
				ActionPermissionsMessage::decode,
				null
		);
		instance.registerMessage(
				14,
				StartVaultMessage.class,
				StartVaultMessage::encode,
				StartVaultMessage::decode,
				StartVaultMessage::handleServer
		);
		instance.registerMessage(
				15,
				SyncActionStateMessage.class,
				SyncActionStateMessage::encode,
				SyncActionStateMessage::decode,
				SyncActionStateMessage::handleServer
		);
	}
}
