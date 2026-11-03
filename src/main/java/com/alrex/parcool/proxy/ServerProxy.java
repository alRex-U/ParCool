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
		instance.messageBuilder(StaminaPacket.CtoS.class, index++)
				.noResponse()
				.decoder(StaminaPacket.CtoS.HANDLER::decode)
				.encoder(StaminaPacket.CtoS.HANDLER::encode)
				.consumerMainThread(StaminaPacket.CtoS.HANDLER::handleInPhysicalServer)
				.add();
		instance.messageBuilder(StaminaPacket.StoC.class, index++)
				.noResponse()
				.decoder(StaminaPacket.StoC.HANDLER::decode)
				.encoder(StaminaPacket.StoC.HANDLER::encode)
				.consumerMainThread(StaminaPacket.StoC.HANDLER::handleInPhysicalServer)
				.add();
		instance.messageBuilder(MultiStaminaPacket.class, index++)
				.noResponse()
				.decoder(MultiStaminaPacket.HANDLER::decode)
				.encoder(MultiStaminaPacket.HANDLER::encode)
				.consumerMainThread(MultiStaminaPacket.HANDLER::handleInPhysicalServer)
				.add();
		instance.messageBuilder(PlayerActionPacket.CtoS.class, index++)
				.noResponse()
				.decoder(PlayerActionPacket.CtoS.HANDLER::decode)
				.encoder(PlayerActionPacket.CtoS.HANDLER::encode)
				.consumerMainThread(PlayerActionPacket.CtoS.HANDLER::handleInPhysicalServer)
				.add();
		instance.messageBuilder(PlayerActionPacket.StoC.class, index++)
				.noResponse()
				.decoder(PlayerActionPacket.StoC.HANDLER::decode)
				.encoder(PlayerActionPacket.StoC.HANDLER::encode)
				.consumerMainThread(PlayerActionPacket.StoC.HANDLER::handleInPhysicalServer)
				.add();
		instance.messageBuilder(MultiPlayerActionPacket.class, index++)
				.noResponse()
				.decoder(MultiPlayerActionPacket.HANDLER::decode)
				.encoder(MultiPlayerActionPacket.HANDLER::encode)
				.consumerMainThread(MultiPlayerActionPacket.HANDLER::handleInPhysicalServer)
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
		instance.messageBuilder(EnableActionPacket.CtoS.class, index++)
				.noResponse()
				.decoder(EnableActionPacket.CtoS.HANDLER::decode)
				.encoder(EnableActionPacket.CtoS.HANDLER::encode)
				.consumerMainThread(EnableActionPacket.CtoS.HANDLER::handleInPhysicalServer)
				.add();
		instance.messageBuilder(EnableActionPacket.StoC.class, index++)
				.noResponse()
				.decoder(EnableActionPacket.StoC.HANDLER::decode)
				.encoder(EnableActionPacket.StoC.HANDLER::encode)
				.consumerMainThread(EnableActionPacket.StoC.HANDLER::handleInPhysicalServer)
				.add();
		instance.messageBuilder(ChangeActivationPacket.CtoS.class, index++)
				.noResponse()
				.decoder(ChangeActivationPacket.CtoS.HANDLER::decode)
				.encoder(ChangeActivationPacket.CtoS.HANDLER::encode)
				.consumerMainThread(ChangeActivationPacket.CtoS.HANDLER::handleInPhysicalServer)
				.add();
		instance.messageBuilder(ChangeActivationPacket.StoC.class, index++)
				.noResponse()
				.decoder(ChangeActivationPacket.StoC.HANDLER::decode)
				.encoder(ChangeActivationPacket.StoC.HANDLER::encode)
				.consumerMainThread(ChangeActivationPacket.StoC.HANDLER::handleInPhysicalServer)
				.add();
		instance.messageBuilder(SkilltreePacket.class, index++)
				.noResponse()
				.decoder(SkilltreePacket.HANDLER::decode)
				.encoder(SkilltreePacket.HANDLER::encode)
				.consumerMainThread(SkilltreePacket.HANDLER::handleInPhysicalServer)
				.add();
	}
}
