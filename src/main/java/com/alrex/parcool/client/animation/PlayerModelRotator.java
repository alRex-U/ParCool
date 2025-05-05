package com.alrex.parcool.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PlayerModelRotator {
	private final PoseStack stack;
	private final Player player;
	private final float partial;
	private double playerHeight = 1.8;
	private final float givenYRot;
	private final PlayerRenderState state;


    public float getYRot() {
        return givenYRot;
    }

	public float getPartialTick() {
		return partial;
	}

	private boolean basedCenter = false;
	private boolean basedTop = false;

	public PlayerModelRotator(PoseStack stack, Player player, PlayerRenderState state, float partial, float yRot) {
		this.stack = stack;
		this.state = state;
		this.player = player;
		this.partial = partial;
        this.givenYRot = yRot;
		switch (player.getPose()) {
			case SWIMMING:
			case CROUCHING:
			case SLEEPING:
				playerHeight = 0.6;
		}
	}

	public PlayerModelRotator start() {
		return this;
	}

	public PlayerModelRotator startBasedCenter() {
		basedCenter = true;
		stack.translate(0, playerHeight / 2, 0);
		return this;
	}

	public PlayerModelRotator startBasedTop() {
		basedTop = true;
		stack.translate(0, playerHeight, 0);
		return this;
	}

    public PoseStack getRawStack() {
        return stack;
    }

	public PlayerModelRotator translateY(float offset) {
		stack.translate(0, offset, 0);
		return this;
	}

    public PlayerModelRotator translate(float offsetX, float offsetY, float offsetZ) {
        stack.translate(offsetX, offsetY, offsetZ);
        return this;
    }

	public PlayerModelRotator rotatePitchFrontward(float angleDegree) {
        stack.mulPose(Axis.XN.rotationDegrees(angleDegree));
		return this;
	}

	public PlayerModelRotator rotateRollRightward(float angleDegree) {
        stack.mulPose(Axis.ZN.rotationDegrees(angleDegree));
		return this;
	}

	public PlayerModelRotator rotateYawRightward(float angleDegree) {
		stack.mulPose(Axis.YN.rotationDegrees(angleDegree));
		return this;
	}

    public PlayerModelRotator rotate(float angle, Vector3f axis) {
        stack.mulPose((new Quaternionf()).rotationAxis(angle, axis));
        return this;
    }

	public void end() {
		if (basedCenter) {
			stack.translate(0, -playerHeight / 2, 0);
		}
		if (basedTop) {
			stack.translate(0, -playerHeight, 0);
		}
	}

	public void endEnabledLegGrounding() {
		end();
	}
}
