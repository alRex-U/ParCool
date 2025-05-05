package com.alrex.parcool.client.animation;

import com.alrex.parcool.api.unstable.animation.AnimationOption;
import com.alrex.parcool.api.unstable.animation.AnimationPart;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

/**
 * Using Radians
 */
public class PlayerModelTransformer {
	private final Player player;
	private final PlayerModel model;
	private final boolean slim;
	private final PlayerRenderState state;
	private final HumanoidModel.ArmPose rightArmPose;
	private final HumanoidModel.ArmPose leftArmPose;

    private AnimationOption option = new AnimationOption();

	public float getPartialTick() {
		return state.partialTick;
	}

	public HumanoidModel.ArmPose getArmPose(HumanoidArm arm) {
		if (arm == HumanoidArm.RIGHT) {
			return rightArmPose;
		}
		return leftArmPose;
	}

	public float getLimbSwing() {
		return state.walkAnimationPos;
	}

	public float getLimbSwingAmount() {
		return state.walkAnimationSpeed;
	}

	public PlayerModel getRawModel() {
		return model;
	}

	public PlayerRenderState getState() {
		return state;
	}

	public PlayerModelTransformer(
			Player player,
			PlayerModel model,
			boolean slim,
			PlayerRenderState state,
			HumanoidModel.ArmPose rightArmPose,
			HumanoidModel.ArmPose leftArmPose
	) {
		this.player = player;
		this.model = model;
		this.slim = slim;
		this.state = state;
		this.rightArmPose = rightArmPose;
		this.leftArmPose = leftArmPose;
	}

    public void setOption(AnimationOption option) {
        this.option = option;
    }

	/**
	 * @param angleX swing arm frontward or backward
	 * @param angleY rotate arm around
	 * @param angleZ swing arm upward or downward
	 */
	public PlayerModelTransformer rotateRightArm(float angleX, float angleY, float angleZ) {
        if (option.isCanceled(AnimationPart.RIGHT_ARM)) return this;
		var rightArm = model.rightArm;
		if (rightArm.visible) {
			setRotations(rightArm, angleX, angleY, angleZ);
		}
		return this;
	}

	public PlayerModelTransformer rotateRightArm(float angleX, float angleY, float angleZ, float factor) {
        if (option.isCanceled(AnimationPart.RIGHT_ARM)) return this;
		var rightArm = model.rightArm;
		if (rightArm.visible) {
			setRotations(rightArm,
					MathUtil.lerp(rightArm.xRot, angleX, factor),
					MathUtil.lerp(rightArm.yRot, angleY, factor),
					MathUtil.lerp(rightArm.zRot, angleZ, factor)
			);
		}
		return this;
	}

	/**
	 * @param angleX swing arm frontward or backward
	 * @param angleY rotate arm around
	 * @param angleZ swing arm upward or downward
	 */
	public PlayerModelTransformer rotateLeftArm(float angleX, float angleY, float angleZ) {
        if (option.isCanceled(AnimationPart.LEFT_ARM)) return this;
		var leftArm = model.leftArm;
		if (leftArm.visible) {
			setRotations(leftArm, angleX, angleY, angleZ);
		}
		return this;
	}

	public PlayerModelTransformer rotateLeftArm(float angleX, float angleY, float angleZ, float factor) {
        if (option.isCanceled(AnimationPart.LEFT_ARM)) return this;
		var leftArm = model.leftArm;
		if (leftArm.visible) {
			setRotations(leftArm,
					MathUtil.lerp(leftArm.xRot, angleX, factor),
					MathUtil.lerp(leftArm.yRot, angleY, factor),
					MathUtil.lerp(leftArm.zRot, angleZ, factor)
			);
		}
		return this;
	}

	/**
	 * @param angleX swing leg frontward or backward
	 * @param angleY rotate leg around
	 * @param angleZ swing leg upward or downward
	 */
	public PlayerModelTransformer rotateRightLeg(float angleX, float angleY, float angleZ) {
        if (option.isCanceled(AnimationPart.RIGHT_LEG)) return this;
		var rightLeg = model.rightLeg;
		if (rightLeg.visible) {
			setRotations(rightLeg, angleX, angleY, angleZ);
		}
		return this;
	}

	public PlayerModelTransformer rotateRightLeg(float angleX, float angleY, float angleZ, float factor) {
        if (option.isCanceled(AnimationPart.RIGHT_LEG)) return this;
		var rightLeg = model.rightLeg;
		if (rightLeg.visible) {
			setRotations(rightLeg,
					MathUtil.lerp(rightLeg.xRot, angleX, factor),
					MathUtil.lerp(rightLeg.yRot, angleY, factor),
					MathUtil.lerp(rightLeg.zRot, angleZ, factor)
			);
		}
		return this;
	}

	/**
	 * @param angleX swing leg frontward or backward
	 * @param angleY rotate leg around
	 * @param angleZ swing leg upward or downward
	 */
	public PlayerModelTransformer rotateLeftLeg(float angleX, float angleY, float angleZ) {
        if (option.isCanceled(AnimationPart.LEFT_LEG)) return this;
		var leftLeg = model.leftLeg;
		if (leftLeg.visible) {
			setRotations(leftLeg, angleX, angleY, angleZ);
		}
		return this;
	}

	public PlayerModelTransformer rotateLeftLeg(float angleX, float angleY, float angleZ, float factor) {
        if (option.isCanceled(AnimationPart.LEFT_LEG)) return this;
		var leftLeg = model.leftLeg;
		if (leftLeg.visible) {
			setRotations(leftLeg,
					MathUtil.lerp(leftLeg.xRot, angleX, factor),
					MathUtil.lerp(leftLeg.yRot, angleY, factor),
					MathUtil.lerp(leftLeg.zRot, angleZ, factor)
			);
		}
		return this;
	}

	public PlayerModelTransformer addRotateRightArm(float angleX, float angleY, float angleZ) {
        if (option.isCanceled(AnimationPart.RIGHT_ARM)) return this;
		var arm = model.rightArm;
		if (arm.visible) {
			setRotations(arm, arm.xRot + angleX, arm.yRot + angleY, arm.zRot + angleZ);
		}
		return this;
	}

	public PlayerModelTransformer addRotateLeftArm(float angleX, float angleY, float angleZ) {
        if (option.isCanceled(AnimationPart.LEFT_ARM)) return this;
		var arm = model.leftArm;
		if (arm.visible) {
			setRotations(arm, arm.xRot + angleX, arm.yRot + angleY, arm.zRot + angleZ);
		}
		return this;
	}

	public PlayerModelTransformer addRotateRightLeg(float angleX, float angleY, float angleZ) {
        if (option.isCanceled(AnimationPart.RIGHT_LEG)) return this;
		var leg = model.rightLeg;
		if (leg.visible) {
			setRotations(leg, leg.xRot + angleX, leg.yRot + angleY, leg.zRot + angleZ);
		}
		return this;
	}

	public PlayerModelTransformer addRotateLeftLeg(float angleX, float angleY, float angleZ) {
        if (option.isCanceled(AnimationPart.LEFT_LEG)) return this;
		var leg = model.leftLeg;
		if (leg.visible) {
			setRotations(leg, leg.xRot + angleX, leg.yRot + angleY, leg.zRot + angleZ);
		}
		return this;
	}

    public PlayerModelTransformer addRotateRightArm(float angleX, float angleY, float angleZ, float factor) {
        if (option.isCanceled(AnimationPart.RIGHT_ARM)) return this;
        return addRotateRightArm(angleX * factor, angleY * factor, angleZ * factor);
    }

    public PlayerModelTransformer addRotateLeftArm(float angleX, float angleY, float angleZ, float factor) {
        if (option.isCanceled(AnimationPart.LEFT_ARM)) return this;
        return addRotateLeftArm(angleX * factor, angleY * factor, angleZ * factor);
    }

    public PlayerModelTransformer addRotateRightLeg(float angleX, float angleY, float angleZ, float factor) {
        if (option.isCanceled(AnimationPart.RIGHT_LEG)) return this;
        return addRotateRightLeg(angleX * factor, angleY * factor, angleZ * factor);
    }

    public PlayerModelTransformer addRotateLeftLeg(float angleX, float angleY, float angleZ, float factor) {
        if (option.isCanceled(AnimationPart.LEFT_LEG)) return this;
        return addRotateLeftLeg(angleX * factor, angleY * factor, angleZ * factor);
    }

	public PlayerModelTransformer makeArmsNatural() {
        if (option.isCanceled(AnimationPart.RIGHT_ARM)) return this;
        if (option.isCanceled(AnimationPart.LEFT_ARM)) return this;
		AnimationUtils.bobArms(model.rightArm, model.leftArm, state.ageInTicks);
		return this;
	}

	public PlayerModelTransformer makeLegsMoveDynamically(float factor) {
        if (option.isCanceled(AnimationPart.RIGHT_LEG)) return this;
        if (option.isCanceled(AnimationPart.LEFT_LEG)) return this;
		model.rightLeg.zRot += Mth.cos(state.ageInTicks * 0.56F) * 0.8F * factor + 0.05F;
		model.leftLeg.zRot -= Mth.cos(state.ageInTicks * 0.56F) * 0.8F * factor + 0.05F;
		model.rightLeg.xRot += Mth.sin(state.ageInTicks * 0.56F) * 0.8F * factor;
		model.leftLeg.xRot -= Mth.sin(state.ageInTicks * 0.56F) * 0.8F * factor;
		return this;
	}

	public PlayerModelTransformer makeArmsMoveDynamically(float factor) {
        if (option.isCanceled(AnimationPart.RIGHT_ARM)) return this;
        if (option.isCanceled(AnimationPart.LEFT_ARM)) return this;
		model.rightArm.zRot += Mth.cos(state.ageInTicks * 0.56F) * 0.8F * factor + 0.05F;
		model.leftArm.zRot -= Mth.cos(state.ageInTicks * 0.56F) * 0.8F * factor + 0.05F;
		model.rightArm.xRot += Mth.sin(state.ageInTicks * 0.56F) * 0.8F * factor;
		model.leftArm.xRot -= Mth.sin(state.ageInTicks * 0.56F) * 0.8F * factor;
		return this;
	}

	public PlayerModelTransformer makeLegsLittleMoving() {
        if (option.isCanceled(AnimationPart.RIGHT_LEG)) return this;
        if (option.isCanceled(AnimationPart.LEFT_LEG)) return this;
		AnimationUtils.bobArms(model.rightLeg, model.leftLeg, state.ageInTicks);
		return this;
	}

	public PlayerModelTransformer makeLegsShakingDynamically(float factor) {
        if (option.isCanceled(AnimationPart.RIGHT_LEG)) return this;
        if (option.isCanceled(AnimationPart.LEFT_LEG)) return this;
		model.rightLeg.zRot += Mth.cos(state.ageInTicks * 0.56F) * 0.8F * factor + 0.05F;
		model.leftLeg.zRot += Mth.cos(state.ageInTicks * 0.56F) * 0.8F * factor + 0.05F;
		model.rightLeg.xRot += Mth.sin(state.ageInTicks * 0.56F) * 0.2F * factor;
		model.leftLeg.xRot -= Mth.sin(state.ageInTicks * 0.56F) * 0.2F * factor;
		return this;
	}

	public PlayerModelTransformer rotateAdditionallyHeadPitch(float pitchDegree) {
        if (option.isCanceled(AnimationPart.HEAD)) return this;
		model.head.xRot = (float) Math.toRadians(pitchDegree + state.xRot);
		return this;
	}

	public PlayerModelTransformer rotateHeadPitch(float pitchDegree) {
        if (option.isCanceled(AnimationPart.HEAD)) return this;
		model.head.xRot = (float) Math.toRadians(pitchDegree);
		return this;
	}

    public PlayerModelTransformer rotateHeadYaw(float yawDegree) {
        if (option.isCanceled(AnimationPart.HEAD)) return this;
        model.head.yRot = (float) Math.toRadians(yawDegree);
        return this;
    }

    public PlayerModelTransformer rotateHeadYawRadian(float yawRadian) {
        if (option.isCanceled(AnimationPart.HEAD)) return this;
        model.head.yRot = yawRadian;
        return this;
    }

	public PlayerModelTransformer rotateAdditionallyHeadYaw(float yawDegree) {
        if (option.isCanceled(AnimationPart.HEAD)) return this;
		model.head.yRot = (float) Math.toRadians(yawDegree + state.yRot);
		return this;
	}

    public PlayerModelTransformer rotateAdditionallyHeadRoll(float rollDegree) {
        if (option.isCanceled(AnimationPart.HEAD)) return this;
		model.head.zRot = (float) Math.toRadians(rollDegree + model.head.zRot);
        return this;
    }

    public PlayerModelTransformer translateRightArm(float xOffset, float yOffset, float zOffset) {
        if (option.isCanceled(AnimationPart.RIGHT_ARM)) return this;
        getRawModel().rightArm.x += xOffset;
        getRawModel().rightArm.y += yOffset;
        getRawModel().rightArm.z += zOffset;
        return this;
    }

    public PlayerModelTransformer translateLeftArm(float xOffset, float yOffset, float zOffset) {
        if (option.isCanceled(AnimationPart.LEFT_ARM)) return this;
        getRawModel().leftArm.x += xOffset;
        getRawModel().leftArm.y += yOffset;
        getRawModel().leftArm.z += zOffset;
        return this;
    }

    public PlayerModelTransformer translateRightLeg(float xOffset, float yOffset, float zOffset) {
        if (option.isCanceled(AnimationPart.RIGHT_LEG)) return this;
        getRawModel().rightLeg.x += xOffset;
        getRawModel().rightLeg.y += yOffset;
        getRawModel().rightLeg.z += zOffset;
        return this;
    }

    public PlayerModelTransformer translateLeftLeg(float xOffset, float yOffset, float zOffset) {
        if (option.isCanceled(AnimationPart.LEFT_LEG)) return this;
        getRawModel().leftLeg.x += xOffset;
        getRawModel().leftLeg.y += yOffset;
        getRawModel().leftLeg.z += zOffset;
        return this;
    }

    public PlayerModelTransformer translateHead(float xOffset, float yOffset, float zOffset) {
        if (option.isCanceled(AnimationPart.HEAD)) return this;
        getRawModel().head.x += xOffset;
        getRawModel().head.y += yOffset;
        getRawModel().head.z += zOffset;
        return this;
    }

    public void end() {
    }

	private void setRotations(ModelPart renderer, float angleX, float angleY, float angleZ) {
		renderer.xRot = angleX;
		renderer.yRot = angleY;
		renderer.zRot = angleZ;
	}

	public void reset() {
		resetModel(model.head);
		resetModel(model.body);
		{
			resetModel(model.rightArm);
            model.rightArm.x = -5.0F;
            model.rightArm.y = 2.0F;
			model.rightArm.z = 0.0F;
		}
		{
			resetModel(model.leftArm);
            model.leftArm.x = 5.0F;
            model.leftArm.y = 2.0F;
			model.leftArm.z = 0.0F;
		}
		{
			resetModel(model.leftLeg);
			model.leftLeg.x = 1.9F;
			model.leftLeg.y = 12.0F;
			model.leftLeg.z = 0.0F;
		}
		{
			resetModel(model.rightLeg);
			model.rightLeg.x = -1.9F;
			model.rightLeg.y = 12.0F;
			model.rightLeg.z = 0.0F;
		}
	}

	public void resetModel(ModelPart model) {
		model.xRot = 0;
		model.yRot = 0;
		model.zRot = 0;
		model.x = 0;
		model.y = 0;
		model.z = 0;
	}
}
