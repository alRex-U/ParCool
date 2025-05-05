package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.FlippingAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class Flipping extends Action {
    public enum ControlType {
        PressRightAndLeft, TapMovementAndJump, PressFlippingKey;

        @OnlyIn(Dist.CLIENT)
        public boolean isInputDone(boolean justJumped) {
			switch (this) {
                case PressRightAndLeft:
					return KeyBindings.isKeyRightDown()
                            && KeyRecorder.keyRight.getTickKeyDown() < 3
							&& KeyBindings.isKeyLeftDown()
                            && KeyRecorder.keyLeft.getTickKeyDown() < 3;
                case PressFlippingKey:
                    return KeyRecorder.keyFlipping.isPressed();
                case TapMovementAndJump:
                    return justJumped && (
							(KeyBindings.isKeyForwardDown() && KeyRecorder.keyForward.getTickKeyDown() < 4)
									|| (KeyBindings.isKeyBackDown() && KeyRecorder.keyBack.getTickKeyDown() < 4)
                    );
            }
            return false;
        }
    }

    public enum Direction {
        Front, Back
    }

    private boolean justJumped = false;

    public void onJump(Player player, Parkourability parkourability) {
        justJumped = true;
    }
	@Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        Direction fDirection;
		if (KeyBindings.isKeyBackDown()) {
            fDirection = Direction.Back;
		} else {
            fDirection = Direction.Front;
		}
		ControlType control = ParCoolConfig.Client.FlipControl.get();
		startInfo
				.putInt(control.ordinal())
				.putInt(fDirection.ordinal());
		boolean input = control.isInputDone(justJumped);
		justJumped = false;
		return (input
				&& !player.isInWater()
                && !player.isShiftKeyDown()
				&& !parkourability.get(Crawl.class).isDoing()
				&& !parkourability.get(Dive.class).isDoing()
				&& !parkourability.get(ChargeJump.class).isDoing()
				&& !parkourability.get(HideInBlock.class).isDoing()
				&& !parkourability.getBehaviorEnforcer().cancelJump()
				&& parkourability.getAdditionalProperties().getNotLandingTick() <= 1
		);
	}

	@Override
    public boolean canContinue(Player player, Parkourability parkourability) {
		return !player.onGround() || getDoingTick() <= 10;
	}

	@Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        ControlType control = ControlType.values()[startData.getInt()];
        if (control != ControlType.TapMovementAndJump) player.jumpFromGround();
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new FlippingAnimator(
                    Direction.values()[startData.getInt()]
			));
		}
	}

	@Override
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        startData.position(4); // skip (int * 1)
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new FlippingAnimator(
                    Direction.values()[startData.getInt()]
			));
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}
}
