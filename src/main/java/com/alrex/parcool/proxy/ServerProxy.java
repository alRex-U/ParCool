package com.alrex.parcool.proxy;

import com.alrex.parcool.common.network.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

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
				1,
				SetActionPossibilityMessage.class,
				SetActionPossibilityMessage::encode,
				SetActionPossibilityMessage::decode,
				SetActionPossibilityMessage::handle
		);
		instance.registerMessage(
				2,
				ShowActionPossibilityMessage.class,
				ShowActionPossibilityMessage::encode,
				ShowActionPossibilityMessage::decode,
				ShowActionPossibilityMessage::handle
		);
		instance.registerMessage(
				3,
				StartRollMessage.class,
				StartRollMessage::encode,
				StartRollMessage::decode,
				StartRollMessage::handleServer
		);
		instance.registerMessage(
				4,
				SyncCatLeapMessage.class,
				SyncCatLeapMessage::encode,
				SyncCatLeapMessage::decode,
				SyncCatLeapMessage::handleServer
		);
		instance.registerMessage(
				5,
				SyncCrawlMessage.class,
				SyncCrawlMessage::encode,
				SyncCrawlMessage::decode,
				SyncCrawlMessage::handleServer
		);
		instance.registerMessage(
				6,
				SyncDodgeMessage.class,
				SyncDodgeMessage::encode,
				SyncDodgeMessage::decode,
				SyncDodgeMessage::handleServer
		);
		instance.registerMessage(
				7,
				SyncFastRunningMessage.class,
				SyncFastRunningMessage::encode,
				SyncFastRunningMessage::decode,
				SyncFastRunningMessage::handleServer
		);
		instance.registerMessage(
				8,
				SyncClingToCliffMessage.class,
				SyncClingToCliffMessage::encode,
				SyncClingToCliffMessage::decode,
				SyncClingToCliffMessage::handleServer
		);
		instance.registerMessage(
				9,
				SyncRollMessage.class,
				SyncRollMessage::encode,
				SyncRollMessage::decode,
				SyncRollMessage::handleServer
		);
		instance.registerMessage(
				10,
				SyncStaminaMessage.class,
				SyncStaminaMessage::encode,
				SyncStaminaMessage::decode,
				SyncStaminaMessage::handleServer
		);
		instance.registerMessage(
				11,
				DisableInfiniteStaminaMessage.class,
				DisableInfiniteStaminaMessage::encode,
				DisableInfiniteStaminaMessage::decode,
				null
		);
		instance.registerMessage(
				12,
				ActionPermissionsMessage.class,
				ActionPermissionsMessage::encode,
				ActionPermissionsMessage::decode,
				null
		);
		instance.registerMessage(
				13,
				AvoidDamageMessage.class,
				AvoidDamageMessage::encode,
				AvoidDamageMessage::decode,
				null
		);
		instance.registerMessage(
				14,
				StartVaultMessage.class,
				StartVaultMessage::encode,
				StartVaultMessage::decode,
				StartVaultMessage::handleServer
		);
	}
}
