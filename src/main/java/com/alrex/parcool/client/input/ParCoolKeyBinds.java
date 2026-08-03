package com.alrex.parcool.client.input;

import com.alrex.parcool.extern.AdditionalMods;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;

@OnlyIn(Dist.CLIENT)
public class ParCoolKeyBinds {
    public interface IStateProvider {
        InputState state();
    }

    public record Input(KeyMapping key, InputState state) implements IStateProvider {
		private static Input from(KeyMapping key) {
			return new Input(key, new InputState());
		}

		private void update() {
			state.update(key);
		}
	}

    public record LogicalInput(BooleanSupplier keyDownSupplier, InputState state) implements IStateProvider {
        private static LogicalInput from(BooleanSupplier keyDownSupplier) {
            return new LogicalInput(keyDownSupplier, new InputState());
        }

        private void update() {
            state.update(keyDownSupplier.getAsBoolean());
        }
    }

	public static class InputState {
        private boolean down;
		private int pressedDurationTick = 0;
		private int notPressedDurationTick = 0;
		private int previousNotPressedDurationTick = 0;

		private void update(KeyMapping key) {
            update(key.isDown());
        }

        private void update(boolean keyDown) {
            down = keyDown;
            if (keyDown) {
				pressedDurationTick++;
				if (pressedDurationTick == 0) previousNotPressedDurationTick = notPressedDurationTick;
				notPressedDurationTick = -1;
			} else {
				pressedDurationTick = -1;
				notPressedDurationTick++;
			}
		}

        public boolean isDown() {
            return down;
        }

		public int getPressedDuration() {
			return pressedDurationTick;
		}

		public int getNotPressedDuration() {
			return notPressedDurationTick;
		}

		public int getPreviousNotPressedDurationTick() {
			return previousNotPressedDurationTick;
		}

		public boolean isJustPressed() {
			return pressedDurationTick == 0;
		}

		public boolean isJustReleased() {
			return notPressedDurationTick == 0;
		}
	}

	private static final ArrayList<Input> REGISTERED_KEYS = new ArrayList<>();
    private static final ArrayList<LogicalInput> LISTENING_KEYS = new ArrayList<>();

	private static Input register(KeyMapping key) {
		var input = Input.from(key);
		REGISTERED_KEYS.add(input);
		return input;
	}

    private static LogicalInput listen(BooleanSupplier keyDownSupplier) {
        var input = LogicalInput.from(keyDownSupplier);
		LISTENING_KEYS.add(input);
		return input;
	}

	public static final String KEY_CATEGORY = "key.category.parcool";

	public static final Input CRAWL = register(new KeyMapping("key.parcool.crawl", GLFW.GLFW_KEY_C, KEY_CATEGORY));
    public static final Input HANG = register(new KeyMapping("key.parcool.hang", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, KEY_CATEGORY));
	public static final Input SLIDE_DOWN = register(new KeyMapping("key.parcool.slide_down", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, KEY_CATEGORY));
    public static final Input DODGE = register(new KeyMapping("key.parcool.dodge", GLFW.GLFW_KEY_R, KEY_CATEGORY));
    public static final Input BREAKFALL = register(new KeyMapping("key.parcool.breakfall", GLFW.GLFW_KEY_R, KEY_CATEGORY));
	public static final Input HORIZONTAL_WALL_RUN = register(new KeyMapping("key.parcool.horizontal_wall_run", GLFW.GLFW_KEY_R, KEY_CATEGORY));
    public static final Input HIDE_IN_BLOCK = register(new KeyMapping("key.parcool.hide_in_block", GLFW.GLFW_KEY_C, KEY_CATEGORY));

    public static final LogicalInput JUMP = listen(Minecraft.getInstance().options.keyJump::isDown);
	public static final LogicalInput SHIFT = listen(Minecraft.getInstance().options.keyShift::isDown);
    public static final LogicalInput MOVEMENT_FORWARD = listen(Minecraft.getInstance().options.keyUp::isDown);
    public static final LogicalInput MOVEMENT_BACK = listen(Minecraft.getInstance().options.keyDown::isDown);
    public static final LogicalInput MOVEMENT_RIGHT = listen(Minecraft.getInstance().options.keyRight::isDown);
    public static final LogicalInput MOVEMENT_LEFT = listen(Minecraft.getInstance().options.keyLeft::isDown);

    public static InputState getMovementInput(LogicalMovement movement) {
		var input = AdditionalMods.getLogicalKey(movement);
		return input == null ? getStandardInput(movement).state : input.state;
    }

	public static LogicalInput getStandardInput(LogicalMovement movement) {
		return switch (movement) {
			case LEFT -> MOVEMENT_LEFT;
			case RIGHT -> MOVEMENT_RIGHT;
			case FORWARD -> MOVEMENT_FORWARD;
			case BACKWARD -> MOVEMENT_BACK;
		};
	}

	public static void tick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) return;
		for (var input : REGISTERED_KEYS) {
			input.update();
		}
		for (var input : LISTENING_KEYS) {
			input.update();
		}
	}

	public static void registerAll(RegisterKeyMappingsEvent event) {
		for (var input : REGISTERED_KEYS) {
			event.register(input.key);
		}
		REGISTERED_KEYS.trimToSize();
		LISTENING_KEYS.trimToSize();
	}
}
