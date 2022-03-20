package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.constants.Advancements;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

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
			parkourability.getActionInfo().receiveServerPermissions(this);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	private static ActionPermissionsMessage newInstance(ServerPlayer player) {
		ActionPermissionsMessage message = new ActionPermissionsMessage();
		PlayerAdvancements adv = player.getAdvancements();
		MinecraftServer server = player.server;
		ServerAdvancementManager manager = server.getAdvancements();
		boolean advEnabled = getProgress(adv, manager, Advancements.ROOT).isDone();
		ParCoolConfig.Server config = ParCoolConfig.CONFIG_SERVER;

		message.allowedCatLeap = config.allowCatLeap.get() && (!advEnabled || getProgress(adv, manager, Advancements.CATLEAP).isDone());
		message.allowedCrawl = config.allowCrawl.get() && (!advEnabled || getProgress(adv, manager, Advancements.CRAWL).isDone());
		message.allowedDodge = config.allowDodge.get() && (!advEnabled || getProgress(adv, manager, Advancements.DODGE).isDone());
		message.allowedFastRunning = config.allowFastRunning.get() && (!advEnabled || getProgress(adv, manager, Advancements.FAST_RUN).isDone());
		message.allowedClingToCliff = config.allowClingToCliff.get() && (!advEnabled || getProgress(adv, manager, Advancements.CLING_TO_CLIFF).isDone());
		message.allowedRoll = config.allowRoll.get() && (!advEnabled || getProgress(adv, manager, Advancements.ROLL).isDone());
		message.allowedVault = config.allowVault.get() && (!advEnabled || getProgress(adv, manager, Advancements.VAULT).isDone());
		message.allowedWallJump = config.allowWallJump.get() && (!advEnabled || getProgress(adv, manager, Advancements.WALL_JUMP).isDone());
		message.allowedInfiniteStamina = config.allowInfiniteStamina.get();
		return message;
	}

	private static AdvancementProgress getProgress(PlayerAdvancements advancements, ServerAdvancementManager manager, ResourceLocation name) {
		return advancements.getOrStartProgress(manager.getAdvancement(name));
	}

	public static void send(ServerPlayer player) {
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), newInstance(player));
	}

}
