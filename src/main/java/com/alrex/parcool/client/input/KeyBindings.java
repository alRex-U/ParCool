package com.alrex.parcool.client.input;

import com.alrex.parcool.utilities.VectorUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class KeyBindings {
	private static final Minecraft mc = Minecraft.getInstance();
	private static final Options settings = mc.options;
	private static final KeyMapping keyBindEnable = new KeyMapping("key.parcool.Enable", KeyConflictContext.UNIVERSAL, KeyModifier.CONTROL, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.categories.parcool");
	private static final KeyMapping keyBindCrawl = new KeyMapping("key.parcool.Crawl", GLFW.GLFW_KEY_C, "key.categories.parcool");
	private static final KeyMapping keyBindGrabWall = new KeyMapping("key.parcool.ClingToCliff", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyMapping keyBindBreakfall = new KeyMapping("key.parcool.Breakfall", GLFW.GLFW_KEY_R, "key.categories.parcool");
	private static final KeyMapping keyBindFastRunning = new KeyMapping("key.parcool.FastRun", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.parcool");
	private static final KeyMapping keyBindFlipping = new KeyMapping("key.parcool.Flipping", GLFW.GLFW_KEY_UNKNOWN, "key.categories.parcool");
	private static final KeyMapping keyBindVault = new KeyMapping("key.parcool.Vault", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyMapping keyBindDodge = new KeyMapping("key.parcool.Dodge", GLFW.GLFW_KEY_R, "key.categories.parcool");
	private static final KeyMapping keyBindRideZipline = new KeyMapping("key.parcool.RideZipline", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyMapping keyBindWallJump = new KeyMapping("key.parcool.WallJump", GLFW.GLFW_KEY_SPACE, "key.categories.parcool");
	private static final KeyMapping keyBindHangDown = new KeyMapping("key.parcool.HangDown", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyMapping keyBindWallSlide = new KeyMapping("key.parcool.WallSlide", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.parcool");
	private static final KeyMapping keyBindHideInBlock = new KeyMapping("key.parcool.HideInBlock", GLFW.GLFW_KEY_C, "key.categories.parcool");
	private static final KeyMapping keyBindHorizontalWallRun = new KeyMapping("key.parcool.HorizontalWallRun", GLFW.GLFW_KEY_R, "key.categories.parcool");
	private static final KeyMapping keyBindQuickTurn = new KeyMapping("key.parcool.QuickTurn", GLFW.GLFW_KEY_UNKNOWN, "key.categories.parcool");
	private static final KeyMapping keyBindOpenSettings = new KeyMapping("key.parcool.openSetting", KeyConflictContext.UNIVERSAL, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.categories.parcool");
	private static final Vec3 forwardVector = new Vec3(0, 0, 1);

	public static KeyMapping getKeySprint() {
		return settings.keySprint;
	}

	public static Boolean isKeyJumpDown() {
		return mc.player != null
				&& mc.player.input != null
				&& mc.player.input.jumping;
	}

	public static KeyMapping getKeySneak() {
		return settings.keyShift;
	}

	public static Vec3 getCurrentMoveVector() {
		var player = Minecraft.getInstance().player;
		if (player == null) return Vec3.ZERO;
		var vector = player.input.getMoveVector();
		if (VectorUtil.isZero(vector)) return Vec3.ZERO;
		double length = vector.length();
		return new Vec3(vector.x / length, 0, vector.y / length);
	}

	public static Vec3 getForwardVector() {
		return forwardVector;
	}

	public static Boolean isAnyMovingKeyDown() {
		return mc.player != null
				&& mc.player.input != null
				&& (mc.player.input.left
				|| mc.player.input.right
				|| mc.player.input.forwardImpulse != 0
				|| mc.player.input.leftImpulse != 0);
	}

	public static Boolean isLeftAndRightDown() {
		return mc.player != null && mc.player.input != null && mc.player.input.left && mc.player.input.right;
	}

	public static Boolean isKeyForwardDown() {
		return mc.player != null && mc.player.input != null && mc.player.input.forwardImpulse > 0;
	}

	public static Boolean isKeyLeftDown() {
		return mc.player != null && mc.player.input != null && mc.player.input.left;
	}

	public static Boolean isKeyRightDown() {
		return mc.player != null && mc.player.input != null && mc.player.input.right;
	}

	public static Boolean isKeyBackDown() {
		return mc.player != null && mc.player.input != null && mc.player.input.forwardImpulse < 0;
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

	public static KeyMapping getKeyRideZipline() {
		return keyBindRideZipline;
	}

	public static KeyMapping getKeyWallSlide() {
		return keyBindWallSlide;
	}

	public static KeyMapping getKeyHangDown() {
		return keyBindHangDown;
	}

	public static KeyMapping getKeyHideInBlock() {
		return keyBindHideInBlock;
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
	public static void register(RegisterKeyMappingsEvent event) {
		event.register(keyBindEnable);
		event.register(keyBindCrawl);
		event.register(keyBindGrabWall);
		event.register(keyBindBreakfall);
		event.register(keyBindFastRunning);
		event.register(keyBindDodge);
		event.register(keyBindRideZipline);
		event.register(keyBindWallSlide);
		event.register(keyBindWallJump);
		event.register(keyBindVault);
		event.register(keyBindHorizontalWallRun);
		event.register(keyBindHideInBlock);
		event.register(keyBindOpenSettings);
		event.register(keyBindQuickTurn);
		event.register(keyBindFlipping);
		event.register(keyBindHangDown);
	}
}
