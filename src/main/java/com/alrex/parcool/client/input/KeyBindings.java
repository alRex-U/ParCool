package com.alrex.parcool.client.input;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class KeyBindings {
	private static final GameSettings settings = Minecraft.getInstance().gameSettings;
	private static final KeyBinding keyBindCrawl = new KeyBinding("key.crawl.description", GLFW.GLFW_KEY_C, "key.categories.movement");
	private static final KeyBinding keyBindGrabWall = new KeyBinding("key.grab.description", GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.movement");
	private static final KeyBinding keyBindRoll = new KeyBinding("key.roll.description", GLFW.GLFW_KEY_C, "key.categories.movement");
	private static final KeyBinding keyBindFastRunning = new KeyBinding("key.fastrunning.description", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.movement");
	private static final KeyBinding keyBindFrontFlip = new KeyBinding("key.frontflip.description", GLFW.GLFW_KEY_W, "key.categories.movement");
	private static final KeyBinding keyBindActivateParCool = new KeyBinding("key.parcool.activate", GLFW.GLFW_KEY_P, "key.categories.parcool");

	public static KeyBinding getKeySprint() {
		return settings.keyBindSprint;
	}

	public static KeyBinding getKeyJump() {
		return settings.keyBindJump;
	}

	public static KeyBinding getKeySneak() {
		return settings.keyBindSneak;
	}

	public static KeyBinding getKeyLeft() {
		return settings.keyBindLeft;
	}

	public static KeyBinding getKeyRight() {
		return settings.keyBindRight;
	}

	public static KeyBinding getKeyForward() {
		return settings.keyBindForward;
	}

	public static KeyBinding getKeyBack() {
		return settings.keyBindBack;
	}

	public static KeyBinding getKeyCrawl() {
		return keyBindCrawl;
	}

	public static KeyBinding getKeyGrabWall() {
		return keyBindGrabWall;
	}

	public static KeyBinding getKeyActivateParCool() {
		return keyBindActivateParCool;
	}

	public static KeyBinding getKeyRoll() {
		return keyBindRoll;
	}

	public static KeyBinding getKeyFastRunning() {
		return keyBindFastRunning;
	}

	public static KeyBinding getKeyFrontFlip() {
		return keyBindFrontFlip;
	}

	@SubscribeEvent
	public static void register(FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(keyBindCrawl);
		ClientRegistry.registerKeyBinding(keyBindGrabWall);
		ClientRegistry.registerKeyBinding(keyBindRoll);
		ClientRegistry.registerKeyBinding(keyBindFastRunning);
		ClientRegistry.registerKeyBinding(keyBindFrontFlip);
		ClientRegistry.registerKeyBinding(keyBindActivateParCool);
	}
}
