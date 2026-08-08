package com.alrex.parcool.client.animation;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.IBlendingFactor;
import com.alrex.parcool.client.animation.system.SimpleBlendFactor;
import com.alrex.parcool.client.animation.system.math.EasingFunctions;
import com.alrex.parcool.client.animation.system.registration.BlendingFactors;
import com.alrex.parcool.client.animation.system.registration.ID;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParCoolBlendingFactors {
    public final ID<IBlendingFactor> HANG_ON_LEFT_TO_WALL = BlendingFactors.getInstance()
            .register(ParCool.resourceLocation("builtin/hang_on_left_to_wall"), (args, method) ->
                    new SimpleBlendFactor((player) -> Parkourability.get(player).get(ParCoolActions.HANG_ON).getBlendFactorLeftToWall(), method)
            );
    public final ID<IBlendingFactor> HANG_ON_RIGHT_TO_WALL = BlendingFactors.getInstance()
            .register(ParCool.resourceLocation("builtin/hang_on_right_to_wall"), (args, method) ->
                    new SimpleBlendFactor((player) -> Parkourability.get(player).get(ParCoolActions.HANG_ON).getBlendFactorRightToWall(), method)
            );
    public final ID<IBlendingFactor> HANG_ON_BACK_TO_WALL = BlendingFactors.getInstance()
            .register(ParCool.resourceLocation("builtin/hang_on_back_to_wall"), (args, method) ->
                    new SimpleBlendFactor((player) -> Parkourability.get(player).get(ParCoolActions.HANG_ON).getBlendFactorBackToWall(), method)
            );
    public final ID<IBlendingFactor> HANG_ON_MOVING_LEFT = BlendingFactors.getInstance()
            .register(ParCool.resourceLocation("builtin/hang_on_moving_left"), (args, method) ->
                    new SimpleBlendFactor((player) -> Parkourability.get(player).get(ParCoolActions.HANG_ON).getBlendFactorMovingLeft(), method)
            );
    public final ID<IBlendingFactor> SLIDE_DOWN_LEFT_TO_WALL = BlendingFactors.getInstance()
            .register(ParCool.resourceLocation("builtin/slide_down_left_to_wall"), (args, method) ->
                    new SimpleBlendFactor((player) -> Parkourability.get(player).get(ParCoolActions.SLIDE_DOWN).getBlendFactorLeftToWall(), method)
            );
    public final ID<IBlendingFactor> SLIDE_DOWN_RIGHT_TO_WALL = BlendingFactors.getInstance()
            .register(ParCool.resourceLocation("builtin/slide_down_right_to_wall"), (args, method) ->
                    new SimpleBlendFactor((player) -> Parkourability.get(player).get(ParCoolActions.SLIDE_DOWN).getBlendFactorRightToWall(), method)
            );
    public final ID<IBlendingFactor> SLIDE_DOWN_BACK_TO_WALL = BlendingFactors.getInstance()
            .register(ParCool.resourceLocation("builtin/slide_down_back_to_wall"), (args, method) ->
                    new SimpleBlendFactor((player) -> Parkourability.get(player).get(ParCoolActions.SLIDE_DOWN).getBlendFactorBackToWall(), method)
            );
    public final ID<IBlendingFactor> SKYDIVE_LEAN_FORWARD = BlendingFactors.getInstance()
            .register(ParCool.resourceLocation("builtin/skydive_lean_forward"), (args, method) ->
                    new SimpleBlendFactor((player) -> EasingFunctions.QUAD.easeInOut(Parkourability.get(player).get(ParCoolActions.SKYDIVE).getBlendingFactorLeanForward()), method)
            );
    public final ID<IBlendingFactor> SKYDIVE_LEAN_BACKWARD = BlendingFactors.getInstance()
            .register(ParCool.resourceLocation("builtin/skydive_lean_backward"), (args, method) ->
                    new SimpleBlendFactor((player) -> EasingFunctions.QUAD.easeInOut(Parkourability.get(player).get(ParCoolActions.SKYDIVE).getBlendingFactorLeanBackward()), method)
            );
    public final ID<IBlendingFactor> SKYDIVE_LEAN_LEFT = BlendingFactors.getInstance()
            .register(ParCool.resourceLocation("builtin/skydive_lean_left"), (args, method) ->
                    new SimpleBlendFactor((player) -> EasingFunctions.QUAD.easeInOut(Parkourability.get(player).get(ParCoolActions.SKYDIVE).getBlendingFactorLeanLeft()), method)
            );
    public final ID<IBlendingFactor> SKYDIVE_LEAN_RIGHT = BlendingFactors.getInstance()
            .register(ParCool.resourceLocation("builtin/skydive_lean_right"), (args, method) ->
                    new SimpleBlendFactor((player) -> EasingFunctions.QUAD.easeInOut(Parkourability.get(player).get(ParCoolActions.SKYDIVE).getBlendingFactorLeanRight()), method)
            );
    public final ID<IBlendingFactor> HANG_DOWN_ORTHOGONAL = BlendingFactors.getInstance()
            .register(ParCool.resourceLocation("builtin/hang_down_orthogonally"), (args, method) ->
                    new SimpleBlendFactor((player) -> EasingFunctions.QUAD.easeInOut(Parkourability.get(player).get(ParCoolActions.HANG_DOWN).getBlendFactorOrthogonalToBar(1f)), method)
            );
}
