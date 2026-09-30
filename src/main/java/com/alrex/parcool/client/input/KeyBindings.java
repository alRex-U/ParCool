package com.alrex.parcool.client.input;

import com.alrex.parcool.utilities.VectorUtil;
import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class KeyBindings {
    private static final KeyMapping keyBindEnable =
            new KeyMapping("key.parcool.Enable", GLFW.GLFW_KEY_UNKNOWN, "key.categories.parcool");
    private static final KeyMapping keyBindCrawl =
            new KeyMapping("key.parcool.Crawl", GLFW.GLFW_KEY_C, "key.categories.parcool");
    private static final KeyMapping keyBindGrabWall =
            new KeyMapping(
                    "key.parcool.ClingToCliff",
                    InputConstants.Type.MOUSE,
                    GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                    "key.categories.parcool");
    private static final KeyMapping keyBindBreakfall =
            new KeyMapping("key.parcool.Breakfall", GLFW.GLFW_KEY_R, "key.categories.parcool");
    private static final KeyMapping keyBindFastRunning =
            new KeyMapping(
                    "key.parcool.FastRun", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.parcool");
    private static final KeyMapping keyBindFlipping =
            new KeyMapping("key.parcool.Flipping", GLFW.GLFW_KEY_UNKNOWN, "key.categories.parcool");
    private static final KeyMapping keyBindVault =
            new KeyMapping(
                    "key.parcool.Vault",
                    InputConstants.Type.MOUSE,
                    GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                    "key.categories.parcool");
    private static final KeyMapping keyBindDodge =
            new KeyMapping("key.parcool.Dodge", GLFW.GLFW_KEY_R, "key.categories.parcool");
    private static final KeyMapping keyBindRideZipline =
            new KeyMapping(
                    "key.parcool.RideZipline",
                    InputConstants.Type.MOUSE,
                    GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                    "key.categories.parcool");
    private static final KeyMapping keyBindWallJump =
            new KeyMapping("key.parcool.WallJump", GLFW.GLFW_KEY_SPACE, "key.categories.parcool");
    private static final KeyMapping keyBindHangDown =
            new KeyMapping(
                    "key.parcool.HangDown",
                    InputConstants.Type.MOUSE,
                    GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                    "key.categories.parcool");
    private static final KeyMapping keyBindWallSlide =
            new KeyMapping(
                    "key.parcool.WallSlide",
                    InputConstants.Type.MOUSE,
                    GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                    "key.categories.parcool");
    private static final KeyMapping keyBindHideInBlock =
            new KeyMapping("key.parcool.HideInBlock", GLFW.GLFW_KEY_C, "key.categories.parcool");
    private static final KeyMapping keyBindHorizontalWallRun =
            new KeyMapping(
                    "key.parcool.HorizontalWallRun", GLFW.GLFW_KEY_R, "key.categories.parcool");
    private static final KeyMapping keyBindQuickTurn =
            new KeyMapping(
                    "key.parcool.QuickTurn", GLFW.GLFW_KEY_UNKNOWN, "key.categories.parcool");
    private static final KeyMapping keyBindOpenSettings =
            new KeyMapping("key.parcool.openSetting", GLFW.GLFW_KEY_P, "key.categories.parcool");
    private static final Vec3 forwardVector = new Vec3(0, 0, 1);

    public static KeyMapping getKeySprint() {
        return Minecraft.getInstance().options.keySprint;
    }

    public static Boolean isKeyJumpDown() {
        return Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.input != null
                && Minecraft.getInstance().player.input.jumping;
    }

    public static KeyMapping getKeySneak() {
        return Minecraft.getInstance().options.keyShift;
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
        return Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.input != null
                && (Minecraft.getInstance().player.input.left
                        || Minecraft.getInstance().player.input.right
                        || Minecraft.getInstance().player.input.forwardImpulse != 0
                        || Minecraft.getInstance().player.input.leftImpulse != 0);
    }

    public static Boolean isLeftAndRightDown() {
        return Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.input != null
                && Minecraft.getInstance().player.input.left
                && Minecraft.getInstance().player.input.right;
    }

    public static Boolean isKeyForwardDown() {
        return Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.input != null
                && Minecraft.getInstance().player.input.forwardImpulse > 0;
    }

    public static Boolean isKeyLeftDown() {
        return Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.input != null
                && Minecraft.getInstance().player.input.left;
    }

    public static Boolean isKeyRightDown() {
        return Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.input != null
                && Minecraft.getInstance().player.input.right;
    }

    public static Boolean isKeyBackDown() {
        return Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.input != null
                && Minecraft.getInstance().player.input.forwardImpulse < 0;
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

    public static void register() {
        KeyBindingHelper.registerKeyBinding(keyBindEnable);
        KeyBindingHelper.registerKeyBinding(keyBindCrawl);
        KeyBindingHelper.registerKeyBinding(keyBindGrabWall);
        KeyBindingHelper.registerKeyBinding(keyBindBreakfall);
        KeyBindingHelper.registerKeyBinding(keyBindFastRunning);
        KeyBindingHelper.registerKeyBinding(keyBindDodge);
        KeyBindingHelper.registerKeyBinding(keyBindRideZipline);
        KeyBindingHelper.registerKeyBinding(keyBindWallSlide);
        KeyBindingHelper.registerKeyBinding(keyBindWallJump);
        KeyBindingHelper.registerKeyBinding(keyBindVault);
        KeyBindingHelper.registerKeyBinding(keyBindHorizontalWallRun);
        KeyBindingHelper.registerKeyBinding(keyBindHideInBlock);
        KeyBindingHelper.registerKeyBinding(keyBindOpenSettings);
        KeyBindingHelper.registerKeyBinding(keyBindQuickTurn);
        KeyBindingHelper.registerKeyBinding(keyBindFlipping);
        KeyBindingHelper.registerKeyBinding(keyBindHangDown);
    }
}
