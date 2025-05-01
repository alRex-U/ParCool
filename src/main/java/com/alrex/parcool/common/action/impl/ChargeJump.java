package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.api.compatibility.ClientPlayerWrapper;
import com.alrex.parcool.api.compatibility.EventBusWrapper;
import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.api.compatibility.Vec3Wrapper;
import com.alrex.parcool.client.animation.impl.ChargeJumpAnimator;
import com.alrex.parcool.client.animation.impl.JumpChargingAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import java.nio.ByteBuffer;

public class ChargeJump extends Action {
    public static final int JUMP_ANIMATION_TICK = 10;
    public static final int JUMP_MAX_CHARGE_TICK = 18;
    private int chargeTick = 0;
    private int lastChargeTick = 0;
    private int notChargeTick = 0;
    private int coolTimeTick = 0;
    private boolean started = false;

    @Override
    public boolean canStart(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
        boolean start = started;
        started = false;
        return start;
    }

    @Override
    public boolean canContinue(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
        return getDoingTick() < JUMP_ANIMATION_TICK;
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnStart;
    }

    @Override
    public void onStartInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
        coolTimeTick = 30;
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.CHARGE_JUMP.get(), 1, 1);
        Animation animation = Animation.get(player);
        if (animation != null) {
            animation.setAnimator(new ChargeJumpAnimator());
        }
    }

    @Override
    public void onStartInOtherClient(PlayerWrapper player, Parkourability parkourability, ByteBuffer startData) {
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.CHARGE_JUMP.get(), 1, 1);
        Animation animation = Animation.get(player);
        if (animation != null) {
            animation.setAnimator(new ChargeJumpAnimator());
        }
    }

    @Override
    public void onClientTick(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
        ClientPlayerWrapper cp = ClientPlayerWrapper.getOrDefault(player);
        if (cp != null) {
            if (cp.isOnGround()
                    && coolTimeTick <= 0
                    && !stamina.isExhausted()
                    && parkourability.getActionInfo().can(ChargeJump.class)
                    && !cp.isVisuallyCrawling()
                    && !cp.isSprinting()
                    && !cp.isInWaterOrBubble()
                    && !cp.isAnyMoveKeyDown()
                    && !parkourability.get(Crawl.class).isDoing()
                    && !EventBusWrapper.tryToStartEvent(player, this)
            ) {
                if (cp.isShiftKeyDown() && KeyRecorder.keySneak.getPreviousTickNotKeyDown() > 5) {
                    chargeTick++;
                    if (chargeTick > JUMP_MAX_CHARGE_TICK) chargeTick = JUMP_MAX_CHARGE_TICK;
                    lastChargeTick = chargeTick;
                    notChargeTick = 0;
                } else {
                    chargeTick--;
                    if (chargeTick < 0) chargeTick = 0;
                    notChargeTick++;
                }
                if (isCharging()) {
                    Vec3Wrapper targetAngle = VectorUtil.fromYawDegree(player.getYHeadRot());
                    Vec3Wrapper currentAngle = VectorUtil.fromYawDegree(player.getYBodyRot());
                    double differenceAngle = Math.atan(
                            (currentAngle.x() * targetAngle.z() - targetAngle.x() * currentAngle.z())
                                    / (targetAngle.x() * currentAngle.x() + targetAngle.z() * currentAngle.z())
                    );
                    player.setYBodyRot((float) VectorUtil.toYawDegree(currentAngle.yRot((float) (-differenceAngle / 2))));
                }
            } else {
                if (coolTimeTick > 0) coolTimeTick--;
                chargeTick = 0;
                notChargeTick++;
            }
        }
        if (isCharging()) {
            Animation animation = Animation.get(player);
            if (animation != null && !animation.hasAnimator()) {
                animation.setAnimator(new JumpChargingAnimator());
            }
        }
    }

    public void onJump(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
        double power = chargeTick / (double) JUMP_MAX_CHARGE_TICK;
        if (power >= 0.5) {
            started = true;
        } else {
            power = lastChargeTick / (double) JUMP_MAX_CHARGE_TICK;
            if (power > 0.5 && notChargeTick < 5) {
                started = true;
            }
        }
        if (started) {
            player.addToDeltaMovement(0, 0.160 * power, 0);
        }
    }

    public void onLand(PlayerWrapper player, Parkourability parkourability) {
        ClientPlayerWrapper cp = ClientPlayerWrapper.getOrDefault(player);
        if (player != null && player.isLocalPlayer()) {
            if (
                    parkourability.getActionInfo().can(ChargeJump.class)
                            && coolTimeTick <= 0
                            && !cp.isAnyMoveKeyDown()
                            && (parkourability.get(FastRun.class).getNotDashTick(parkourability.getAdditionalProperties()) < 15)
            ) {
                chargeTick = JUMP_MAX_CHARGE_TICK + 5;
                lastChargeTick = chargeTick;
                notChargeTick = 0;
            }
        }
    }

    @Override
    public boolean wantsToShowStatusBar(ClientPlayerWrapper player, Parkourability parkourability) {
        return isCharging();
    }

    @Override
    public float getStatusValue(ClientPlayerWrapper player, Parkourability parkourability) {
        return ((float) getChargingTick()) / JUMP_MAX_CHARGE_TICK;
    }

    @Override
    public void saveSynchronizedState(ByteBuffer buffer) {
        buffer.putInt(chargeTick);
    }

    @Override
    public void restoreSynchronizedState(ByteBuffer buffer) {
        chargeTick = buffer.getInt();
    }

    public boolean isCharging() {
        return chargeTick > 0 && !isDoing();
    }

    public int getChargingTick() {
        return chargeTick;
    }
}
