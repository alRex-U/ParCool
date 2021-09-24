package com.alrex.parcool.client;

public class ActionPermissions {
	static private boolean allowedCatLeap = true;
	static private boolean allowedCrawl = true;
	static private boolean allowedDodge = true;
	static private boolean allowedFastRunning = true;
	static private boolean allowedGrabCliff = true;
	static private boolean allowedRoll = true;
	static private boolean allowedVault = true;
	static private boolean allowedWallJump = true;
	static private boolean allowedInfiniteStamina = false;

	public static boolean isAllowedCatLeap() {
		return allowedCatLeap;
	}

	public static boolean isAllowedCrawl() {
		return allowedCrawl;
	}

	public static boolean isAllowedDodge() {
		return allowedDodge;
	}

	public static boolean isAllowedFastRunning() {
		return allowedFastRunning;
	}

	public static boolean isAllowedGrabCliff() {
		return allowedGrabCliff;
	}

	public static boolean isAllowedRoll() {
		return allowedRoll;
	}

	public static boolean isAllowedVault() {
		return allowedVault;
	}

	public static boolean isAllowedWallJump() {
		return allowedWallJump;
	}

	public static boolean isAllowedInfiniteStamina() {
		return allowedInfiniteStamina;
	}

	public static void setAllowedCatLeap(boolean allowedCatLeap) {
		ActionPermissions.allowedCatLeap = allowedCatLeap;
	}

	public static void setAllowedCrawl(boolean allowedCrawl) {
		ActionPermissions.allowedCrawl = allowedCrawl;
	}

	public static void setAllowedDodge(boolean allowedDodge) {
		ActionPermissions.allowedDodge = allowedDodge;
	}

	public static void setAllowedFastRunning(boolean allowedFastRunning) {
		ActionPermissions.allowedFastRunning = allowedFastRunning;
	}

	public static void setAllowedGrabCliff(boolean allowedGrabCliff) {
		ActionPermissions.allowedGrabCliff = allowedGrabCliff;
	}

	public static void setAllowedRoll(boolean allowedRoll) {
		ActionPermissions.allowedRoll = allowedRoll;
	}

	public static void setAllowedVault(boolean allowedVault) {
		ActionPermissions.allowedVault = allowedVault;
	}

	public static void setAllowedWallJump(boolean allowedWallJump) {
		ActionPermissions.allowedWallJump = allowedWallJump;
	}

	public static void setAllowedInfiniteStamina(boolean allowedInfinite) {
		allowedInfiniteStamina = allowedInfinite;
	}
}