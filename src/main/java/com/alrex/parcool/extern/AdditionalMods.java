package com.alrex.parcool.extern;

import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.extern.betterthirdperson.BetterThirdPersonManager;
import com.alrex.parcool.extern.paraglider.ParagliderManager;
import com.alrex.parcool.extern.shouldersurfing.ShoulderSurfingManager;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Arrays;
import java.util.function.Supplier;

public enum AdditionalMods {
    BETTER_THIRD_PERSON(BetterThirdPersonManager::new),
    SHOULDER_SURFING(ShoulderSurfingManager::new),
    PARAGLIDER(ParagliderManager::new);
    private final ModManager manager;

    AdditionalMods(Supplier<ModManager> supplier) {
        manager = supplier.get();
    }

    public static BetterThirdPersonManager betterThirdPerson() {
        return (BetterThirdPersonManager) BETTER_THIRD_PERSON.manager;
    }

    public static ShoulderSurfingManager shoulderSurfing() {
        return (ShoulderSurfingManager) SHOULDER_SURFING.manager;
    }

    public static ParagliderManager paraglider() {
        return (ParagliderManager) PARAGLIDER.manager;
    }

    public ModManager get() {
        return manager;
    }

    public static void init() {
        Arrays.stream(values()).map(AdditionalMods::get).forEach(ModManager::init);
    }

    public static void initInClient() {
        NeoForge.EVENT_BUS.register(AdditionalModsEventConsumer.Client.class);
        Arrays.stream(values()).map(AdditionalMods::get).forEach(ModManager::initInClient);
    }

    public static void initInDedicatedServer() {
        Arrays.stream(values()).map(AdditionalMods::get).forEach(ModManager::initInDedicatedServer);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isCameraDecoupled() {
        return shoulderSurfing().isCameraDecoupled() || betterThirdPerson().isCameraDecoupled();
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isUsingExternalStamina() {
        var player = Minecraft.getInstance().player;
        if (player == null) return false;
        return LocalStamina.get(player).isUsingExternalStamina();
    }
}
