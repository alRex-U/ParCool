package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.particle.ParticleProvider;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class AvoidDamageMessage {
	private UUID playerID = null;
	private float damage = 0f;

	public void encode(PacketBuffer packet) {
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
		packet.writeFloat(damage);
	}

	public static AvoidDamageMessage decode(PacketBuffer packet) {
		AvoidDamageMessage message = new AvoidDamageMessage();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		message.damage = packet.readFloat();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) Minecraft.getInstance().level.getPlayerByUUID(playerID);
			if (player == null) return;
			Stamina stamina = Stamina.get(player);
			Parkourability parkourability = Parkourability.get(player);
			if (stamina == null || parkourability == null) return;
			stamina.consume(parkourability.getActionInfo().getStaminaConsumptionDodgeAvoid(damage), parkourability.getActionInfo());
			ParticleProvider.spawnEffectAvoidDamage(player);
			player.playSound(SoundEvents.ANVIL_LAND, 0.6f, 0.7f);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public static void send(ServerPlayerEntity player, float damage) {
		AvoidDamageMessage message = new AvoidDamageMessage();
		message.playerID = player.getUUID();
		message.damage = damage;
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), message);
	}
}
