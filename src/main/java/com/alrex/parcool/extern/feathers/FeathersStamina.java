package com.alrex.parcool.extern.feathers;

import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.elenai.feathers.api.FeathersHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class FeathersStamina extends AbstractLocalStamina {
    private double fraction = 0;

    public FeathersStamina(Player owner) {
        super(owner);
    }

    @Override
    public void setValue(double value) {
    }

    @Override
    public void consume(double value) {
        if (isInfinite()) return;
        if (owner.isLocalPlayer()) {
            int spentFeathers = (int) (value / 100);
            fraction += (value / 100.) - spentFeathers;
            if (fraction >= 1) {
                fraction -= 1;
                FeathersHelper.spendFeathers(spentFeathers + 1);
            } else {
                FeathersHelper.spendFeathers(spentFeathers);
            }
        }
    }

    @Override
    public void recover(double value) {
        fraction -= value / 100f;
        if (fraction < 0) {
            fraction = 0;
        }
    }

    @Override
    public double max() {
        if (owner.isLocalPlayer()) {
            return FeathersHelper.getMaxFeathers();
        } else if (owner instanceof ServerPlayer serverPlayer) {
            return FeathersHelper.getMaxFeathers(serverPlayer);
        }
        return 1;
    }

    @Override
    public double value() {
        if (owner.isLocalPlayer()) {
            return FeathersHelper.getFeathers();
        } else if (owner instanceof ServerPlayer serverPlayer) {
            return FeathersHelper.getFeathers(serverPlayer);
        }
        return 1;
    }

    @Override
    public boolean isExhausted() {
        return owner.isLocalPlayer() && !FeathersHelper.checkFeathersRemaining();
    }

    @Override
    public boolean imposePenalty() {
        return false;
    }
}
