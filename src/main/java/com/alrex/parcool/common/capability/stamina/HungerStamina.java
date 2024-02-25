package com.alrex.parcool.common.capability.stamina;

import com.alrex.parcool.api.Effects;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class HungerStamina implements IStamina {
    private final Player player;
    private float consumedBuffer = 0;

    public HungerStamina(Player player) {
        this.player = player;
    }

    @Override
    public int getActualMaxStamina() {
        return 20;
    }

    @Override
    public int get() {
        return player.getFoodData().getFoodLevel();
    }

    @Override
    public int getOldValue() {
        return get();
    }

    @Override
    public void consume(int value) {
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        if (isExhausted()
                || parkourability.getActionInfo().isStaminaInfinite(player.isSpectator() || player.isCreative())
                || player.hasEffect(Effects.INEXHAUSTIBLE.get())
        ) return;
        consumedBuffer += value / 150f;
    }

    @Override
    public void recover(int value) {
    }

    @Override
    public boolean isExhausted() {
        return get() <= 6;
    }

    @Override
    public void setExhaustion(boolean value) {
    }

    @Override
    public void tick() {
    }

    @Override
    public void set(int value) {
    }

    @Override
    public boolean wantToConsumeOnServer() {
        return consumedBuffer != 0f;
    }

    @Override
    public int getRequestedValueConsumedOnServer() {
        int neededValue = (int) (consumedBuffer * 10000f);
        consumedBuffer = 0f;
        return neededValue;
    }

    public static void consumeOnServer(ServerPlayer player, int value) {
        player.causeFoodExhaustion(value / 10000f);
    }
}
