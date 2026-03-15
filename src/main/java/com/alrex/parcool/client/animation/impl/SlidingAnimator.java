package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class SlidingAnimator extends Animator {
	private static final int MAX_TRANSITION_TICK = 5;
	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
        Slide slide = parkourability.get(Slide.class);
        return !slide.isDoing() && slide.getNotDoingTick() >= MAX_TRANSITION_TICK;
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float animFactor = getAnimFactor(parkourability, transformer.getPartialTick());

		transformer
                .translateLeftLeg(
                        0,
                        -1.2f * animFactor,
                        -2f * animFactor
                )
                .translateRightArm(
                        0,
                        1.2f * animFactor,
                        1.2f * animFactor
                )
                .translateHead(0, 0, -animFactor)
                .rotateHeadPitch(50 * animFactor)
                .rotateAdditionallyHeadYaw(50 * animFactor)
                .rotateAdditionallyHeadRoll(-10 * animFactor)
                .rotateRightArm((float) Math.toRadians(50), (float) Math.toRadians(-40), 0, animFactor)
                .rotateLeftArm((float) Math.toRadians(20), 0, (float) Math.toRadians(-100), animFactor)
                .rotateRightLeg((float) Math.toRadians(-30), (float) Math.toRadians(40), 0, animFactor)
                .rotateLeftLeg((float) Math.toRadians(40), (float) Math.toRadians(-30), (float) Math.toRadians(15), animFactor)
				.makeLegsLittleMoving()
				.makeArmsNatural()
				.end();
	}

	@Override
    public boolean rotatePre(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        Vec3 vec = parkourability.get(Slide.class).getSlidingVector();
        if (vec == null) return false;
        float animFactor = getAnimFactor(parkourability, rotator.getPartialTick());
        float yRot = parkourability.get(Slide.class).isDoing() ? (float) VectorUtil.toYawDegree(vec) : rotator.getYRot();
		rotator
                .rotateYawRightward(180f + yRot)
                .rotatePitchFrontward(-55f * animFactor)
                .translate(0.35f * animFactor, 0, 0)
                .rotateYawRightward(-55f * animFactor)
                .translate(0, -0.7f * animFactor, -0.3f * animFactor);
        return true;
	}

    private float getAnimFactor(Parkourability parkourability, float partialTick) {
        Slide slide = parkourability.get(Slide.class);
        boolean doing = slide.isDoing();
        int tick = doing ? getTick() : slide.getNotDoingTick();
        float animFactor = Math.min((tick + partialTick) / MAX_TRANSITION_TICK, 1);
        if (!doing) {
            animFactor = 1 - animFactor;
        }
        animFactor = new Easing(animFactor)
                .sinInOut(0, 1, 0, 1)
                .get();
        return animFactor;
    }
}
