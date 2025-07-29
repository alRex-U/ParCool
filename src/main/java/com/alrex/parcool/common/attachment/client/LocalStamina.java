package com.alrex.parcool.common.attachment.client;

import com.alrex.parcool.api.Effects;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.ClientAttachments;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.common.stamina.handlers.InfiniteStaminaHandler;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class LocalStamina {
    @Nullable
    private StaminaType currentType = null;
    @Nullable
    private IParCoolStaminaHandler handler = null;

    public static LocalStamina get(LocalPlayer player) {
        return player.getData(ClientAttachments.LOCAL_STAMINA);
    }

    public boolean isAvailable() {
        return handler != null && currentType != null;
    }

    public boolean isInfinite(LocalPlayer player) {
        return player.isCreative() || player.isSpectator() || handler instanceof InfiniteStaminaHandler;
    }

    public void changeType(LocalPlayer player, StaminaType type) {
        currentType = type;
        handler = type.newHandler();
        player.setData(Attachments.STAMINA, handler.initializeStamina(player, player.getData(Attachments.STAMINA)));
    }

    public boolean isExhausted(LocalPlayer player) {
        return player.getData(Attachments.STAMINA).isExhausted();
    }

    public int getValue(LocalPlayer player) {
        return player.getData(Attachments.STAMINA).value();
    }

    public int getMax(LocalPlayer player) {
        return player.getData(Attachments.STAMINA).max();
    }

    public void consume(LocalPlayer player, int value) {
        if (player.isCreative() || player.isSpectator()) return;
        if (handler == null) return;
        if (player.hasEffect(Effects.INEXHAUSTIBLE)) return;
        player.setData(
                Attachments.STAMINA,
                handler.consume(player, player.getData(Attachments.STAMINA), value)
        );
    }

    public void recover(LocalPlayer player, int value) {
        if (player.isCreative() || player.isSpectator()) return;
        if (handler == null) return;
        player.setData(
                Attachments.STAMINA,
                handler.recover(player, player.getData(Attachments.STAMINA), value)
        );
    }

    public void onTick(LocalPlayer player) {
        if (handler == null) return;
        player.setData(
                Attachments.STAMINA,
                handler.onTick(player, player.getData(Attachments.STAMINA))
        );
    }

    public void sync(LocalPlayer player) {
        player.getData(Attachments.STAMINA).sync(player);
    }
}
