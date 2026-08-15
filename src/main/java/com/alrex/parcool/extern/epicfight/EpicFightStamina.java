package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.alrex.parcool.common.stamina.impl.ParCoolStamina;
import com.alrex.parcool.extern.AdditionalMods;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class EpicFightStamina extends AbstractLocalStamina {
    public EpicFightStamina(Player owner) {
        super(owner);
        fallback = new ParCoolStamina(owner, null);
    }

    private final ParCoolStamina fallback;
    private float consumeBuffer;

    @Override
    public void setValue(double value) {
        if (!AdditionalMods.epicFight().isBattleMode(owner)) {
            fallback.setValue(value);
        }
    }

    @Override
    public void consume(double value) {
        if (isInfinite()) return;
        if (AdditionalMods.epicFight().isBattleMode(owner)) {
            consumeBuffer += (float) (value / 60.);
        } else {
            fallback.consume(value);
        }
    }

    @Override
    public void recover(double value) {
        if (!AdditionalMods.epicFight().isBattleMode(owner)) {
            fallback.recover(value);
        }
    }

    @Override
    public double max() {
        if (AdditionalMods.epicFight().isBattleMode(owner)) {
            var patch = AdditionalMods.epicFight().getPlayerPatch(owner);
            if (patch == null) return 0;
            return patch.getMaxStamina();
        }
        return fallback.max();
    }

    @Override
    public void tick() {
        if (!AdditionalMods.epicFight().isBattleMode(owner)) fallback.tick();
        if (consumeBuffer > 0) {
            AdditionalMods.epicFight().getConnection().send(PacketDistributor.SERVER.noArg(), new EpicFightStaminaConsumePacket(consumeBuffer));
        }
        consumeBuffer = 0;
        setDirty();
    }

    @Override
    public double value() {
        if (AdditionalMods.epicFight().isBattleMode(owner)) {
            var patch = AdditionalMods.epicFight().getPlayerPatch(owner);
            if (patch == null) return 0;
            return patch.getStamina();
        }
        return fallback.value();
    }

    @Override
    public boolean isExhausted() {
        if (AdditionalMods.epicFight().isBattleMode(owner)) {
            var patch = AdditionalMods.epicFight().getPlayerPatch(owner);
            if (patch == null) return false;
            return patch.getStamina() < 0.1f;
        }
        return fallback.isExhausted();
    }

    @Override
    public boolean showHud() {
        return !AdditionalMods.epicFight().isBattleMode(owner);
    }
}
