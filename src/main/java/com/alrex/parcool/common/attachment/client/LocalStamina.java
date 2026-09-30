package com.alrex.parcool.common.attachment.client;

import com.alrex.parcool.api.Effects;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.ClientAttachments;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.common.stamina.handlers.InfiniteStaminaHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class LocalStamina {
    @Nullable private StaminaType currentType = null;
    @Nullable private IParCoolStaminaHandler handler = null;

    public static LocalStamina get(LocalPlayer player) {
        return player.getAttachedOrCreate(ClientAttachments.LOCAL_STAMINA);
    }

    public boolean isAvailable() {
        return handler != null && currentType != null;
    }

    public boolean isInfinite(LocalPlayer player) {
        return player.isCreative()
                || player.isSpectator()
                || handler instanceof InfiniteStaminaHandler;
    }

    public void changeType(LocalPlayer player, StaminaType type) {
        currentType = type;
        handler = type.newHandler(player);
        player.setAttached(
                Attachments.STAMINA,
                handler.initializeStamina(player, player.getAttachedOrCreate(Attachments.STAMINA)));
    }

    @Nullable
    public IParCoolStaminaHandler getHandler() {
        return handler;
    }

    public boolean isExhausted(LocalPlayer player) {
        return player.getAttachedOrCreate(Attachments.STAMINA).isExhausted();
    }

    public int getValue(LocalPlayer player) {
        return player.getAttachedOrCreate(Attachments.STAMINA).value();
    }

    public int getMax(LocalPlayer player) {
        return player.getAttachedOrCreate(Attachments.STAMINA).max();
    }

    public void consume(LocalPlayer player, int value) {
        if (player.isCreative() || player.isSpectator()) return;
        if (handler == null) return;
        if (isInfinite(player)) return;
        if (player.hasEffect(Effects.INEXHAUSTIBLE)) return;
        player.setAttached(
                Attachments.STAMINA,
                handler.consume(player, player.getAttachedOrCreate(Attachments.STAMINA), value));
    }

    public void recover(LocalPlayer player, int value) {
        if (player.isCreative() || player.isSpectator()) return;
        if (handler == null) return;
        player.setAttached(
                Attachments.STAMINA,
                handler.recover(player, player.getAttachedOrCreate(Attachments.STAMINA), value));
    }

    public void onTick(LocalPlayer player) {
        if (handler == null) return;
        player.setAttached(
                Attachments.STAMINA,
                handler.onTick(player, player.getAttachedOrCreate(Attachments.STAMINA)));
    }

    public boolean shouldShowHUD(LocalPlayer player) {
        if (handler == null) return false;
        return handler.shouldShowHUD(player);
    }

    public boolean imposeExhaustionPenalty(LocalPlayer player) {
        if (handler == null) return false;
        var current = player.getAttachedOrCreate(Attachments.STAMINA);
        return current.isExhausted() && handler.shouldImposeExhaustionPenalty(player, current);
    }

    private ReadonlyStamina oldStamina = ReadonlyStamina.createDefault();

    public void sync(LocalPlayer player) {
        ReadonlyStamina stamina = player.getAttachedOrCreate(Attachments.STAMINA);
        if (!stamina.equals(oldStamina)) {
            stamina.sync(player);
        }
        oldStamina = stamina;
    }

    public boolean isUsingExternalStamina() {
        return handler != null && handler.isExternalStamina();
    }
}
