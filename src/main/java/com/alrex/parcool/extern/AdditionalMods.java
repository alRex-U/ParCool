package com.alrex.parcool.extern;

import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.extern.tacz.TaCZAnimationCompat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

/**
 * Из внешних интеграций жива только TaCZ (см. extern/tacz).
 *
 * <p>ponytail: интеграции с Better Third Person, Shoulder Surfing, Paraglider и Epic Fight
 * выброшены — ни одного из этих модов нет в паке Blockfield, а под Fabric 1.21.1 у них нет и
 * сборок, на которые можно компилироваться. Вернуть, если такой мод появится в паке (код лежит в
 * апстрим-ветке 1.21.1-NF, пакеты extern/betterthirdperson, extern/shouldersurfing,
 * extern/paraglider, extern/epicfight).
 */
public final class AdditionalMods {
    private AdditionalMods() {}

    public static void init() {}

    @Environment(EnvType.CLIENT)
    public static void initInClient() {
        if (FabricLoader.getInstance().isModLoaded("tacz")) {
            TaCZAnimationCompat.register();
        }
    }

    public static void initInDedicatedServer() {}

    @Environment(EnvType.CLIENT)
    public static boolean isCameraDecoupled() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public static boolean isUsingExternalStamina() {
        var player = Minecraft.getInstance().player;
        if (player == null) return false;
        return LocalStamina.get(player).isUsingExternalStamina();
    }
}
