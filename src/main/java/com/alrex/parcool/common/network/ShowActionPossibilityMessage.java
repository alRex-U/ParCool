package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.constants.ActionsEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class ShowActionPossibilityMessage {
	ActionsEnum action = null;

	public void encode(PacketBuffer packet) {
		packet.writeBoolean(action != null);
		if (action != null) {
			packet.writeUtf(action.name());
		}
	}

	public static ShowActionPossibilityMessage decode(PacketBuffer packet) {
		ShowActionPossibilityMessage message = new ShowActionPossibilityMessage();
		try {
			if (packet.readBoolean()) message.action = ActionsEnum.valueOf(packet.readUtf());
		} catch (IllegalArgumentException e) {
			message.action = null;
		}

		return message;
	}

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			if (player == null) return;
			player.displayClientMessage(new StringTextComponent(getText(action)), false);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	//only in Client
	private static String getText(ActionsEnum action) {
		ParCoolConfig.Client c = ParCoolConfig.CONFIG_CLIENT;
		if (action != null) switch (action) {
			case CatLeap:
				return action.name() + " : " + c.canCatLeap.get().toString();
			case Crawl:
				return action.name() + " : " + c.canCrawl.get().toString();
			case Dodge:
				return action.name() + " : " + c.canDodge.get().toString();
			case FastRunning:
				return action.name() + " : " + c.canFastRunning.get().toString();
			case GrabCliff:
				return action.name() + " : " + c.canClingToCliff.get().toString();
			case Roll:
				return action.name() + " : " + c.canRoll.get().toString();
			case Vault:
				return action.name() + " : " + c.canVault.get().toString();
			case WallJump:
				return action.name() + " : " + c.canWallJump.get().toString();
		}
		StringBuilder builder = new StringBuilder();
		builder
				.append(ActionsEnum.CatLeap.name()).append(" : ").append(c.canCatLeap.get().toString()).append('\n')
				.append(ActionsEnum.Crawl.name()).append(" : ").append(c.canCrawl.get().toString()).append('\n')
				.append(ActionsEnum.Dodge.name()).append(" : ").append(c.canDodge.get().toString()).append('\n')
				.append(ActionsEnum.FastRunning.name()).append(" : ").append(c.canFastRunning.get().toString()).append('\n')
				.append(ActionsEnum.GrabCliff.name()).append(" : ").append(c.canClingToCliff.get().toString()).append('\n')
				.append(ActionsEnum.Roll.name()).append(" : ").append(c.canRoll.get().toString()).append('\n')
				.append(ActionsEnum.Vault.name()).append(" : ").append(c.canVault.get().toString()).append('\n')
				.append(ActionsEnum.WallJump.name()).append(" : ").append(c.canWallJump.get().toString());
		return builder.toString();
	}

	public static void send(ServerPlayerEntity player, ActionsEnum action) {
		ShowActionPossibilityMessage message = new ShowActionPossibilityMessage();
		message.action = action;
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
	}

}
