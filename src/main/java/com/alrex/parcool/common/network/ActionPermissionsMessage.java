package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.nio.ByteBuffer;
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
	private boolean allowedFlipping;
	private boolean allowedBreakfall;
	private boolean allowedWallSlide;
	private boolean allowedHorizontalWallRun;
	private boolean allowedInfiniteStamina;
	private byte[] infoData = null;

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

	public boolean isAllowedFlipping() {
		return allowedFlipping;
	}

	public boolean isAllowedBreakfall() {
		return allowedBreakfall;
	}

	public boolean isAllowedWallSlide() {
		return allowedWallSlide;
	}

	public boolean isAllowedHorizontalWallRun() {
		return allowedHorizontalWallRun;
	}

	public void encode(FriendlyByteBuf packet) {
		packet.writeBoolean(allowedCatLeap);
		packet.writeBoolean(allowedCrawl);
		packet.writeBoolean(allowedDodge);
		packet.writeBoolean(allowedFastRunning);
		packet.writeBoolean(allowedClingToCliff);
		packet.writeBoolean(allowedRoll);
		packet.writeBoolean(allowedVault);
		packet.writeBoolean(allowedWallJump);
		packet.writeBoolean(allowedInfiniteStamina);
		packet.writeBoolean(allowedBreakfall);
		packet.writeBoolean(allowedFlipping);
		packet.writeBoolean(allowedWallSlide);
		packet.writeBoolean(allowedHorizontalWallRun);
		packet.writeByteArray(infoData);
	}

	public static ActionPermissionsMessage decode(FriendlyByteBuf packet) {
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
		message.allowedBreakfall = packet.readBoolean();
		message.allowedFlipping = packet.readBoolean();
		message.allowedWallSlide = packet.readBoolean();
		message.allowedHorizontalWallRun = packet.readBoolean();
		message.infoData = packet.readByteArray();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) return;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			parkourability.getPermission().receiveServerPermissions(this);
			parkourability.getActionInfo().receiveServerPermissions(ByteBuffer.wrap(infoData));
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
		message.allowedFlipping = config.allowFlipping.get();
		message.allowedBreakfall = config.allowBreakfall.get();
		message.allowedWallSlide = config.allowWallSlide.get();
		message.allowedHorizontalWallRun = config.allowHorizontalWallRun.get();

		ByteBuffer buffer = ByteBuffer.allocate(128);
		ActionInfo.encode(buffer);
		buffer.flip();
		message.infoData = new byte[buffer.limit()];
		buffer.get(message.infoData);
		return message;
	}

	public static void send(ServerPlayer player) {
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), newInstance());
	}

	public static void broadcast() {
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), newInstance());
	}
}
