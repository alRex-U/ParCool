package com.alrex.parcool.client.input;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class KeyBindings {
	private static final Minecraft mc = Minecraft.getInstance();
	private static final GameSettings settings = mc.options;
	private static final KeyBinding keyBindEnable = new KeyBinding("key.parcool.Enable", KeyConflictContext.UNIVERSAL, KeyModifier.CONTROL, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.categories.parcool");
	private static final KeyBinding keyBindCrawl = new KeyBinding("key.parcool.Crawl", GLFW.GLFW_KEY_C, "key.categories.parcool");
	private static final KeyBinding keyBindGrabWall = new KeyBinding("key.parcool.ClingToCliff", InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyBinding keyBindBreakfall = new KeyBinding("key.parcool.Breakfall", GLFW.GLFW_KEY_R, "key.categories.parcool");
	private static final KeyBinding keyBindFastRunning = new KeyBinding("key.parcool.FastRun", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.parcool");
	private static final KeyBinding keyBindFlipping = new KeyBinding("key.parcool.Flipping", GLFW.GLFW_KEY_UNKNOWN, "key.categories.parcool");
	private static final KeyBinding keyBindVault = new KeyBinding("key.parcool.Vault", InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyBinding keyBindDodge = new KeyBinding("key.parcool.Dodge", GLFW.GLFW_KEY_R, "key.categories.parcool");
	private static final KeyBinding keyBindRideZipline = new KeyBinding("key.parcool.RideZipline", InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyBinding keyBindWallJump = new KeyBinding("key.parcool.WallJump", GLFW.GLFW_KEY_SPACE, "key.categories.parcool");
	private static final KeyBinding keyBindHangDown = new KeyBinding("key.parcool.HangDown", InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyBinding keyBindWallSlide = new KeyBinding("key.parcool.WallSlide", InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyBinding keyBindHideInBlock = new KeyBinding("key.parcool.HideInBlock", GLFW.GLFW_KEY_H, "key.categories.parcool");
	private static final KeyBinding keyBindHorizontalWallRun = new KeyBinding("key.parcool.HorizontalWallRun", GLFW.GLFW_KEY_R, "key.categories.parcool");
	private static final KeyBinding keyBindQuickTurn = new KeyBinding("key.parcool.QuickTurn", GLFW.GLFW_KEY_UNKNOWN, "key.categories.parcool");
	private static final KeyBinding keyBindOpenSettings = new KeyBinding("key.parcool.openSetting", KeyConflictContext.UNIVERSAL, KeyModifier.ALT, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.categories.parcool");

	public static KeyBinding getKeySprint() {
		return settings.keySprint;
	}

	public static Boolean isKeyJumpDown() {
		return mc.player != null
			&& mc.player.input.jumping;
	}

	public static KeyBinding getKeySneak() {
		return settings.keyShift;
	}

	public static Boolean isAnyMovingKeyDown() {
		return mc.player != null
			&& (mc.player.input.left
			|| mc.player.input.right
			|| mc.player.input.forwardImpulse != 0
			|| mc.player.input.leftImpulse != 0);
	}

	public static Boolean isLeftAndRightDown() {
		return mc.player != null && mc.player.input.left && mc.player.input.right;
	}

	public static Boolean isKeyForwardDown() {
		return mc.player != null && mc.player.input.forwardImpulse > 0;
	}

	public static Boolean isKeyLeftDown() {
		return mc.player != null && mc.player.input.left;
	}

	public static Boolean isKeyRightDown() {
		return mc.player != null && mc.player.input.right;
	}

	public static Boolean isKeyBackDown() {
		return mc.player != null && mc.player.input.forwardImpulse < 0;
	}

	public static KeyBinding getKeyBindEnable() {
		return keyBindEnable;
	}

	public static KeyBinding getKeyCrawl() {
		return keyBindCrawl;
	}

	public static KeyBinding getKeyQuickTurn() {
		return keyBindQuickTurn;
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

	public static KeyBinding getKeyRideZipline() {
		return keyBindRideZipline;
	}

	public static KeyBinding getKeyWallSlide() {
		return keyBindWallSlide;
	}

	public static KeyBinding getKeyHangDown() {
		return keyBindHangDown;
	}

	public static KeyBinding getKeyHideInBlock() {
		return keyBindHideInBlock;
	}

	public static KeyBinding getKeyHorizontalWallRun() {
		return keyBindHorizontalWallRun;
	}

	public static KeyBinding getKeyWallJump() {
		return keyBindWallJump;
	}

	public static KeyBinding getKeyFlipping() {
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
		ClientRegistry.registerKeyBinding(keyBindRideZipline);
		ClientRegistry.registerKeyBinding(keyBindWallSlide);
		ClientRegistry.registerKeyBinding(keyBindWallJump);
		ClientRegistry.registerKeyBinding(keyBindVault);
		ClientRegistry.registerKeyBinding(keyBindHorizontalWallRun);
		ClientRegistry.registerKeyBinding(keyBindHideInBlock);
		ClientRegistry.registerKeyBinding(keyBindOpenSettings);
		ClientRegistry.registerKeyBinding(keyBindQuickTurn);
		ClientRegistry.registerKeyBinding(keyBindFlipping);
		ClientRegistry.registerKeyBinding(keyBindHangDown);
	}
}
