package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.client.event.types.render.ValidatePlayerModelEvent;
import yesman.epicfight.api.event.subscription.DefaultEventSubscription;

/**
 * Epic Fight renders patched living entities through its own skinned mesh: {@code RenderEngine.onRenderLiving}
 * calls {@code renderEntityArmatureModel} and then cancels the vanilla {@code RenderLivingEvent.Pre} whenever
 * {@code LivingEntityPatch.overrideRender()} returns true. For a player that method returns
 * {@code ValidatePlayerModelEvent.getShouldRender()} (initialised from {@code ClientConfig.enableOriginalModel}
 * and the current combat/vanilla mode).
 * <p>
 * ParCool poses the vanilla model only: {@code PlayerModelMixin} redirects {@code HumanoidModel.setupAnim} and
 * {@code PlayerRendererMixin} injects into {@code setupRotations}. While Epic Fight owns the render pass those
 * hooks never run, so parkour animations have no surface to be drawn on. Vetoing the Epic Fight model for the
 * frames in which a ParCool animation is actually working hands the pass back to the vanilla renderer, which is
 * the exact path ParCool already animates.
 */
@OnlyIn(Dist.CLIENT)
public final class EpicFightRenderCompat {
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean registered = false;

    private EpicFightRenderCompat() {
    }

    public static void init() {
        if (registered) return;
        registered = true;
        // Explicit subscriber name and priority 0: the hook only exposes the default (unnamed) registration
        // otherwise, and the name is what shows up in EventContext diagnostics.
        EpicFightClientEventHooks.Render.VALIDATE_PLAYER_MODEL_TO_RENDER.registerEvent(
                (DefaultEventSubscription<ValidatePlayerModelEvent>) EpicFightRenderCompat::onValidatePlayerModel,
                "parcool",
                0
        );
        LOGGER.debug("Registered Epic Fight player model veto for ParCool animations");
    }

    private static void onValidatePlayerModel(ValidatePlayerModelEvent event) {
        var player = event.getPlayerPatch().getOriginal();
        if (!(player instanceof IPlayerAnimatorHolder holder)) return;
        // null while no ParCool animation set is working, so idle, combat and attack poses keep Epic Fight's model.
        if (holder.getParCoolPlayerAnimator().getCurrentTransformation() == null) return;
        event.setShouldRender(false);
    }
}
