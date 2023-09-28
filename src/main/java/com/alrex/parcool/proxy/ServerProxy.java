package com.alrex.parcool.proxy;

import com.alrex.parcool.common.network.*;
import com.alrex.parcool.extern.paraglider.SyncParagliderStaminaMessage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.simple.SimpleChannel;

@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerProxy extends CommonProxy {
	@Override
	public void registerMessages(SimpleChannel instance) {
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
				SyncLimitationMessage.class,
				SyncLimitationMessage::encode,
				SyncLimitationMessage::decode,
				null
		);
		instance.registerMessage(
				15,
				SyncActionStateMessage.class,
				SyncActionStateMessage::encode,
				SyncActionStateMessage::decode,
				SyncActionStateMessage::handleServer
		);
		instance.registerMessage(
				16,
				StaminaControlMessage.class,
				StaminaControlMessage::encode,
				StaminaControlMessage::decode,
				null
		);
		instance.registerMessage(
				17,
				SyncClientInformationMessage.class,
				SyncClientInformationMessage::encode,
				SyncClientInformationMessage::decode,
				SyncClientInformationMessage::handleServer
		);
		instance.registerMessage(
				18,
				SyncParagliderStaminaMessage.class,
				SyncParagliderStaminaMessage::encode,
				SyncParagliderStaminaMessage::decode,
				SyncParagliderStaminaMessage::handleServer
		);
	}
}
