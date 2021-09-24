package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.ActionPermissions;
import com.alrex.parcool.constants.ActionsEnum;
import com.alrex.parcool.constants.TranslateKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class SendActionPermissionsMessage {
	private boolean allowedCatLeap;
	private boolean allowedCrawl;
	private boolean allowedDodge;
	private boolean allowedFastRunning;
	private boolean allowedGrabCliff;
	private boolean allowedRoll;
	private boolean allowedVault;
	private boolean allowedWallJump;

	public void encode(PacketBuffer packet) {
		packet.writeBoolean(allowedCatLeap);
		packet.writeBoolean(allowedCrawl);
		packet.writeBoolean(allowedDodge);
		packet.writeBoolean(allowedFastRunning);
		packet.writeBoolean(allowedGrabCliff);
		packet.writeBoolean(allowedRoll);
		packet.writeBoolean(allowedVault);
		packet.writeBoolean(allowedWallJump);
	}

	public static SendActionPermissionsMessage decode(PacketBuffer packet) {
		SendActionPermissionsMessage message = new SendActionPermissionsMessage();
		message.allowedCatLeap = packet.readBoolean();
		message.allowedCrawl = packet.readBoolean();
		message.allowedDodge = packet.readBoolean();
		message.allowedFastRunning = packet.readBoolean();
		message.allowedGrabCliff = packet.readBoolean();
		message.allowedRoll = packet.readBoolean();
		message.allowedVault = packet.readBoolean();
		message.allowedWallJump = packet.readBoolean();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			ActionPermissions.setAllowedCatLeap(allowedCatLeap);
			ActionPermissions.setAllowedCrawl(allowedCrawl);
			ActionPermissions.setAllowedDodge(allowedDodge);
			ActionPermissions.setAllowedFastRunning(allowedFastRunning);
			ActionPermissions.setAllowedGrabCliff(allowedGrabCliff);
			ActionPermissions.setAllowedRoll(allowedRoll);
			ActionPermissions.setAllowedVault(allowedVault);
			ActionPermissions.setAllowedWallJump(allowedWallJump);
			PlayerEntity player = Minecraft.getInstance().player;
			if (player == null) return;
			player.sendStatusMessage(ITextComponent.func_241827_a_(new StringBuilder()
					.append("ParCool : ")
					.append(I18n.format(TranslateKeys.MESSAGE_ALLOWED_ACTION)).append('\n')
					.append(ActionsEnum.CatLeap.name()).append(" : ").append(allowedCatLeap).append('\n')
					.append(ActionsEnum.Crawl.name()).append(" : ").append(allowedCrawl).append('\n')
					.append(ActionsEnum.Dodge.name()).append(" : ").append(allowedDodge).append('\n')
					.append(ActionsEnum.FastRunning.name()).append(" : ").append(allowedFastRunning).append('\n')
					.append(ActionsEnum.GrabCliff.name()).append(" : ").append(allowedGrabCliff).append('\n')
					.append(ActionsEnum.Roll.name()).append(" : ").append(allowedRoll).append('\n')
					.append(ActionsEnum.Vault.name()).append(" : ").append(allowedVault).append('\n')
					.append(ActionsEnum.WallJump.name()).append(" : ").append(allowedWallJump)
					.toString()), false
			);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public static void send(ServerPlayerEntity player) {
		SendActionPermissionsMessage message = new SendActionPermissionsMessage();
		ParCoolConfig.Server config = ParCoolConfig.CONFIG_SERVER;
		message.allowedCatLeap = config.allowCatLeap.get();
		message.allowedCrawl = config.allowCrawl.get();
		message.allowedDodge = config.allowDodge.get();
		message.allowedFastRunning = config.allowFastRunning.get();
		message.allowedGrabCliff = config.allowGrabCliff.get();
		message.allowedRoll = config.allowRoll.get();
		message.allowedVault = config.allowVault.get();
		message.allowedWallJump = config.allowWallJump.get();
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
	}
}
