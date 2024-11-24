package com.alrex.parcool.extern.paraglider;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.api.movement.MovementPlugin;
import tictim.paraglider.api.plugin.ParagliderPlugin;


@ParagliderPlugin
public class ParCoolPlugin implements MovementPlugin {
    @Override
    public void registerNewStates(@NotNull PlayerStateRegister register) {
        ParCoolPlayerStates.ENTRIES.forEach(it -> {
            register.register(it.stateID(), it.staminaDelta());
        });
    }

    @Override public void registerStateConnections(@NotNull PlayerStateConnectionRegister register) {
        ParCoolPlayerStates.ENTRIES.forEach(it -> {
            for (ResourceLocation parent : it.parentID()) {
                register.addBranch(parent, it.condition(), it.stateID(), it.priority());
            }
        });
    }
}
