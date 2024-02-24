package com.alrex.parcool.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class KeyBindings {
	private static final Options settings = Minecraft.getInstance().options;
	private static final KeyMapping keyBindEnable = new KeyMapping("key.parcool.Enable", KeyConflictContext.UNIVERSAL, KeyModifier.CONTROL, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.categories.parcool");
	private static final KeyMapping keyBindCrawl = new KeyMapping("key.parcool.Crawl", GLFW.GLFW_KEY_C, "key.categories.parcool");
	private static final KeyMapping keyBindGrabWall = new KeyMapping("key.parcool.ClingToCliff", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyMapping keyBindBreakfall = new KeyMapping("key.parcool.Breakfall", GLFW.GLFW_KEY_R, "key.categories.parcool");
	private static final KeyMapping keyBindFastRunning = new KeyMapping("key.parcool.FastRun", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.parcool");
	private static final KeyMapping keyBindFlipping = new KeyMapping("key.parcool.Flipping", GLFW.GLFW_KEY_UNKNOWN, "key.categories.parcool");
	private static final KeyMapping keyBindVault = new KeyMapping("key.parcool.Vault", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyMapping keyBindDodge = new KeyMapping("key.parcool.Dodge", GLFW.GLFW_KEY_R, "key.categories.parcool");
	private static final KeyMapping keyBindWallJump = new KeyMapping("key.parcool.WallJump", GLFW.GLFW_KEY_SPACE, "key.categories.parcool");
	private static final KeyMapping keyBindHangDown = new KeyMapping("key.parcool.HangDown", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyMapping keyBindWallSlide = new KeyMapping("key.parcool.WallSlide", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyMapping keyBindHorizontalWallRun = new KeyMapping("key.parcool.HorizontalWallRun", GLFW.GLFW_KEY_R, "key.categories.parcool");
	private static final KeyMapping keyBindQuickTurn = new KeyMapping("key.parcool.QuickTurn", GLFW.GLFW_KEY_UNKNOWN, "key.categories.parcool");
	private static final KeyMapping keyBindOpenSettings = new KeyMapping("key.parcool.openSetting", KeyConflictContext.UNIVERSAL, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.categories.parcool");

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

	public static KeyMapping getKeyBindEnable() {
		return keyBindEnable;
	}

	public static KeyMapping getKeyCrawl() {
		return keyBindCrawl;
	}

	public static KeyMapping getKeyQuickTurn() {
		return keyBindQuickTurn;
	}

	public static KeyMapping getKeyGrabWall() {
		return keyBindGrabWall;
	}

	public static KeyMapping getKeyVault() {
		return keyBindVault;
	}

	public static KeyMapping getKeyActivateParCool() {
		return keyBindOpenSettings;
	}

	public static KeyMapping getKeyBreakfall() {
		return keyBindBreakfall;
	}

	public static KeyMapping getKeyFastRunning() {
		return keyBindFastRunning;
	}

	public static KeyMapping getKeyDodge() {
		return keyBindDodge;
	}

	public static KeyMapping getKeyWallSlide() {
		return keyBindWallSlide;
	}

	public static KeyMapping getKeyHangDown() {
		return keyBindHangDown;
	}

	public static KeyMapping getKeyHorizontalWallRun() {
		return keyBindHorizontalWallRun;
	}

	public static KeyMapping getKeyWallJump() {
		return keyBindWallJump;
	}

	public static KeyMapping getKeyFlipping() {
		return keyBindFlipping;
	}

	@SubscribeEvent
	public static void register(FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(keyBindEnable);
		ClientRegistry.registerKeyBinding(keyBindCrawl);
		ClientRegistry.registerKeyBinding(keyBindGrabWall);
		ClientRegistry.registerKeyBinding(keyBindBreakfall);
		ClientRegistry.registerKeyBinding(keyBindFastRunning);
		ClientRegistry.registerKeyBinding(keyBindDodge);
		ClientRegistry.registerKeyBinding(keyBindWallSlide);
		ClientRegistry.registerKeyBinding(keyBindWallJump);
		ClientRegistry.registerKeyBinding(keyBindVault);
		ClientRegistry.registerKeyBinding(keyBindHorizontalWallRun);
		ClientRegistry.registerKeyBinding(keyBindOpenSettings);
		ClientRegistry.registerKeyBinding(keyBindQuickTurn);
		ClientRegistry.registerKeyBinding(keyBindFlipping);
		ClientRegistry.registerKeyBinding(keyBindHangDown);
	}
}
