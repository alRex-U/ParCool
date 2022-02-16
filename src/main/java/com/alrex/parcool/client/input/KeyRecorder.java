package com.alrex.parcool.client.input;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


@OnlyIn(Dist.CLIENT)
public class KeyRecorder {
	public static final KeyState keyForward = new KeyState();
	public static final KeyState keyBack = new KeyState();
	public static final KeyState keyRight = new KeyState();
	public static final KeyState keyLeft = new KeyState();
	public static final KeyState keyJumpState = new KeyState();
	public static final KeyState keySprintState = new KeyState();
	public static final KeyState keyCrawlState = new KeyState();
	public static final KeyState keyOpenSettingsState = new KeyState();
	public static final KeyState keyFastRunning = new KeyState();
	public static final KeyState keyDodge = new KeyState();

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;

		record(KeyBindings.getKeyForward(), keyForward);
		record(KeyBindings.getKeyBack(), keyBack);
		record(KeyBindings.getKeyRight(), keyRight);
		record(KeyBindings.getKeyLeft(), keyLeft);
		record(KeyBindings.getKeyJump(), keyJumpState);
		record(KeyBindings.getKeySprint(), keySprintState);
		record(KeyBindings.getKeyCrawl(), keyCrawlState);
		record(KeyBindings.getKeyActivateParCool(), keyOpenSettingsState);
		record(KeyBindings.getKeyFastRunning(), keyFastRunning);
		record(KeyBindings.getKeyDodge(), keyDodge);
	}

	private static void record(KeyBinding keyBinding, KeyState state) {
		state.pressed = (keyBinding.isKeyDown() && state.tickKeyDown == 0);
		state.doubleTapped = (keyBinding.isKeyDown() && 0 < state.tickNotKeyDown && state.tickNotKeyDown <= 2);
		if (keyBinding.isKeyDown()) {
			state.tickKeyDown++;
			state.tickNotKeyDown = 0;
		} else {
			state.tickKeyDown = 0;
			state.tickNotKeyDown++;
		}
	}

	public static class KeyState {
		private boolean pressed = false;
		private boolean doubleTapped = false;
		private int tickKeyDown = 0;
		private int tickNotKeyDown = 0;

		public boolean isPressed() {
			return pressed;
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
	}
}
