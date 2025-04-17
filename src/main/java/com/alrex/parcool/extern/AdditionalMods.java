package com.alrex.parcool.extern;

import com.alrex.parcool.extern.betterthirdperson.BetterThirdPersonManager;

import java.util.Arrays;
import java.util.function.Supplier;

public enum AdditionalMods {
    BETTER_THIRD_PERSON(BetterThirdPersonManager::new);
    private final ModManager manager;

    AdditionalMods(Supplier<ModManager> supplier) {
        manager = supplier.get();
    }

    public static BetterThirdPersonManager betterThirdPerson() {
        return (BetterThirdPersonManager) BETTER_THIRD_PERSON.manager;
    }

    public ModManager get() {
        return manager;
    }

    public static void init() {
        Arrays.stream(values()).map(AdditionalMods::get).forEach(ModManager::init);
    }
}
