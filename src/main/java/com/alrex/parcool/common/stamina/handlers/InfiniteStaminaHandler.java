package com.alrex.parcool.common.stamina.handlers;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import net.minecraft.world.entity.player.Player;

public class InfiniteStaminaHandler implements IParCoolStaminaHandler {
    private static final ReadonlyStamina INSTANCE = new ReadonlyStamina(false, 1, 1);

    //@OnlyIn(Dist.CLIENT)
    @Override
    public ReadonlyStamina initializeStamina(Player player, ReadonlyStamina current) {
        return INSTANCE;
    }

    //@OnlyIn(Dist.CLIENT)
    @Override
    public ReadonlyStamina consume(Player player, ReadonlyStamina current, int value) {
        return INSTANCE;
    }

    //@OnlyIn(Dist.CLIENT)
    @Override
    public ReadonlyStamina recover(Player player, ReadonlyStamina current, int value) {
        return INSTANCE;
    }
}
