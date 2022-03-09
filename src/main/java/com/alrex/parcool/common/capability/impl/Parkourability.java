package com.alrex.parcool.common.capability.impl;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.impl.*;
import com.alrex.parcool.common.capability.provider.ParkourabilityProvider;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.info.ActionPermission;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class Parkourability {
	@Nullable
	public static Parkourability get(Player player) {
		LazyOptional<Parkourability> optional = player.getCapability(ParkourabilityProvider.PARKOURABILITY_CAPABILITY);
		return optional.orElse(null);
	}

	private final CatLeap catLeap = new CatLeap();
	private final Crawl crawl = new Crawl();
	private final Dodge dodge = new Dodge();
	private final FastRun fastRun = new FastRun();
	private final ClingToCliff clingToCliff = new ClingToCliff();
	private final Roll roll = new Roll();
	private final Vault vault = new Vault();
	private final WallJump wallJump = new WallJump();
	private final AdditionalProperties additionalProperties = new AdditionalProperties();
	private final ActionPermission permission = new ActionPermission();
	private final ActionInfo actionInfo = new ActionInfo();

	private final List<Action> actions = Arrays.<Action>asList(
			catLeap, crawl, dodge, fastRun, clingToCliff, roll, vault, wallJump, additionalProperties
	);

	public CatLeap getCatLeap() {
		return catLeap;
	}

	public Crawl getCrawl() {
		return crawl;
	}

	public ClingToCliff getClingToCliff() {
		return clingToCliff;
	}

	public Dodge getDodge() {
		return dodge;
	}

	public FastRun getFastRun() {
		return fastRun;
	}

	public Roll getRoll() {
		return roll;
	}

	public Vault getVault() {
		return vault;
	}

	public WallJump getWallJump() {
		return wallJump;
	}

	public AdditionalProperties getAdditionalProperties() {
		return additionalProperties;
	}

	public ActionInfo getActionInfo() {
		return actionInfo;
	}

	public ActionPermission getPermission() {
		return permission;
	}

	public List<Action> getList() {
		return actions;
	}
}
