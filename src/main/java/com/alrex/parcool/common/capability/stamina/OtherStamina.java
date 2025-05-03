package com.alrex.parcool.common.capability.stamina;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.ServerPlayerWrapper;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;

import java.util.UUID;

public class OtherStamina implements IStamina {
    private static final String EXHAUSTED_SPEED_MODIFIER_NAME = "parcool.modifier.exhausted.speed";
    private static final UUID EXHAUSTED_SPEED_MODIFIER_UUID = UUID.randomUUID();
    private final PlayerWrapper player;

    public OtherStamina(PlayerWrapper player) {
        this.player = player;
    }

    private int max = 0;
    private int value = 0;
    private boolean exhausted;

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
        if (ServerPlayerWrapper.is(player)) {
            ModifiableAttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attr == null) return;
            if (attr.getModifier(EXHAUSTED_SPEED_MODIFIER_UUID) != null)
                attr.removeModifier(EXHAUSTED_SPEED_MODIFIER_UUID);
            if (isExhausted()) {
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
    public void set(int value) {
        this.value = value;
    }
}
