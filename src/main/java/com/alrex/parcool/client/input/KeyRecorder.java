package com.alrex.parcool.client.input;

import com.github.exopandora.shouldersurfing.math.Vec2f;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.Minecraft;

@OnlyIn(Dist.CLIENT)
public class KeyRecorder {
	public static final KeyState keyForward = new KeyState();
	public static final KeyState keyBack = new KeyState();
	public static final KeyState keyRight = new KeyState();
	public static final KeyState keyLeft = new KeyState();
	public static final KeyState keySneak = new KeyState();
	public static final KeyState keyJumpState = new KeyState();
	public static final KeyState keySprintState = new KeyState();
	public static final KeyState keyCrawlState = new KeyState();
	public static final KeyState keyOpenSettingsState = new KeyState();
	public static final KeyState keyFastRunning = new KeyState();
	public static final KeyState keyDodge = new KeyState();
    public static final KeyState keyRideZipline = new KeyState();
	public static final KeyState keyBreakfall = new KeyState();
	public static final KeyState keyWallJump = new KeyState();
	public static final KeyState keyQuickTurn = new KeyState();
	public static final KeyState keyFlipping = new KeyState();
    public static final KeyState keyBindGrabWall = new KeyState();
	public static Vec2f lastDirection = null;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;

        record(KeyBindings.isKeyForwardDown(), keyForward);
        record(KeyBindings.isKeyBackDown(), keyBack);
        record(KeyBindings.isKeyRightDown(), keyRight);
        record(KeyBindings.isKeyLeftDown(), keyLeft);
		record(KeyBindings.getKeySneak(), keySneak);
        record(KeyBindings.isKeyJumpDown(), keyJumpState);
		record(KeyBindings.getKeySprint(), keySprintState);
		record(KeyBindings.getKeyCrawl(), keyCrawlState);
		record(KeyBindings.getKeyActivateParCool(), keyOpenSettingsState);
		record(KeyBindings.getKeyFastRunning(), keyFastRunning);
		record(KeyBindings.getKeyDodge(), keyDodge);
        record(KeyBindings.getKeyRideZipline(), keyRideZipline);
		record(KeyBindings.getKeyBreakfall(), keyBreakfall);
		record(KeyBindings.getKeyWallJump(), keyWallJump);
		record(KeyBindings.getKeyQuickTurn(), keyQuickTurn);
		record(KeyBindings.getKeyFlipping(), keyFlipping);
		record(KeyBindings.getKeyGrabWall(), keyBindGrabWall);
		recordMovingVector(KeyBindings.isAnyMovingKeyDown());
	}

	public static Vec2f getLastDirection() {
		return lastDirection;
	}

    private static void record(Boolean isDown, KeyState state) {
        state.pressed = (isDown && state.tickKeyDown == 0);
        state.released = (!isDown && state.tickNotKeyDown == 0);
        state.doubleTapped = (isDown && 0 < state.tickNotKeyDown && state.tickNotKeyDown <= 2);
        if (state.pressed && state.tickNotKeyDown > 0) {
            state.previousTickNotKeyDown = state.tickNotKeyDown;
        }
        if (isDown) {
			state.tickKeyDown++;
			state.tickNotKeyDown = 0;
		} else {
			state.tickKeyDown = 0;
			state.tickNotKeyDown++;
		}
	}

    private static void record(KeyMapping keyBinding, KeyState state) {
        record(keyBinding.isDown(), state);
    }

	private static void recordMovingVector(boolean isDown) {
		if (KeyBindings.isAnyMovingKeyDown()) {
			var vector = Minecraft.getInstance().player.input.getMoveVector();
			lastDirection = new Vec2f(vector.x, vector.y);
		}
	}

	public static class KeyState {
		private boolean pressed = false;
		private boolean released = false;
		private boolean doubleTapped = false;
		private int tickKeyDown = 0;
		private int tickNotKeyDown = 0;
        private int previousTickNotKeyDown = Integer.MAX_VALUE;

		public boolean isPressed() {
			return pressed;
		}

		public boolean isReleased() {
			return released;
		}

		public boolean isDoubleTapped() {
			return doubleTapped;
		}

		public int getTickKeyDown() {
			return tickKeyDown;
		}

		public int getTickNotKeyDown() {
			return tickNotKeyDown;
		}

        public int getPreviousTickNotKeyDown() {
            return previousTickNotKeyDown;
        }
	}
}
