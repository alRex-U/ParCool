package com.alrex.parcool.proxy;

import com.alrex.parcool.common.item.gui.ParCoolGuideScreen;
import com.alrex.parcool.common.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
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
				StartRollMessage::handleClient
		);
		instance.registerMessage(
				4,
				SyncCatLeapMessage.class,
				SyncCatLeapMessage::encode,
				SyncCatLeapMessage::decode,
				SyncCatLeapMessage::handleClient
		);
		instance.registerMessage(
				5,
				SyncCrawlMessage.class,
				SyncCrawlMessage::encode,
				SyncCrawlMessage::decode,
				SyncCrawlMessage::handleClient
		);
		instance.registerMessage(
				6,
				SyncDodgeMessage.class,
				SyncDodgeMessage::encode,
				SyncDodgeMessage::decode,
				SyncDodgeMessage::handleClient
		);
		instance.registerMessage(
				7,
				SyncFastRunningMessage.class,
				SyncFastRunningMessage::encode,
				SyncFastRunningMessage::decode,
				SyncFastRunningMessage::handleClient
		);
		instance.registerMessage(
				8,
				SyncGrabCliffMessage.class,
				SyncGrabCliffMessage::encode,
				SyncGrabCliffMessage::decode,
				SyncGrabCliffMessage::handleClient
		);
		instance.registerMessage(
				9,
				SyncRollReadyMessage.class,
				SyncRollReadyMessage::encode,
				SyncRollReadyMessage::decode,
				SyncRollReadyMessage::handleClient
		);
		instance.registerMessage(
				10,
				SyncStaminaMessage.class,
				SyncStaminaMessage::encode,
				SyncStaminaMessage::decode,
				SyncStaminaMessage::handleClient
		);
	}

	@Override
	public void showParCoolGuideScreen(PlayerEntity playerIn) {
		Minecraft.getInstance().displayGuiScreen(new ParCoolGuideScreen());
	}
}
