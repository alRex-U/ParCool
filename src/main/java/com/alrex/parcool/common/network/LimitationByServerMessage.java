package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ActionLimitation;
import com.alrex.parcool.common.info.LimitationByServer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class LimitationByServerMessage {
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

	public static LimitationByServerMessage decode(PacketBuffer packet) {
		LimitationByServerMessage message = new LimitationByServerMessage();
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
			PlayerEntity player = Minecraft.getInstance().player;
			if (player == null) return;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			if (forIndividuals) {
				parkourability.getActionInfo().receiveIndividualLimitation(this);
			} else {
				parkourability.getActionInfo().receiveLimitation(this);
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	private static LimitationByServerMessage newInstanceForServerWide() {
		LimitationByServerMessage message = new LimitationByServerMessage();
		ParCoolConfig.Server config = ParCoolConfig.CONFIG_SERVER;

		message.maxStaminaLimitation = config.staminaMax.get();
		message.enforced = ParCoolConfig.CONFIG_SERVER.enforced.get();
		message.permissionOfInfiniteStamina = config.allowInfiniteStamina.get();
		message.maxStaminaRecovery = config.staminaRecoveryMax.get();
		message.forIndividuals = false;
		for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
			message.limitations[i]
					= new ActionLimitation(
					config.getPermissionOf(ActionList.getByIndex(i)),
					config.getLeastStaminaConsumptionOf(ActionList.getByIndex(i))
			);
		}
		return message;
	}

	private static LimitationByServerMessage newInstance(LimitationByServer limitation) {
		LimitationByServerMessage message = new LimitationByServerMessage();

		message.forIndividuals = false;
		limitation.writeSyncData(message);
		return message;
	}

	public static void send(ServerPlayerEntity player) {
		LimitationByServerMessage msg = newInstanceForServerWide();
		msg.forIndividuals = false;
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
	}

	public static void sendIndividualLimitation(ServerPlayerEntity player) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		LimitationByServerMessage msg = newInstance(parkourability.getActionInfo().getIndividualLimitation());
		msg.forIndividuals = true;
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
	}
}
