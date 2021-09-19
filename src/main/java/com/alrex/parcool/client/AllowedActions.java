package com.alrex.parcool.client;

public class AllowedActions {
	static private boolean allowedCatLeap = true;
	static private boolean allowedCrawl = true;
	static private boolean allowedDodge = true;
	static private boolean allowedFastRunning = true;
	static private boolean allowedGrabCliff = true;
	static private boolean allowedRoll = true;
	static private boolean allowedVault = true;
	static private boolean allowedWallJump = true;

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

	public static void setAllowedCatLeap(boolean allowedCatLeap) {
		AllowedActions.allowedCatLeap = allowedCatLeap;
	}

	public static void setAllowedCrawl(boolean allowedCrawl) {
		AllowedActions.allowedCrawl = allowedCrawl;
	}

	public static void setAllowedDodge(boolean allowedDodge) {
		AllowedActions.allowedDodge = allowedDodge;
	}

	public static void setAllowedFastRunning(boolean allowedFastRunning) {
		AllowedActions.allowedFastRunning = allowedFastRunning;
	}

	public static void setAllowedGrabCliff(boolean allowedGrabCliff) {
		AllowedActions.allowedGrabCliff = allowedGrabCliff;
	}

	public static void setAllowedRoll(boolean allowedRoll) {
		AllowedActions.allowedRoll = allowedRoll;
	}

	public static void setAllowedVault(boolean allowedVault) {
		AllowedActions.allowedVault = allowedVault;
	}

	public static void setAllowedWallJump(boolean allowedWallJump) {
		AllowedActions.allowedWallJump = allowedWallJump;
	}
}