package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.constants.Advancements;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

public class ActionPermissionsMessage {
	private boolean allowedCatLeap;
	private boolean allowedCrawl;
	private boolean allowedDodge;
	private boolean allowedFastRunning;
	private boolean allowedClingToCliff;
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

	public void encode(PacketBuffer packet) {
		packet.writeBoolean(allowedCatLeap);
		packet.writeBoolean(allowedCrawl);
		packet.writeBoolean(allowedDodge);
		packet.writeBoolean(allowedFastRunning);
		packet.writeBoolean(allowedClingToCliff);
		packet.writeBoolean(allowedVault);
		packet.writeBoolean(allowedWallJump);
		packet.writeBoolean(allowedInfiniteStamina);
		packet.writeBoolean(allowedBreakfall);
		packet.writeBoolean(allowedFlipping);
		packet.writeBoolean(allowedWallSlide);
		packet.writeBoolean(allowedHorizontalWallRun);
		packet.writeByteArray(infoData);
	}

	public static ActionPermissionsMessage decode(PacketBuffer packet) {
		ActionPermissionsMessage message = new ActionPermissionsMessage();
		message.allowedCatLeap = packet.readBoolean();
		message.allowedCrawl = packet.readBoolean();
		message.allowedDodge = packet.readBoolean();
		message.allowedFastRunning = packet.readBoolean();
		message.allowedClingToCliff = packet.readBoolean();
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
			PlayerEntity player = Minecraft.getInstance().player;
			if (player == null) return;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			parkourability.getPermission().receiveServerPermissions(this);
			parkourability.getActionInfo().receiveServerPermissions(ByteBuffer.wrap(infoData));
		});
		contextSupplier.get().setPacketHandled(true);
	}

	private static ActionPermissionsMessage newInstance(ServerPlayerEntity player) {
		ActionPermissionsMessage message = new ActionPermissionsMessage();
		ParCoolConfig.Server config = ParCoolConfig.CONFIG_SERVER;
		MinecraftServer server = player.server;
		AdvancementManager manager = server.getAdvancements();
		PlayerAdvancements adv = player.getAdvancements();
		boolean advDisabled = !getProgress(adv, manager, Advancements.Root).isDone();

		message.allowedCatLeap = config.allowCatLeap.get() && (advDisabled || getProgress(adv, manager, Advancements.Catleap).isDone());
		message.allowedCrawl = config.allowCrawl.get() && (advDisabled || getProgress(adv, manager, Advancements.Crawl).isDone());
		message.allowedDodge = config.allowDodge.get() && (advDisabled || getProgress(adv, manager, Advancements.Dodge).isDone());
		message.allowedFastRunning = config.allowFastRunning.get() && (advDisabled || getProgress(adv, manager, Advancements.Fast_Run).isDone());
		message.allowedClingToCliff = config.allowClingToCliff.get() && (advDisabled || getProgress(adv, manager, Advancements.Cling_To_Cliff).isDone());
		message.allowedVault = config.allowVault.get() && (advDisabled || getProgress(adv, manager, Advancements.Vault).isDone());
		message.allowedWallJump = config.allowWallJump.get() && (advDisabled || getProgress(adv, manager, Advancements.Wall_Jump).isDone());
		message.allowedFlipping = config.allowFlipping.get() && (advDisabled || getProgress(adv, manager, Advancements.Flipping).isDone());
		message.allowedBreakfall = config.allowBreakfall.get() && (advDisabled || getProgress(adv, manager, Advancements.Breakfall).isDone());
		message.allowedWallSlide = config.allowWallSlide.get() && (advDisabled || getProgress(adv, manager, Advancements.Wall_Slide).isDone());
		message.allowedHorizontalWallRun = config.allowHorizontalWallRun.get() && (advDisabled || getProgress(adv, manager, Advancements.Horizontal_Wall_Run).isDone());
		message.allowedInfiniteStamina = config.allowInfiniteStamina.get();

		ByteBuffer buffer = ByteBuffer.allocate(128);
		ActionInfo.encode(buffer);
		buffer.flip();
		message.infoData = new byte[buffer.limit()];
		buffer.get(message.infoData);
		return message;
	}

	private static AdvancementProgress getProgress(PlayerAdvancements advancements, AdvancementManager manager, ResourceLocation name) {
		Advancement advancement = manager.getAdvancement(name);
		if (advancement == null) {
		}
		return advancements.getOrStartProgress(advancement);
	}

	public static void send(ServerPlayerEntity player) {
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), newInstance(player));
	}
}
