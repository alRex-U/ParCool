package com.alrex.parcool.api.stamina;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.ParCoolMobEffects;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.network.StaminaPacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

public abstract class AbstractLocalStamina implements IReadableStamina {
    @OnlyIn(Dist.CLIENT)
    public static AbstractLocalStamina get(LocalPlayer player) {
        return (AbstractLocalStamina) Parkourability.get(player).getStamina();
    }

    public AbstractLocalStamina(Player owner) {
        this.owner = owner;
    }

    protected final Player owner;
    private boolean dirty;

    public abstract void setValue(double value);

    public abstract void consume(double value);

    public abstract void recover(double value);

    public boolean isInfinite() {
        return owner.isCreative() || owner.isSpectator() || owner.hasEffect(ParCoolMobEffects.INEXHAUSTIBLE);
    }

    public void tick() {
    }

    public boolean imposePenalty() {
        return isExhausted();
    }

    public boolean showHud() {
        return false;
    }

    public void setDirty() {
        this.dirty = true;
    }

    public final void sync() {
        if (dirty) {
            PacketDistributor.sendToServer(new StaminaPacket(owner.getUUID(), true, this.copyAsReadOnly()));
            dirty = false;
        }
    }
}
