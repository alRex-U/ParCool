package com.alrex.parcool.proxy;

import com.alrex.parcool.common.network.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.SimpleChannel;

@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerProxy extends CommonProxy {
	@Override
	public void registerMessages(SimpleChannel instance) {
        instance
                .messageBuilder(StartBreakfallMessage.class)
                .encoder(StartBreakfallMessage::encode)
                .decoder(StartBreakfallMessage::decode)
                .consumerNetworkThread(StartBreakfallMessage::handleServer)
                .add();
        instance
                .messageBuilder(SyncStaminaMessage.class)
                .encoder(SyncStaminaMessage::encode)
                .decoder(SyncStaminaMessage::decode)
                .consumerNetworkThread(SyncStaminaMessage::handleServer)
                .add();
        instance
                .messageBuilder(SyncLimitationMessage.class)
                .encoder(SyncLimitationMessage::encode)
                .decoder(SyncLimitationMessage::decode)
                //.consumerNetworkThread(null)
                .add();
        instance
                .messageBuilder(SyncActionStateMessage.class)
                .encoder(SyncActionStateMessage::encode)
                .decoder(SyncActionStateMessage::decode)
                .consumerNetworkThread(SyncActionStateMessage::handleServer)
                .add();
        instance
                .messageBuilder(StaminaControlMessage.class)
                .encoder(StaminaControlMessage::encode)
                .decoder(StaminaControlMessage::decode)
                //.consumerNetworkThread(null)
                .add();
        instance
                .messageBuilder(SyncClientInformationMessage.class)
                .encoder(SyncClientInformationMessage::encode)
                .decoder(SyncClientInformationMessage::decode)
                .consumerNetworkThread(SyncClientInformationMessage::handleServer)
                .add();
	}
}
