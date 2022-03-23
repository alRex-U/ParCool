package com.alrex.parcool.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class KeyBindings {
	private static final Options settings = Minecraft.getInstance().options;
	private static final KeyMapping keyBindCrawl = new KeyMapping("key.crawl.description", GLFW.GLFW_KEY_C, "key.categories.movement");
	private static final KeyMapping keyBindGrabWall = new KeyMapping("key.grab.description", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.movement");
	private static final KeyMapping keyBindRoll = new KeyMapping("key.roll.description", GLFW.GLFW_KEY_C, "key.categories.movement");
	private static final KeyMapping keyBindFastRunning = new KeyMapping("key.fastrunning.description", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.movement");
	private static final KeyMapping keyBindDodge = new KeyMapping("key.dodge.description", GLFW.GLFW_KEY_R, "key.categories.movement");
	private static final KeyMapping keyBindOpenSettings = new KeyMapping("key.parcool.setting.open", GLFW.GLFW_KEY_P, "key.categories.parcool");
	private static final KeyMapping keyBindWallSlide = new KeyMapping("key.wallslide.description", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.movement");

	public static KeyMapping getKeySprint() {
		return settings.keySprint;
	}

	public static KeyMapping getKeyJump() {
		return settings.keyJump;
	}

	public static KeyMapping getKeySneak() {
		return settings.keyShift;
	}

	public static KeyMapping getKeyLeft() {
		return settings.keyLeft;
	}

	public static KeyMapping getKeyRight() {
		return settings.keyRight;
	}

	public static KeyMapping getKeyForward() {
		return settings.keyUp;
	}

	public static KeyMapping getKeyBack() {
		return settings.keyDown;
	}

	public static KeyMapping getKeyCrawl() {
		return keyBindCrawl;
	}

	public static KeyMapping getKeyGrabWall() {
		return keyBindGrabWall;
	}

	public static KeyMapping getKeyActivateParCool() {
		return keyBindOpenSettings;
	}

	public static KeyMapping getKeyRoll() {
		return keyBindRoll;
	}

	public static KeyMapping getKeyFastRunning() {
		return keyBindFastRunning;
	}

	public static KeyMapping getKeyDodge() {
		return keyBindDodge;
	}

	public static KeyMapping getKeyBindWallSlide() {
		return keyBindWallSlide;
	}

	@SubscribeEvent
	public static void register(FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(keyBindCrawl);
		ClientRegistry.registerKeyBinding(keyBindGrabWall);
		ClientRegistry.registerKeyBinding(keyBindRoll);
		ClientRegistry.registerKeyBinding(keyBindFastRunning);
		ClientRegistry.registerKeyBinding(keyBindDodge);
		ClientRegistry.registerKeyBinding(keyBindOpenSettings);
		ClientRegistry.registerKeyBinding(keyBindWallSlide);
	}
}
