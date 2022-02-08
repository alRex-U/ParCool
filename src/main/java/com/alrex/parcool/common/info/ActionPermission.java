package com.alrex.parcool.common.info;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.network.ActionPermissionsMessage;

public class ActionPermission {
	private boolean haveReceived = false;
	private boolean allowedCatLeap = true;
	private boolean allowedCrawl = true;
	private boolean allowedDodge = true;
	private boolean allowedFastRunning = true;
	private boolean allowedClingToCliff = true;
	private boolean allowedRoll = true;
	private boolean allowedVault = true;
	private boolean allowedWallJump = true;

	public boolean canCatLeap() {
		return ParCool.isActive() && haveReceived && ParCoolConfig.CONFIG_CLIENT.canCatLeap.get() && allowedCatLeap;
	}

	public boolean canCrawl() {
		return ParCool.isActive() && haveReceived && ParCoolConfig.CONFIG_CLIENT.canCrawl.get() && allowedCrawl;
	}

	public boolean canDodge() {
		return ParCool.isActive() && haveReceived && ParCoolConfig.CONFIG_CLIENT.canDodge.get() && allowedDodge;
	}

	public boolean canFastRunning() {
		return ParCool.isActive() && haveReceived && ParCoolConfig.CONFIG_CLIENT.canFastRunning.get() && allowedFastRunning;
	}

	public boolean canClingToCliff() {
		return ParCool.isActive() && haveReceived && ParCoolConfig.CONFIG_CLIENT.canClingToCliff.get() && allowedClingToCliff;
	}

	public boolean canRoll() {
		return ParCool.isActive() && haveReceived && ParCoolConfig.CONFIG_CLIENT.canRoll.get() && allowedRoll;
	}

	public boolean canVault() {
		return ParCool.isActive() && haveReceived && ParCoolConfig.CONFIG_CLIENT.canVault.get() && allowedVault;
	}

	public boolean canWallJump() {
		return ParCool.isActive() && haveReceived && ParCoolConfig.CONFIG_CLIENT.canWallJump.get() && allowedWallJump;
	}

	public void receiveServerPermissions(ActionPermissionsMessage message) {
		allowedCatLeap = message.isAllowedCatLeap();
		allowedCrawl = message.isAllowedCrawl();
		allowedClingToCliff = message.isAllowedClingToCliff();
		allowedDodge = message.isAllowedDodge();
		allowedFastRunning = message.isAllowedFastRunning();
		allowedRoll = message.isAllowedRoll();
		allowedVault = message.isAllowedVault();
		allowedWallJump = message.isAllowedWallJump();
		haveReceived = true;
	}
}
