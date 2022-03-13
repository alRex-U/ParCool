package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.particle.ParticleProvider;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class AvoidDamageMessage {
	private UUID playerID = null;
	private float damage = 0f;

	public void encode(FriendlyByteBuf packet) {
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
		packet.writeFloat(damage);
	}

	public static AvoidDamageMessage decode(FriendlyByteBuf packet) {
		AvoidDamageMessage message = new AvoidDamageMessage();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		message.damage = packet.readFloat();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			AbstractClientPlayer player = (AbstractClientPlayer) Minecraft.getInstance().level.getPlayerByUUID(playerID);
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

	public static void send(ServerPlayer player, float damage) {
		AvoidDamageMessage message = new AvoidDamageMessage();
		message.playerID = player.getUUID();
		message.damage = damage;
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), message);
	}
}
