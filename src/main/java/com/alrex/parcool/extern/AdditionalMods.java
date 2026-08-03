package com.alrex.parcool.extern;

import com.alrex.parcool.client.input.LogicalMovement;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.extern.betterthirdperson.BetterThirdPersonManager;
import com.alrex.parcool.extern.curios.CuriosManager;
import com.alrex.parcool.extern.feathers.FeathersManager;
import com.alrex.parcool.extern.shouldersurfing.ShoulderSurfingManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Supplier;

public enum AdditionalMods {
    CURIOS(CuriosManager::new),
    BETTER_THIRD_PERSON(BetterThirdPersonManager::new),
    SHOULDER_SURFING(ShoulderSurfingManager::new),
    FEATHERS(FeathersManager::new);

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
