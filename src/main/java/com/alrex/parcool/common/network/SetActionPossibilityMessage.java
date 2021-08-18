package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.constants.ActionsEnum;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class SetActionPossibilityMessage {
	private ActionsEnum actionsEnum = null;
	private boolean possibility = false;

	public void encode(PacketBuffer packet) {
		packet.writeBoolean(possibility);
		packet.writeString(actionsEnum.name());
	}

	public static SetActionPossibilityMessage decode(PacketBuffer packet) {
		SetActionPossibilityMessage message = new SetActionPossibilityMessage();
		message.possibility = packet.readBoolean();
		message.actionsEnum = ActionsEnum.valueOf(packet.readString());
		return message;
	}

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			if (contextSupplier.get().getNetworkManager().getDirection() == PacketDirection.CLIENTBOUND) {
				ParCoolConfig.Client c = ParCoolConfig.CONFIG_CLIENT;
				switch (actionsEnum) {
					case Crawl:
						c.canCrawl.set(possibility);
						break;
					case CatLeap:
						c.canCatLeap.set(possibility);
						break;
					case Dodge:
						c.canDodge.set(possibility);
						break;
					case FastRunning:
						c.canFastRunning.set(possibility);
						break;
					case Roll:
						c.canRoll.set(possibility);
						break;
					case Vault:
						c.canVault.set(possibility);
						break;
					case WallJump:
						c.canWallJump.set(possibility);
						break;
					case GrabCliff:
						c.canGrabCliff.set(possibility);
						break;
					case InfiniteStamina:
						c.infiniteStamina.set(possibility);
						break;
				}
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public static void send(ServerPlayerEntity player, ActionsEnum actionsEnum, boolean possibility) {
		SetActionPossibilityMessage message = new SetActionPossibilityMessage();
		message.actionsEnum = actionsEnum;
		message.possibility = possibility;
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
	}
}