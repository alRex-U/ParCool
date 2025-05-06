package com.alrex.parcool.extern.paraglider;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.action.impl.*;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.resources.ResourceLocation;
import tictim.paraglider.api.movement.ParagliderPlayerStates;
import tictim.paraglider.api.movement.PlayerStateCondition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ParCoolPlayerStates {
    public static final Entry FAST_RUN = new Entry(FastRun.class)
            .parentID(ParagliderPlayerStates.IDLE)
            .priority(3);
    public static final Entry FAST_SWIM = new Entry(FastSwim.class).parentID(ParagliderPlayerStates.SWIMMING);
    public static final Entry CLING_TO_CLIFF = new Entry(ClingToCliff.class);
    public static final Entry BREAKFALL = new Entry(BreakfallReady.class)
            .condition(
                    (p, s, b, f) -> !p.isFallFlying()
            );
    public static final Entry DODGE = new Entry(Dodge.class).priority(4).parentID(ParagliderPlayerStates.IDLE, ParagliderPlayerStates.RUNNING);
    public static final Entry CLIMB_UP = new Entry(ClimbUp.class).parentID(CLING_TO_CLIFF.stateID());
    public static final Entry ROLL = new Entry(Roll.class).parentID(BREAKFALL.stateID())
            .condition(
                    (p, s, b, f) -> !p.isFallFlying()
            );
    public static final Entry HORIZONTAL_WALL_RUN = new Entry(HorizontalWallRun.class).parentID(ParagliderPlayerStates.MIDAIR);
    public static final Entry VERTICAL_WALL_RUN = new Entry(VerticalWallRun.class).parentID(ParagliderPlayerStates.MIDAIR);
    public static final Entry VAULT = new Entry(Vault.class).parentID(FAST_RUN.stateID());
    public static final Entry CATLEAP = new Entry(CatLeap.class).parentID(FAST_RUN.stateID());
    public static final Entry CHARGE_JUMP = new Entry(ChargeJump.class).parentID(ParagliderPlayerStates.MIDAIR);

    public static final List<Entry> ENTRIES = Arrays.asList(
            FAST_RUN,
            FAST_SWIM,
            CLING_TO_CLIFF,
            BREAKFALL,
            DODGE,
            CLIMB_UP,
            ROLL,
            HORIZONTAL_WALL_RUN,
            VERTICAL_WALL_RUN,
            VAULT,
            CATLEAP,
            CHARGE_JUMP
    );

    public record Entry(
            Class<? extends Action> clazz,
            ResourceLocation stateID,
            List<ResourceLocation> parentID,
            int staminaDelta,
            double priority,
            PlayerStateCondition condition
    ) {
        private Entry(Class<? extends Action> clazz) {
            this(
                    clazz,
                    new ResourceLocation(ParCool.MOD_ID, clazz.getSimpleName().toLowerCase()),
                    Collections.singletonList(ParagliderPlayerStates.IDLE),
                    -Math.min(15, ActionList.ACTION_REGISTRIES.get(ActionList.getIndexOf(clazz)).getDefaultStaminaConsumption()),
                    0,
                    (p, s, b, f) -> {
                        var parkourability = Parkourability.get(p);
                        if (parkourability == null) return false;
                        return parkourability.getClientInfo().getStaminaType() == IStamina.Type.Paraglider
                                && parkourability.get(clazz).isDoing();
                    }
            );
        }

        public Entry condition(PlayerStateCondition condition) {
            return new Entry(clazz, stateID, parentID, staminaDelta, priority, (p, s, b, f) -> this.condition.test(p, s, b, f) && condition.test(p, s, b, f));
        }

        public Entry priority(double value) {
            return new Entry(clazz, stateID, parentID, staminaDelta, value, condition);
        }

        public Entry staminaDelta(int value) {
            return new Entry(clazz, stateID, parentID, value, priority, condition);
        }

        public Entry parentID(ResourceLocation... value) {
            return new Entry(clazz, stateID, Arrays.stream(value).toList(), staminaDelta, priority, condition);
        }
    }
}
