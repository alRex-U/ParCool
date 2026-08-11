package com.alrex.parcool.api.stamina;

public interface IReadableStamina {
    double max();

    double value();

    boolean isExhausted();

    boolean imposePenalty();
}
