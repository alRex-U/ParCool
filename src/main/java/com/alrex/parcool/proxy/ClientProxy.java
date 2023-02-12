package com.alrex.parcool.proxy;

import com.alrex.parcool.client.gui.ParCoolGuideScreen;
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
				ActionPermissionsMessage.class,
				ActionPermissionsMessage::encode,
				ActionPermissionsMessage::decode,
				ActionPermissionsMessage::handle
		);
		instance.registerMessage(
				15,
				SyncActionStateMessage.class,
				SyncActionStateMessage::encode,
				SyncActionStateMessage::decode,
				SyncActionStateMessage::handleClient
		);
		instance.registerMessage(
				16,
				StaminaControlMessage.class,
				StaminaControlMessage::encode,
				StaminaControlMessage::decode,
				StaminaControlMessage::handleClient
		);
	}

	@Override
	public void showParCoolGuideScreen(PlayerEntity playerIn) {
		if (playerIn.level.isClientSide) {
			Minecraft.getInstance().setScreen(new ParCoolGuideScreen());
		}
	}
}
