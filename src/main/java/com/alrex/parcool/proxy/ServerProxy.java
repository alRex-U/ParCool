package com.alrex.parcool.proxy;

import com.alrex.parcool.common.network.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.simple.SimpleChannel;

@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerProxy extends CommonProxy {
	@Override
	public void registerMessages(SimpleChannel instance) {
		int index = 0;
		instance.messageBuilder(StaminaPacket.class, index++)
				.noResponse()
				.decoder(StaminaPacket.HANDLER::decode)
				.encoder(StaminaPacket.HANDLER::encode)
				.consumerMainThread(StaminaPacket.HANDLER::handleInPhysicalServer)
				.add();
		instance.messageBuilder(MultiStaminaPacket.class, index++)
				.noResponse()
				.decoder((packet) -> MultiStaminaPacket.decode(MultiStaminaPacket::new, packet))
				.encoder(MultiStaminaPacket::encode)
				.consumerMainThread(MultiStaminaPacket::handleInPhysicalServer)
				.add();
		instance.messageBuilder(ActionStateSetPacket.class, index++)
				.noResponse()
				.decoder(ActionStateSetPacket.HANDLER::decode)
				.encoder(ActionStateSetPacket.HANDLER::encode)
				.consumerMainThread(ActionStateSetPacket.HANDLER::handleInPhysicalServer)
				.add();
		instance.messageBuilder(MultiActionStateSetPacket.class, index++)
				.noResponse()
				.decoder((packet) -> MultiActionStateSetPacket.decode(MultiActionStateSetPacket::new, packet))
				.encoder(MultiActionStateSetPacket::encode)
				.consumerMainThread(MultiActionStateSetPacket::handleInPhysicalServer)
				.add();
        instance.messageBuilder(ActionCapabilitiesPacket.class, index++)
                .noResponse()
                .decoder(ActionCapabilitiesPacket.HANDLER::decode)
                .encoder(ActionCapabilitiesPacket.HANDLER::encode)
                .consumerMainThread(ActionCapabilitiesPacket.HANDLER::handleInPhysicalServer)
                .add();
		instance.messageBuilder(RequestUnlockActionPacket.class, index++)
				.noResponse()
				.decoder(RequestUnlockActionPacket.HANDLER::decode)
				.encoder(RequestUnlockActionPacket.HANDLER::encode)
				.consumerMainThread(RequestUnlockActionPacket.HANDLER::handleInPhysicalServer)
				.add();
	}
}
