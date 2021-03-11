package com.alrex.parcool.common.capability;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.common.util.LazyOptional;

public class JumpBoost implements IJumpBoost{
    @Override
    public boolean canJumpBoost(ClientPlayerEntity player) {
        return true;
    }

    @Override
    public double getBoostValue(ClientPlayerEntity player) {
        IStamina stamina;
        {
            LazyOptional<IStamina> staminaOptional = player.getCapability(IStamina.StaminaProvider.STAMINA_CAPABILITY);
            if (!staminaOptional.isPresent()) return 0;
            stamina = staminaOptional.resolve().get();
        }
        return stamina.isExhausted() ? -0.1 : 0.12;
    }
}
