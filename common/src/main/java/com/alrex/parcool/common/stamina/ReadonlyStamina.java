package com.alrex.parcool.common.stamina;

import com.alrex.parcool.api.stamina.IReadableStamina;

public record ReadonlyStamina(double value, double max, boolean isExhausted,
                              boolean imposePenalty) implements IReadableStamina {
    public static final ReadonlyStamina DEFAULT = new ReadonlyStamina(1, 1, false, false);
}
