package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ActionLimitation;
import com.alrex.parcool.common.info.Limitations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncLimitationByServerMessage {
	private boolean forIndividuals = false;
	private boolean enforced = false;
	private int maxStaminaLimitation = Integer.MAX_VALUE;
	private int maxStaminaRecovery = Integer.MAX_VALUE;
	private boolean permissionOfInfiniteStamina = true;
	private final ActionLimitation[] limitations = new ActionLimitation[ActionList.ACTIONS.size()];

	public void setEnforced(boolean enforced) {
		this.enforced = enforced;
	}

	public void setMaxStaminaLimitation(int maxStaminaLimitation) {
		this.maxStaminaLimitation = maxStaminaLimitation;
	}

	public void setPermissionOfInfiniteStamina(boolean permissionOfInfiniteStamina) {
		this.permissionOfInfiniteStamina = permissionOfInfiniteStamina;
	}

	public void setMaxStaminaRecovery(int maxStaminaRecovery) {
		this.maxStaminaRecovery = maxStaminaRecovery;
	}

	public boolean isEnforced() {
		return enforced;
	}

	public ActionLimitation[] getLimitations() {
		return limitations;
	}

	public int getMaxStaminaLimitation() {
		return maxStaminaLimitation;
	}

	public int getMaxStaminaRecovery() {
		return maxStaminaRecovery;
	}

	public boolean getPermissionOfInfiniteStamina() {
		return permissionOfInfiniteStamina;
	}

	public void encode(PacketBuffer packet) {
		packet.writeBoolean(forIndividuals);
		packet.writeBoolean(enforced);
		packet.writeInt(maxStaminaLimitation);
		packet.writeInt(maxStaminaRecovery);
		packet.writeBoolean(permissionOfInfiniteStamina);
		for (ActionLimitation limitation : limitations) {
			packet.writeBoolean(limitation.isPossible())
					.writeInt(limitation.getLeastStaminaConsumption());
		}
	}

	public static SyncLimitationByServerMessage decode(PacketBuffer packet) {
		SyncLimitationByServerMessage message = new SyncLimitationByServerMessage();
		message.forIndividuals = packet.readBoolean();
		message.enforced = packet.readBoolean();
		message.maxStaminaLimitation = packet.readInt();
		message.maxStaminaRecovery = packet.readInt();
		message.permissionOfInfiniteStamina = packet.readBoolean();
		for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
			message.limitations[i]
					= new ActionLimitation(
					packet.readBoolean(),
					packet.readInt()
			);
		}
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			if (player == null) return;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			if (forIndividuals) {
				parkourability.getActionInfo().receiveIndividualLimitation(this);
			} else {
				parkourability.getActionInfo().receiveLimitation(this);
				SyncClientInformationMessage.sync(player);
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	private static SyncLimitationByServerMessage newInstance(Limitations limitation) {
		SyncLimitationByServerMessage message = new SyncLimitationByServerMessage();
		limitation.writeSyncData(message);
		return message;
	}

	public static void sendServerLimitation(ServerPlayerEntity player) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		SyncLimitationByServerMessage msg = newInstance(parkourability.getActionInfo().getServerLimitation());
		msg.forIndividuals = false;
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
	}

	public static void sendIndividualLimitation(ServerPlayerEntity player) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		SyncLimitationByServerMessage msg = newInstance(parkourability.getActionInfo().getIndividualLimitation());
		msg.forIndividuals = true;
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
	}
}
