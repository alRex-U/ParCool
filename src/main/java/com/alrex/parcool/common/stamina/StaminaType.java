package com.alrex.parcool.common.stamina;

import com.alrex.parcool.common.stamina.handlers.HungerStaminaHandler;
import com.alrex.parcool.common.stamina.handlers.InfiniteStaminaHandler;
import com.alrex.parcool.common.stamina.handlers.ParCoolStaminaHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public enum StaminaType {
    NONE(InfiniteStaminaHandler::new), PARCOOL(ParCoolStaminaHandler::new), HUNGER(HungerStaminaHandler::new);

    private final Supplier<IParCoolStaminaHandler> constructor;

    StaminaType(Supplier<IParCoolStaminaHandler> constructor) {
        this.constructor = constructor;
    }

    public IParCoolStaminaHandler newHandler() {
        return constructor.get();
    }

    public static final StreamCodec<ByteBuf, StaminaType> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            StaminaType::ordinal,
            (v) -> StaminaType.values()[v]
    );
}
