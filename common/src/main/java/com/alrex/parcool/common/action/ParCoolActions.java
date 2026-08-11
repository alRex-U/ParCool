package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.LogicalSide;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.api.action.ActionGroup;
import com.alrex.parcool.api.action.ActionOption;
import com.alrex.parcool.api.action.StaminaConsumption;
import com.alrex.parcool.common.action.impl.*;
import net.minecraft.world.entity.Pose;

public class ParCoolActions {
    private static final ActionGroup GROUP;

    public static final ActionEntry<FastRun> FAST_RUN;
    public static final ActionEntry<FastSwim> FAST_SWIM;
    public static final ActionEntry<Vault> VAULT;
    public static final ActionEntry<Dive> DIVE;
    public static final ActionEntry<Skydive> SKYDIVE;
    public static final ActionEntry<Crawl> CRAWL;
    public static final ActionEntry<Slide> SLIDE;
    public static final ActionEntry<HangOn> HANG_ON;
    public static final ActionEntry<HangDown> HANG_DOWN;
    public static final ActionEntry<Dodge> DODGE;
    public static final ActionEntry<ClimbUp> CLIMB_UP;
    public static final ActionEntry<Castaway> CASTAWAY;
    public static final ActionEntry<TrickJump> TRICK_JUMP;
    public static final ActionEntry<SlideDown> SLIDE_DOWN;
    public static final ActionEntry<Breakfall> BREAKFALL;
    public static final ActionEntry<HorizontalWallRun> HORIZONTAL_WALL_RUN;
    public static final ActionEntry<ChargeJump> CHARGE_JUMP;
    public static final ActionEntry<WallJump> WALL_JUMP;
    public static final ActionEntry<HideInBlock> HIDE_IN_BLOCK;
    public static final ActionEntry<RideZipline> RIDE_ZIPLINE;
    public static final ActionEntry<WallRun> WALL_RUN;
    public static final ActionEntry<PoleClimb> POLE_CLIMB;

    static {
        var builder = new ActionGroup.Builder(ParCool.MOD_ID);
        WALL_JUMP = builder.add("wall_jump", WallJump.class, WallJump::new, new ActionOption()
                .cost(StaminaConsumption.get(50, 0, 0))
                .needNotOnGround(true)
                .learningCost(5)
        );

        RIDE_ZIPLINE = builder.add("ride_zipline", RideZipline.class, RideZipline::new, new ActionOption()
                .cost(StaminaConsumption.get(0, 2, 0))
                .needNotOnGround(true)
                .availableInFluid(true)
                .learningCost(5)
        );

        WALL_RUN = builder.add("wall_run", WallRun.class, WallRun::new, new ActionOption()
                .cost(StaminaConsumption.get(50, 0, 0))
                .needNotOnGround(true)
                .learningCost(5)
        );

        FAST_RUN = builder.add("fast_run", FastRun.class, FastRun::new, new ActionOption()
                .processedAfter(WALL_RUN)
                .cost(StaminaConsumption.get(0, 2, 0))
                .learningCost(1)
        );
        {
            VAULT = builder.add("vault", Vault.class, Vault::new, new ActionOption()
                    .parent(FAST_RUN)
                    .cost(StaminaConsumption.get(50, 0, 0))
                    .learningCost(5)
            );
            HORIZONTAL_WALL_RUN = builder.add("horizontal_wall_run", HorizontalWallRun.class, HorizontalWallRun::new, new ActionOption()
                    .parent(FAST_RUN)
                    .needNotOnGround(true)
                    .cost(StaminaConsumption.get(0, 3, 0))
                    .learningCost(7)
            );
        }

        FAST_SWIM = builder.add("fast_swim", FastSwim.class, FastSwim::new, new ActionOption()
                .availableInFluid(true)
                .availableNotInFluid(false)
                .needPose(Pose.SWIMMING)
                .cost(StaminaConsumption.get(0, 2, 0))
                .learningCost(3)
        );

        DIVE = builder.add("dive", Dive.class, Dive::new, new ActionOption()
                .processedAfter(FAST_RUN)
                .needNotOnGround(true)
                .learningCost(3)
        );
        {
            SKYDIVE = builder.add("skydive", Skydive.class, Skydive::new, new ActionOption()
                    .parent(DIVE)
                    .learningCost(5)
            );
        }

        TRICK_JUMP = builder.add("trick_jump", TrickJump.class, TrickJump::new, new ActionOption()
                .processedAfter(DIVE)
                .learningCost(3)
        );

        HIDE_IN_BLOCK = builder.add("hide_in_block", HideInBlock.class, HideInBlock::new, new ActionOption()
                .needPose(null)
                .learningCost(8)
        );

        CRAWL = builder.add("crawl", Crawl.class, Crawl::new, new ActionOption()
                .processedAfter(HIDE_IN_BLOCK)
                .needPose(null)
                .learningCost(1)
        );
        {
            SLIDE = builder.add("slide", Slide.class, Slide::new, new ActionOption()
                    .parent(CRAWL)
                    .needPose(null)
                    .learningCost(10)
            );
        }

        HANG_ON = builder.add("hang_on", HangOn.class, HangOn::new, new ActionOption()
                .cost(StaminaConsumption.get(0, 3, 0))
                .learningCost(1)
        );

        CLIMB_UP = builder.add("climb_up", ClimbUp.class, ClimbUp::new, new ActionOption()
                .processedAfter(HANG_ON)
                .cost(StaminaConsumption.get(50, 0, 0))
                .learningCost(10)
        );

        CASTAWAY = builder.add("castaway", Castaway.class, Castaway::new, new ActionOption()
                .processedAfter(HANG_ON)
                .needNotOnGround(true)
                .cost(StaminaConsumption.get(20, 0, 0))
                .learningCost(3)
        );

        HANG_DOWN = builder.add("hang_down", HangDown.class, HangDown::new, new ActionOption()
                .processedAfter(HANG_ON)
                .needNotOnGround(true)
                .learningCost(8)
        );

        POLE_CLIMB = builder.add("pole_climb", PoleClimb.class, PoleClimb::new, new ActionOption()
                .processedAfter(HANG_ON)
                .cost(StaminaConsumption.get(0, 1, 0))
                .learningCost(10)
        );

        SLIDE_DOWN = builder.add("slide_down", SlideDown.class, SlideDown::new, new ActionOption()
                .processedAfter(HANG_ON, CLIMB_UP, CASTAWAY, POLE_CLIMB)
                .needNotOnGround(true)
                .cost(StaminaConsumption.get(0, 1, 0))
                .learningCost(10)
        );

        DODGE = builder.add("dodge", Dodge.class, Dodge::new, new ActionOption()
                .needOnGround(true)
                .cost(StaminaConsumption.get(50, 0, 0))
                .learningCost(12)
        );

        BREAKFALL = builder.add("breakfall", Breakfall.class, Breakfall::new, new ActionOption()
                .triggeredSide(LogicalSide.SERVER)
                .cost(StaminaConsumption.get(50, 0, 0))
                .learningCost(10)
        );

        CHARGE_JUMP = builder.add("charge_jump", ChargeJump.class, ChargeJump::new, new ActionOption()
                .cost(StaminaConsumption.get(0, 0, 50))
                .needPose(null)
                .learningCost(10)
        );

        GROUP = builder.build();
    }

    public static void onRegister(ActionRegistry registry) {
        registry.register(GROUP);
    }
}
