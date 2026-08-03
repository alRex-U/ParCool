package com.alrex.parcool.extern;

import com.alrex.parcool.extern.curios.CuriosManager;

import java.util.Arrays;
import java.util.function.Supplier;

public enum AdditionalMods {
    CURIOS(CuriosManager::new);
    private final ModManager manager;

    AdditionalMods(Supplier<ModManager> supplier) {
        manager = supplier.get();
    }

    public ModManager get() {
        return manager;
    }

    public static CuriosManager curios() {
        return (CuriosManager) CURIOS.get();
    }

    public static void init() {
        Arrays.stream(values()).map(AdditionalMods::get).forEach(ModManager::init);
    }

    public static void initInClient() {
        Arrays.stream(values()).map(AdditionalMods::get).forEach(ModManager::initInClient);
    }

    public static void initInDedicatedServer() {
        Arrays.stream(values()).map(AdditionalMods::get).forEach(ModManager::initInDedicatedServer);
    }
}
