package com.alrex.parcool.extern.paraglider;

import com.alrex.parcool.common.action.impl.*;
import com.alrex.parcool.common.capability.Parkourability;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.api.movement.MovementPlugin;
import tictim.paraglider.api.plugin.ParagliderPlugin;

import java.util.Objects;

import static com.alrex.parcool.extern.paraglider.ParCoolPlayerStates.*;
import static tictim.paraglider.api.movement.ParagliderPlayerStates.*;
import static tictim.paraglider.api.movement.ParagliderPlayerStates.Flags.FLAG_RUNNING;

@ParagliderPlugin
public class ParCoolPlugin implements MovementPlugin {
    @Override
    public void registerNewStates(@NotNull PlayerStateRegister register) {
        register.register(FAST_RUNNING, FAST_RUNNING_STAMINA_DELTA, FLAG_RUNNING);
        register.register(FAST_SWIMMING, FAST_SWIMMING_STAMINA_DELTA, FLAG_RUNNING);
        register.register(DODGE, DODGE_STAMINA_DELTA, FLAG_RUNNING);
        register.register(CLING_TO_CLIFF, CLING_TO_CLIFF_STAMINA_DELTA, FLAG_RUNNING);
        register.register(CLIMB_UP, CLIMB_UP_STAMINA_DELTA, FLAG_RUNNING);
        register.register(BREAKFALL, BREAKFALL_STAMINA_DELTA, FLAG_RUNNING);
        register.register(ROLL, ROLL_STAMINA_DELTA, FLAG_RUNNING);
        register.register(HORIZONTAL_WALL_RUN, HORIZONTAL_WALL_RUN_STAMINA_DELTA, FLAG_RUNNING);
        register.register(VERTICAL_WALL_RUN, VERTICAL_WALL_RUN_STAMINA_DELTA, FLAG_RUNNING);
        register.register(VAULT, VAULT_STAMINA_DELTA, FLAG_RUNNING);
        register.register(CAT_LEAP, CAT_LEAP_STAMINA_DELTA, FLAG_RUNNING);
        register.register(CHARGE_JUMP, CHARGE_JUMP_STAMINA_DELTA, FLAG_RUNNING);
    }

    @Override public void registerStateConnections(@NotNull PlayerStateConnectionRegister register) {
        register.addBranch(RUNNING, (p, s, b, f) -> Objects.requireNonNull(Parkourability.get(p)).get(FastRun.class).isDoing(), FAST_RUNNING, FAST_RUNNING_PRIORITY);
        register.addBranch(SWIMMING, (p, s, b, f) -> Objects.requireNonNull(Parkourability.get(p)).get(FastSwim.class).isDoing(), FAST_SWIMMING);
        register.addBranch(IDLE, (p, s, b, f) -> Objects.requireNonNull(Parkourability.get(p)).get(Dodge.class).isDoing(), DODGE, DODGE_PRIORITY);
        register.addBranch(RUNNING, (p, s, b, f) -> Objects.requireNonNull(Parkourability.get(p)).get(Dodge.class).isDoing(), DODGE, DODGE_PRIORITY);
        register.addBranch(IDLE, (p, s, b, f) -> Objects.requireNonNull(Parkourability.get(p)).get(ClingToCliff.class).isDoing(), CLING_TO_CLIFF);
        register.addBranch(CLING_TO_CLIFF, (p, s, b, f) -> Objects.requireNonNull(Parkourability.get(p)).get(ClimbUp.class).isDoing(), CLIMB_UP);
        register.addBranch(IDLE, (p, s, b, f) -> Objects.requireNonNull(Parkourability.get(p)).get(BreakfallReady.class).isDoing() && !p.isFallFlying(), BREAKFALL);
        register.addBranch(BREAKFALL, (p, s, b, f) -> Objects.requireNonNull(Parkourability.get(p)).get(Roll.class).isDoing() && !p.isFallFlying(), ROLL);
        register.addBranch(MIDAIR, (p, s, b, f) -> Objects.requireNonNull(Parkourability.get(p)).get(HorizontalWallRun.class).isDoing(), HORIZONTAL_WALL_RUN);
        register.addBranch(MIDAIR, (p, s, b, f) -> Objects.requireNonNull(Parkourability.get(p)).get(VerticalWallRun.class).isDoing(), VERTICAL_WALL_RUN);
        register.addBranch(FAST_RUNNING, (p, s, b, f) -> Objects.requireNonNull(Parkourability.get(p)).get(Vault.class).isDoing(), VAULT);
        register.addBranch(FAST_RUNNING, (p, s, b, f) -> Objects.requireNonNull(Parkourability.get(p)).get(CatLeap.class).isDoing(), CAT_LEAP);
        register.addBranch(MIDAIR, (p, s, b, f) -> Objects.requireNonNull(Parkourability.get(p)).get(ChargeJump.class).isDoing(), CHARGE_JUMP);
    }
}
