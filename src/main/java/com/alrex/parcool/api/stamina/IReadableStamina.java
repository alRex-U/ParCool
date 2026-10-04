package com.alrex.parcool.api.stamina;

import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.stamina.ReadonlyStamina;
import net.minecraft.world.entity.player.Player;

public interface IReadableStamina {
    public static IReadableStamina get(Player player) {
        return Parkourability.get(player).getStamina();
    }

    double max();

    double value();

    boolean isExhausted();

    boolean imposePenalty();

    default ReadonlyStamina copyAsReadOnly() {
        return new ReadonlyStamina(value(), max(), isExhausted(), imposePenalty());
    }
}
