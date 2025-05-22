package com.alrex.parcool.common.capability.stamina;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class OtherStamina implements IStamina {
    private static final String EXHAUSTED_SPEED_MODIFIER_NAME = "parcool.modifier.exhausted.speed";
    private static final UUID EXHAUSTED_SPEED_MODIFIER_UUID = UUID.randomUUID();
    private final Player player;

    public OtherStamina(Player player) {
        this.player = player;
    }

    private int max = 0;
    private int value = 0;
    private boolean exhausted;
    private boolean imposingPenalty;

    public void setMax(int value) {
        max = value;
    }

    @Override
    public int getActualMaxStamina() {
        return max;
    }

    @Override
    public int get() {
        return value;
    }

    @Override
    public int getOldValue() {
        return value;
    }

    @Override
    public void consume(int value) {
    }

    @Override
    public void recover(int value) {
    }

    @Override
    public boolean isExhausted() {
        return exhausted;
    }

    @Override
    public void setExhaustion(boolean value) {
        exhausted = value;
    }

    @Override
    public void tick() {
        if (player instanceof ServerPlayer) {
            AttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attr == null) return;
            if (attr.getModifier(EXHAUSTED_SPEED_MODIFIER_UUID) != null)
                attr.removeModifier(EXHAUSTED_SPEED_MODIFIER_UUID);
            var parkourability = Parkourability.get(player);
            if (parkourability == null) return;
            if (isImposingExhaustionPenalty() && parkourability.getClientInfo().get(ParCoolConfig.Client.Booleans.EnableStaminaExhaustionPenalty)) {
                player.setSprinting(false);
                attr.addTransientModifier(new AttributeModifier(
                        EXHAUSTED_SPEED_MODIFIER_UUID,
                        EXHAUSTED_SPEED_MODIFIER_NAME,
                        -0.05,
                        AttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

    @Override
    public boolean isImposingExhaustionPenalty() {
        return imposingPenalty;
    }

    public void setImposingPenalty(boolean imposingPenalty) {
        this.imposingPenalty = imposingPenalty;
    }

    @Override
    public void set(int value) {
        this.value = value;
    }
}
