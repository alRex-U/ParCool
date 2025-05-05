package com.alrex.parcool.common.attachment.common;

import com.alrex.parcool.api.Attributes;
import com.alrex.parcool.common.network.payload.StaminaPayload;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

public record ReadonlyStamina(boolean isExhausted, int value, int max) {
    public static ReadonlyStamina createDefault() {
        return new ReadonlyStamina(false, 0, 2000);
    }

    public ReadonlyStamina consumed(int value) {
        if (isExhausted()) return this;
        int newValue = this.value() - value;
        boolean exhausted = false;
        if (newValue < 0) {
            newValue = 0;
            exhausted = true;
        }
        return new ReadonlyStamina(exhausted, newValue, max());
    }

    public ReadonlyStamina recovered(int value) {
        int newValue = this.value() + value;
        boolean exhausted = isExhausted();
        if (newValue > max) {
            newValue = max;
            exhausted = false;
        }
        return new ReadonlyStamina(exhausted, newValue, max());
    }

    @OnlyIn(Dist.CLIENT)
    public ReadonlyStamina updateMax(LocalPlayer player) {
        var attr = player.getAttribute(Attributes.MAX_STAMINA);
        if (attr == null) return this;
        var parkourability = Parkourability.get(player);
        if (parkourability == null) return this;
        int newMax = (int) Math.min(Math.floor(attr.getValue()), parkourability.getActionInfo().getMaxStaminaLimit());
        if (max() == newMax) return this;
        return new ReadonlyStamina(isExhausted(), value(), newMax);
    }

    @OnlyIn(Dist.CLIENT)
    public void sync(LocalPlayer player) {
        PacketDistributor.sendToServer(new StaminaPayload(player.getUUID(), this));
    }

    public static final Codec<ReadonlyStamina> CODEC = RecordCodecBuilder.create(staminaInstance ->
            staminaInstance.group(
                    Codec.BOOL.fieldOf("exhausted").forGetter(ReadonlyStamina::isExhausted),
                    Codec.INT.fieldOf("value").forGetter(ReadonlyStamina::value),
                    Codec.INT.fieldOf("max").forGetter(ReadonlyStamina::max)
            ).apply(staminaInstance, ReadonlyStamina::new)
    );
    public static final StreamCodec<ByteBuf, ReadonlyStamina> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            ReadonlyStamina::isExhausted,
            ByteBufCodecs.VAR_INT,
            ReadonlyStamina::value,
            ByteBufCodecs.INT,
            ReadonlyStamina::max,
            ReadonlyStamina::new
    );
}
