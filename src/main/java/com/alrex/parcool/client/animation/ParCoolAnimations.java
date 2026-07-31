package com.alrex.parcool.client.animation;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.data.AnimationSet;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.registration.ID;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import net.minecraft.resources.ResourceLocation;

public class ParCoolAnimations {
    public final ID<AnimationSet> FAST_RUN = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "fast_run"),
            (p) -> Parkourability.get(p).get(ParCoolActions.FAST_RUN).isDoing(),
            null
    );
    public final ID<AnimationSet> CRAWL = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "crawl"),
            (p) -> Parkourability.get(p).get(ParCoolActions.CRAWL).isDoing(),
            null
    );
    public final ID<AnimationSet> DODGE_RIGHT = AnimationSets.getInstance().registerTimeout(
            new ResourceLocation(ParCool.MOD_ID, "dodge_right"),
            100,
            null
    );
    public final ID<AnimationSet> DODGE_FRONT = AnimationSets.getInstance().registerTimeout(
            new ResourceLocation(ParCool.MOD_ID, "dodge_front"),
            100,
            null
    );
    public final ID<AnimationSet> DODGE_BACK = AnimationSets.getInstance().registerTimeout(
            new ResourceLocation(ParCool.MOD_ID, "dodge_back"),
            100,
            null
    );
    public final ID<AnimationSet> HANG_ON = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "hang_on"),
            (p) -> Parkourability.get(p).get(ParCoolActions.HANG_ON).isDoing(),
            null
    );
    public final ID<AnimationSet> CLIMB_UP = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "climb_up"),
            (p) -> Parkourability.get(p).get(ParCoolActions.CLIMB_UP).isDoing(),
            null
    );
    public final ID<AnimationSet> TRICK_JUMP_BACK = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "trick_jump_back"),
            (p) -> !p.isOnGround(),
            null
    );
    public final ID<AnimationSet> TRICK_JUMP_FORWARD = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "trick_jump_forward"),
            (p) -> !p.isOnGround(),
            null
    );
    public final ID<AnimationSet> STRIDE_JUMP = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "stride"),
            (p) -> !p.isOnGround(),
            null
    );
    public final ID<AnimationSet> SLIDE_DOWN = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "slide_down"),
            (p) -> Parkourability.get(p).get(ParCoolActions.SLIDE_DOWN).isDoing(),
            null
    );
    public final ID<AnimationSet> BREAKFALL_NO_MOVE = AnimationSets.getInstance().registerTimeout(
            new ResourceLocation(ParCool.MOD_ID, "breakfall_no_move"),
            100,
            null
    );
    public final ID<AnimationSet> BREAKFALL_FORWARD = AnimationSets.getInstance().registerTimeout(
            new ResourceLocation(ParCool.MOD_ID, "breakfall_forward"),
            100,
            null
    );
    public final ID<AnimationSet> HORIZONTAL_WALL_RUN = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "horizontal_wall_run"),
            (p) -> Parkourability.get(p).get(ParCoolActions.HORIZONTAL_WALL_RUN).isDoing(),
            null
    );
    public final ID<AnimationSet> SLIDE = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "slide"),
            (p) -> Parkourability.get(p).get(ParCoolActions.SLIDE).isDoing(),
            CRAWL
    );
    public final ID<AnimationSet> DIVE = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "dive"),
            (p) -> Parkourability.get(p).get(ParCoolActions.DIVE).isDoing(),
            null
    );
    public final ID<AnimationSet> SKYDIVE = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "skydive"),
            (p) -> Parkourability.get(p).get(ParCoolActions.SKYDIVE).isDoing(),
            DIVE
    );
    public final ID<AnimationSet> DIVE_IN_AIR = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "dive_in_air"),
            (p) -> Parkourability.get(p).get(ParCoolActions.DIVE).isDoing(),
            null
    );
    public final ID<AnimationSet> SKYDIVE_IN_AIR = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "skydive_in_air"),
            (p) -> Parkourability.get(p).get(ParCoolActions.SKYDIVE).isDoing(),
            DIVE_IN_AIR
    );
    public final ID<AnimationSet> DIVE_INTO_WATER = AnimationSets.getInstance().registerTimeout(
            new ResourceLocation(ParCool.MOD_ID, "dive_into_water"),
            100,
            null
    );
    public final ID<AnimationSet> JUMP_CHARGING = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "jump_charging"),
            (p) -> Parkourability.get(p).get(ParCoolActions.CHARGE_JUMP).isDoing(),
            null
    );
    public final ID<AnimationSet> CHARGE_JUMP = AnimationSets.getInstance().registerTimeout(
            new ResourceLocation(ParCool.MOD_ID, "charge_jump"),
            100,
            null
    );
    public final ID<AnimationSet> VAULT_FORWARD = AnimationSets.getInstance().registerTimeout(
            new ResourceLocation(ParCool.MOD_ID, "vault_forward"),
            100,
            null
    );
    public final ID<AnimationSet> VAULT_SIDE = AnimationSets.getInstance().registerTimeout(
            new ResourceLocation(ParCool.MOD_ID, "vault_side"),
            100,
            null
    );
    public final ID<AnimationSet> WALL_JUMP = AnimationSets.getInstance().registerTimeout(
            new ResourceLocation(ParCool.MOD_ID, "wall_jump"),
            100,
            null
    );
    public final ID<AnimationSet> HIDE_IN_BLOCK_STANDING = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "hide_in_block_stand"),
            (p) -> Parkourability.get(p).get(ParCoolActions.HIDE_IN_BLOCK).isDoing(),
            null
    );
    public final ID<AnimationSet> HIDE_IN_BLOCK_CRAWLING = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "hide_in_block_crawl"),
            (p) -> Parkourability.get(p).get(ParCoolActions.HIDE_IN_BLOCK).isDoing(),
            null
    );
    public final ID<AnimationSet> HANG_DOWN = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "hang_down"),
            (p) -> Parkourability.get(p).get(ParCoolActions.HANG_DOWN).isDoing(),
            null
    );
    public final ID<AnimationSet> HANG_DOWN_JUMP_FORWARD = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "hang_down_jump_forward"),
            (p) -> !p.isOnGround(),
            null
    );
    public final ID<AnimationSet> HANG_DOWN_JUMP_BACKWARD = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "hang_down_jump_backward"),
            (p) -> !p.isOnGround(),
            null
    );
    public final ID<AnimationSet> RIDE_ZIPLINE = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "ride_zipline"),
            (p) -> Parkourability.get(p).get(ParCoolActions.RIDE_ZIPLINE).isDoing(),
            null
    );
    public final ID<AnimationSet> WALL_RUN = AnimationSets.getInstance().registerTimeout(
            new ResourceLocation(ParCool.MOD_ID, "wall_run"),
            50,
            null
    );
    public final ID<AnimationSet> CASTAWAY = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "castaway"),
            (p) -> !p.isOnGround(),
            null
    );
    public final ID<AnimationSet> POLE_CLIMB = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "pole_climb"),
            (p) -> Parkourability.get(p).get(ParCoolActions.POLE_CLIMB).isDoing(),
            null
    );
    public final ID<AnimationSet> FAST_SWIM = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "fast_swim"),
            (p) -> Parkourability.get(p).get(ParCoolActions.FAST_SWIM).isDoing(),
            null
    );
}
