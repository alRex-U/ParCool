package com.alrex.parcool.extern;

import com.alrex.parcool.extern.betterthirdperson.BetterThirdPersonManager;
import com.alrex.parcool.extern.shouldersurfing.ShoulderSurfingManager;
import java.util.Arrays;
import java.util.function.Supplier;

public enum AdditionalMods {
    BETTER_THIRD_PERSON(BetterThirdPersonManager::new),
    SHOULDER_SURFING(ShoulderSurfingManager::new);
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

    public ModManager get() {
        return manager;
    }

    public static void init() {
        Arrays.stream(values()).map(AdditionalMods::get).forEach(ModManager::init);
    }

    public static boolean isCameraDecoupled() {
        return shoulderSurfing().isCameraDecoupled() || betterThirdPerson().isCameraDecoupled();
    }
}
