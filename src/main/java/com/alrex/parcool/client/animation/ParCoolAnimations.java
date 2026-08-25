package com.alrex.parcool.client.animation;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.data.AnimationSet;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.registration.ID;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParCoolAnimations {
    public final ID<AnimationSet> FAST_RUN = AnimationSets.getInstance().register(
            ParCool.resourceLocation("fast_run"),
            (p) -> Parkourability.get(p).get(ParCoolActions.FAST_RUN).isDoing(),
            null
    );
    public final ID<AnimationSet> CRAWL = AnimationSets.getInstance().register(
            ParCool.resourceLocation("crawl"),
            (p) -> Parkourability.get(p).get(ParCoolActions.CRAWL).isDoing(),
            null
    );
    public final ID<AnimationSet> DODGE_RIGHT = AnimationSets.getInstance().registerTimeout(
            ParCool.resourceLocation("dodge_right"),
            100,
            null
    );
    public final ID<AnimationSet> DODGE_FRONT = AnimationSets.getInstance().registerTimeout(
            ParCool.resourceLocation("dodge_front"),
            100,
            null
    );
    public final ID<AnimationSet> DODGE_BACK = AnimationSets.getInstance().registerTimeout(
            ParCool.resourceLocation("dodge_back"),
            100,
            null
    );
    public final ID<AnimationSet> HANG_ON = AnimationSets.getInstance().register(
            ParCool.resourceLocation("hang_on"),
            (p) -> Parkourability.get(p).get(ParCoolActions.HANG_ON).isDoing(),
            null
    );
    public final ID<AnimationSet> CLIMB_UP = AnimationSets.getInstance().register(
            ParCool.resourceLocation("climb_up"),
            (p) -> Parkourability.get(p).get(ParCoolActions.CLIMB_UP).isDoing(),
            null
    );
    public final ID<AnimationSet> CLIMB_UP_JUMP = AnimationSets.getInstance().registerTimeout(
            ParCool.resourceLocation("climb_up_jump"),
            100, null
    );
    public final ID<AnimationSet> TRICK_JUMP_BACK = AnimationSets.getInstance().register(
            ParCool.resourceLocation("trick_jump_back"),
            (p) -> !p.onGround(),
            null
    );
    public final ID<AnimationSet> TRICK_JUMP_FORWARD = AnimationSets.getInstance().register(
            ParCool.resourceLocation("trick_jump_forward"),
            (p) -> !p.onGround(),
            null
    );
    public final ID<AnimationSet> STRIDE_JUMP = AnimationSets.getInstance().register(
            ParCool.resourceLocation("stride"),
            (p) -> !p.onGround(),
            null
    );
    public final ID<AnimationSet> SLIDE_DOWN = AnimationSets.getInstance().register(
            ParCool.resourceLocation("slide_down"),
            (p) -> Parkourability.get(p).get(ParCoolActions.SLIDE_DOWN).isDoing(),
            null
    );
    public final ID<AnimationSet> BREAKFALL_NO_MOVE = AnimationSets.getInstance().registerTimeout(
            ParCool.resourceLocation("breakfall_no_move"),
            100,
            null
    );
    public final ID<AnimationSet> BREAKFALL_FORWARD = AnimationSets.getInstance().registerTimeout(
            ParCool.resourceLocation("breakfall_forward"),
            100,
            null
    );
    public final ID<AnimationSet> HORIZONTAL_WALL_RUN = AnimationSets.getInstance().register(
            ParCool.resourceLocation("horizontal_wall_run"),
            (p) -> Parkourability.get(p).get(ParCoolActions.HORIZONTAL_WALL_RUN).isDoing(),
            null
    );
    public final ID<AnimationSet> SLIDE = AnimationSets.getInstance().register(
            ParCool.resourceLocation("slide"),
            (p) -> Parkourability.get(p).get(ParCoolActions.SLIDE).isDoing(),
            CRAWL
    );
    public final ID<AnimationSet> DIVE = AnimationSets.getInstance().register(
            ParCool.resourceLocation("dive"),
            (p) -> Parkourability.get(p).get(ParCoolActions.DIVE).isDoing(),
            null
    );
    public final ID<AnimationSet> SKYDIVE = AnimationSets.getInstance().register(
            ParCool.resourceLocation("skydive"),
            (p) -> Parkourability.get(p).get(ParCoolActions.SKYDIVE).isDoing(),
            DIVE
    );
    public final ID<AnimationSet> DIVE_IN_AIR = AnimationSets.getInstance().register(
            ParCool.resourceLocation("dive_in_air"),
            (p) -> Parkourability.get(p).get(ParCoolActions.DIVE).isDoing(),
            null
    );
    public final ID<AnimationSet> SKYDIVE_IN_AIR = AnimationSets.getInstance().register(
            ParCool.resourceLocation("skydive_in_air"),
            (p) -> Parkourability.get(p).get(ParCoolActions.SKYDIVE).isDoing(),
            DIVE_IN_AIR
    );
    public final ID<AnimationSet> DIVE_INTO_WATER = AnimationSets.getInstance().registerTimeout(
            ParCool.resourceLocation("dive_into_water"),
            100,
            null
    );
    public final ID<AnimationSet> JUMP_CHARGING = AnimationSets.getInstance().register(
            ParCool.resourceLocation("jump_charging"),
            (p) -> Parkourability.get(p).get(ParCoolActions.CHARGE_JUMP).isDoing(),
            null
    );
    public final ID<AnimationSet> CHARGE_JUMP = AnimationSets.getInstance().registerTimeout(
            ParCool.resourceLocation("charge_jump"),
            100,
            null
    );
    public final ID<AnimationSet> VAULT_FORWARD = AnimationSets.getInstance().registerTimeout(
            ParCool.resourceLocation("vault_forward"),
            100,
            null
    );
    public final ID<AnimationSet> VAULT_SIDE = AnimationSets.getInstance().registerTimeout(
            ParCool.resourceLocation("vault_side"),
            100,
            null
    );
    public final ID<AnimationSet> WALL_JUMP = AnimationSets.getInstance().registerTimeout(
            ParCool.resourceLocation("wall_jump"),
            100,
            null
    );
    public final ID<AnimationSet> HIDE_IN_BLOCK_STANDING = AnimationSets.getInstance().register(
            ParCool.resourceLocation("hide_in_block_stand"),
            (p) -> Parkourability.get(p).get(ParCoolActions.HIDE_IN_BLOCK).isDoing(),
            null
    );
    public final ID<AnimationSet> HIDE_IN_BLOCK_CRAWLING = AnimationSets.getInstance().register(
            ParCool.resourceLocation("hide_in_block_crawl"),
            (p) -> Parkourability.get(p).get(ParCoolActions.HIDE_IN_BLOCK).isDoing(),
            null
    );
    public final ID<AnimationSet> HANG_DOWN = AnimationSets.getInstance().register(
            ParCool.resourceLocation("hang_down"),
            (p) -> Parkourability.get(p).get(ParCoolActions.HANG_DOWN).isDoing(),
            null
    );
    public final ID<AnimationSet> HANG_DOWN_JUMP_FORWARD = AnimationSets.getInstance().register(
            ParCool.resourceLocation("hang_down_jump_forward"),
            (p) -> !p.onGround(),
            null
    );
    public final ID<AnimationSet> HANG_DOWN_JUMP_BACKWARD = AnimationSets.getInstance().register(
            ParCool.resourceLocation("hang_down_jump_backward"),
            (p) -> !p.onGround(),
            null
    );
    public final ID<AnimationSet> RIDE_ZIPLINE = AnimationSets.getInstance().register(
            ParCool.resourceLocation("ride_zipline"),
            (p) -> Parkourability.get(p).get(ParCoolActions.RIDE_ZIPLINE).isDoing(),
            null
    );
    public final ID<AnimationSet> WALL_RUN = AnimationSets.getInstance().registerTimeout(
            ParCool.resourceLocation("wall_run"),
            50,
            null
    );
    public final ID<AnimationSet> CASTAWAY = AnimationSets.getInstance().register(
            ParCool.resourceLocation("castaway"),
            (p) -> !p.onGround(),
            null
    );
    public final ID<AnimationSet> POLE_CLIMB = AnimationSets.getInstance().register(
            ParCool.resourceLocation("pole_climb"),
            (p) -> Parkourability.get(p).get(ParCoolActions.POLE_CLIMB).isDoing(),
            null
    );
    public final ID<AnimationSet> FAST_SWIM = AnimationSets.getInstance().register(
            ParCool.resourceLocation("fast_swim"),
            (p) -> Parkourability.get(p).get(ParCoolActions.FAST_SWIM).isDoing(),
            null
    );
    public final ID<AnimationSet> GRAPPLE = AnimationSets.getInstance().register(
            ParCool.resourceLocation("grapple"),
            (p) -> Parkourability.get(p).get(ParCoolActions.GRAPPLE).isDoing(),
            null
    );
    public final ID<AnimationSet> CREATIVE_FLY = AnimationSets.getInstance().register(
            ParCool.resourceLocation("creative_fly"),
            (p) -> p.getAbilities().flying,
            null
    );
}
