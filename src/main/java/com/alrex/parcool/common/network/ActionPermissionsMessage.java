package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.constants.ActionsEnum;
import com.alrex.parcool.constants.TranslateKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class ActionPermissionsMessage {
	private boolean allowedCatLeap;
	private boolean allowedCrawl;
	private boolean allowedDodge;
	private boolean allowedFastRunning;
	private boolean allowedClingToCliff;
	private boolean allowedRoll;
	private boolean allowedVault;
	private boolean allowedWallJump;
	private boolean allowedInfiniteStamina;

	public boolean isAllowedCatLeap() {
		return allowedCatLeap;
	}

	public boolean isAllowedClingToCliff() {
		return allowedClingToCliff;
	}

	public boolean isAllowedCrawl() {
		return allowedCrawl;
	}

	public boolean isAllowedDodge() {
		return allowedDodge;
	}

	public boolean isAllowedFastRunning() {
		return allowedFastRunning;
	}

	public boolean isAllowedRoll() {
		return allowedRoll;
	}

	public boolean isAllowedVault() {
		return allowedVault;
	}

	public boolean isAllowedWallJump() {
		return allowedWallJump;
	}

	public boolean isAllowedInfiniteStamina() {
		return allowedInfiniteStamina;
	}

	public void encode(PacketBuffer packet) {
		packet.writeBoolean(allowedCatLeap);
		packet.writeBoolean(allowedCrawl);
		packet.writeBoolean(allowedDodge);
		packet.writeBoolean(allowedFastRunning);
		packet.writeBoolean(allowedClingToCliff);
		packet.writeBoolean(allowedRoll);
		packet.writeBoolean(allowedVault);
		packet.writeBoolean(allowedWallJump);
		packet.writeBoolean(allowedInfiniteStamina);
	}

	public static ActionPermissionsMessage decode(PacketBuffer packet) {
		ActionPermissionsMessage message = new ActionPermissionsMessage();
		message.allowedCatLeap = packet.readBoolean();
		message.allowedCrawl = packet.readBoolean();
		message.allowedDodge = packet.readBoolean();
		message.allowedFastRunning = packet.readBoolean();
		message.allowedClingToCliff = packet.readBoolean();
		message.allowedRoll = packet.readBoolean();
		message.allowedVault = packet.readBoolean();
		message.allowedWallJump = packet.readBoolean();
		message.allowedInfiniteStamina = packet.readBoolean();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player = Minecraft.getInstance().player;
			if (player == null) return;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			parkourability.getPermission().receiveServerPermissions(this);
			parkourability.getActionInfo().receiveServerPermissions(this);
			StringBuilder text = new StringBuilder();
			if (allowedCatLeap && allowedCrawl && allowedDodge && allowedFastRunning && allowedFastRunning
					&& allowedClingToCliff && allowedRoll && allowedVault && allowedWallJump)
				return;
			text.append("ParCool : ").append(I18n.format(TranslateKeys.MESSAGE_PROHIBITED_ACTION));
			if (!allowedCatLeap) text.append("\n+- ").append(ActionsEnum.CatLeap);
			if (!allowedCrawl) text.append("\n+- ").append(ActionsEnum.Crawl);
			if (!allowedDodge) text.append("\n+- ").append(ActionsEnum.Dodge);
			if (!allowedFastRunning) text.append("\n+- ").append(ActionsEnum.FastRunning);
			if (!allowedClingToCliff) text.append("\n+- ").append(ActionsEnum.GrabCliff);
			if (!allowedRoll) text.append("\n+- ").append(ActionsEnum.Roll);
			if (!allowedVault) text.append("\n+- ").append(ActionsEnum.Vault);
			if (!allowedWallJump) text.append("\n+- ").append(ActionsEnum.WallJump);
			player.sendStatusMessage(new StringTextComponent(text.toString()), false);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	private static ActionPermissionsMessage newInstance() {
		ActionPermissionsMessage message = new ActionPermissionsMessage();
		ParCoolConfig.Server config = ParCoolConfig.CONFIG_SERVER;
		message.allowedCatLeap = config.allowCatLeap.get();
		message.allowedCrawl = config.allowCrawl.get();
		message.allowedDodge = config.allowDodge.get();
		message.allowedFastRunning = config.allowFastRunning.get();
		message.allowedClingToCliff = config.allowClingToCliff.get();
		message.allowedRoll = config.allowRoll.get();
		message.allowedVault = config.allowVault.get();
		message.allowedWallJump = config.allowWallJump.get();
		message.allowedInfiniteStamina = config.allowInfiniteStamina.get();
		return message;
	}

	public static void send(ServerPlayerEntity player) {
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), newInstance());
	}

	public static void broadcast() {
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), newInstance());
	}

}
