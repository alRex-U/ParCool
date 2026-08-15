package com.alrex.parcool.api.stamina;

import com.alrex.parcool.common.stamina.ReadonlyStamina;

public interface IReadableStamina {
    double max();

    double value();

    boolean isExhausted();

    boolean imposePenalty();

    default ReadonlyStamina copyAsReadOnly() {
        return new ReadonlyStamina(value(), max(), isExhausted(), imposePenalty());
    }
}
