package com.alrex.parcool.common.stamina;

import com.alrex.parcool.common.stamina.handlers.HungerStaminaHandler;
import com.alrex.parcool.common.stamina.handlers.InfiniteStaminaHandler;
import com.alrex.parcool.common.stamina.handlers.ParCoolStaminaHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;
import java.util.function.Supplier;

public enum StaminaType {
    NONE(InfiniteStaminaHandler::new),
    PARCOOL(ParCoolStaminaHandler::new),
    HUNGER(HungerStaminaHandler::new);

    private final Function<Player, IParCoolStaminaHandler> constructor;

    StaminaType(Function<Player, IParCoolStaminaHandler> constructor) {
        this.constructor = constructor;
    }

    StaminaType(Supplier<IParCoolStaminaHandler> constructor) {
        this.constructor = (player) -> constructor.get();
    }

    public IParCoolStaminaHandler newHandler(Player player) {
        return constructor.apply(player);
    }

    public static final StreamCodec<ByteBuf, StaminaType> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            StaminaType::ordinal,
            (v) -> StaminaType.values()[v]
    );
}
