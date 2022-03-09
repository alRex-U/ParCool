package com.alrex.parcool.proxy;

import com.alrex.parcool.client.gui.ParCoolGuideScreen;
import com.alrex.parcool.common.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

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
				SyncClingToCliffMessage.class,
				SyncClingToCliffMessage::encode,
				SyncClingToCliffMessage::decode,
				SyncClingToCliffMessage::handleClient
		);
		instance.registerMessage(
				9,
				SyncRollMessage.class,
				SyncRollMessage::encode,
				SyncRollMessage::decode,
				SyncRollMessage::handleClient
		);
		instance.registerMessage(
				10,
				SyncStaminaMessage.class,
				SyncStaminaMessage::encode,
				SyncStaminaMessage::decode,
				SyncStaminaMessage::handleClient
		);
		instance.registerMessage(
				11,
				DisableInfiniteStaminaMessage.class,
				DisableInfiniteStaminaMessage::encode,
				DisableInfiniteStaminaMessage::decode,
				DisableInfiniteStaminaMessage::handle
		);
		instance.registerMessage(
				12,
				ActionPermissionsMessage.class,
				ActionPermissionsMessage::encode,
				ActionPermissionsMessage::decode,
				ActionPermissionsMessage::handle
		);
		instance.registerMessage(
				13,
				AvoidDamageMessage.class,
				AvoidDamageMessage::encode,
				AvoidDamageMessage::decode,
				AvoidDamageMessage::handleClient
		);
		instance.registerMessage(
				14,
				StartVaultMessage.class,
				StartVaultMessage::encode,
				StartVaultMessage::decode,
				StartVaultMessage::handleClient
		);
	}

	@Override
	public void showParCoolGuideScreen(Player playerIn) {
		if (playerIn.level.isClientSide) {
			Minecraft.getInstance().setScreen(new ParCoolGuideScreen());
		}
	}
}
