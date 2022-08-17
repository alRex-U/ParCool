package com.alrex.parcool.client.input;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class KeyBindings {
	private static final GameSettings settings = Minecraft.getInstance().options;
	private static final KeyBinding keyBindCrawl = new KeyBinding("key.crawl.description", GLFW.GLFW_KEY_C, "key.categories.parcool");
	private static final KeyBinding keyBindGrabWall = new KeyBinding("key.grab.description", InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyBinding keyBindBreakfall = new KeyBinding("key.breakfall.description", GLFW.GLFW_KEY_C, "key.categories.parcool");
	private static final KeyBinding keyBindFastRunning = new KeyBinding("key.fastrunning.description", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.parcool");
	private static final KeyBinding keyBindVault = new KeyBinding("key.vaultdescription", InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyBinding keyBindDodge = new KeyBinding("key.dodge.description", GLFW.GLFW_KEY_R, "key.categories.parcool");
	private static final KeyBinding keyBindWallSlide = new KeyBinding("key.wallslide.description", InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyBinding keyBindHorizontalWallRun = new KeyBinding("key.horizontalwallrun.description", GLFW.GLFW_KEY_C, "key.categories.parcool");
	private static final KeyBinding keyBindOpenSettings = new KeyBinding("key.parcool.setting.open", GLFW.GLFW_KEY_P, "key.categories.parcool");

	public static KeyBinding getKeySprint() {
		return settings.keySprint;
	}

	public static KeyBinding getKeyJump() {
		return settings.keyJump;
	}

	public static KeyBinding getKeySneak() {
		return settings.keyShift;
	}

	public static KeyBinding getKeyLeft() {
		return settings.keyLeft;
	}

	public static KeyBinding getKeyRight() {
		return settings.keyRight;
	}

	public static KeyBinding getKeyForward() {
		return settings.keyUp;
	}

	public static KeyBinding getKeyBack() {
		return settings.keyDown;
	}

	public static KeyBinding getKeyCrawl() {
		return keyBindCrawl;
	}

	public static KeyBinding getKeyGrabWall() {
		return keyBindGrabWall;
	}

	public static KeyBinding getKeyVault() {
		return keyBindVault;
	}

	public static KeyBinding getKeyActivateParCool() {
		return keyBindOpenSettings;
	}

	public static KeyBinding getKeyBreakfall() {
		return keyBindBreakfall;
	}

	public static KeyBinding getKeyFastRunning() {
		return keyBindFastRunning;
	}

	public static KeyBinding getKeyDodge() {
		return keyBindDodge;
	}

	public static KeyBinding getKeyWallSlide() {
		return keyBindWallSlide;
	}

	public static KeyBinding getKeyHorizontalWallRun() {
		return keyBindHorizontalWallRun;
	}

	@SubscribeEvent
	public static void register(FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(keyBindCrawl);
		ClientRegistry.registerKeyBinding(keyBindGrabWall);
		ClientRegistry.registerKeyBinding(keyBindBreakfall);
		ClientRegistry.registerKeyBinding(keyBindFastRunning);
		ClientRegistry.registerKeyBinding(keyBindDodge);
		ClientRegistry.registerKeyBinding(keyBindWallSlide);
		ClientRegistry.registerKeyBinding(keyBindVault);
		ClientRegistry.registerKeyBinding(keyBindHorizontalWallRun);
		ClientRegistry.registerKeyBinding(keyBindOpenSettings);
	}
}
