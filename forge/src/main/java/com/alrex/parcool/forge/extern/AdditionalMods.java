package com.alrex.parcool.forge.extern;

import com.alrex.parcool.client.input.LogicalMovement;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.forge.extern.betterthirdperson.BetterThirdPersonManager;
import com.alrex.parcool.forge.extern.curios.CuriosManager;
import com.alrex.parcool.forge.extern.epicfight.EpicFightManager;
import com.alrex.parcool.forge.extern.feathers.FeathersManager;
import com.alrex.parcool.forge.extern.shouldersurfing.ShoulderSurfingManager;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Supplier;

public enum AdditionalMods {
    CURIOS(CuriosManager::new),
    BETTER_THIRD_PERSON(BetterThirdPersonManager::new),
    SHOULDER_SURFING(ShoulderSurfingManager::new),
    FEATHERS(FeathersManager::new),
    EPIC_FIGHT(EpicFightManager::new);

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

    public static BetterThirdPersonManager betterThirdPerson() {
        return (BetterThirdPersonManager) BETTER_THIRD_PERSON.get();
    }

    public static ShoulderSurfingManager shoulderSurfing() {
        return (ShoulderSurfingManager) SHOULDER_SURFING.get();
    }

    public static FeathersManager feathers() {
        return (FeathersManager) FEATHERS.get();
    }

    public static EpicFightManager epicFight() {
        return (EpicFightManager) EPIC_FIGHT.get();
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

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public static ParCoolKeyBinds.LogicalInput getLogicalKey(LogicalMovement movement) {
        var input = shoulderSurfing().getLogicalKey(movement);
        if (input != null) return input;
        input = betterThirdPerson().getLogicalKey(movement);
        return input;
    }
}
