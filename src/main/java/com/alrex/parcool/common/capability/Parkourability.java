package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.impl.*;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.info.ActionPermission;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Parkourability {
	@Nullable
	public static Parkourability get(PlayerEntity player) {
		LazyOptional<Parkourability> optional = player.getCapability(Capabilities.PARKOURABILITY_CAPABILITY);
		return optional.orElse(null);
	}

	private final CatLeap catLeap = new CatLeap();
	private final Crawl crawl = new Crawl();
	private final Dodge dodge = new Dodge();
	private final Dive dive = new Dive();
	private final ClimbUp climbUp = new ClimbUp();
	private final Slide slide = new Slide();
	private final FastRun fastRun = new FastRun();
	private final ClingToCliff clingToCliff = new ClingToCliff();
	private final Roll roll = new Roll();
	private final Vault vault = new Vault();
	private final WallJump wallJump = new WallJump();
	private final Flipping flipping = new Flipping();
	private final BreakfallReady breakfall = new BreakfallReady();
	private final Tap tap = new Tap();
	private final WallSlide wallSlide = new WallSlide();
	private final HorizontalWallRun horizontalWallRun = new HorizontalWallRun();
	private final AdditionalProperties additionalProperties = new AdditionalProperties();
	private final ActionPermission permission = new ActionPermission();
	private final ActionInfo actionInfo = new ActionInfo();

	private final List<Action> actions = Arrays.<Action>asList(
			catLeap,
			breakfall,
			crawl,
			slide,
			dodge,
			dive,
			climbUp,
			fastRun,
			clingToCliff,
			roll,
			vault,
			flipping,
			tap,
			wallSlide,
			horizontalWallRun,
			wallJump,
			additionalProperties

	);
	private final List<Class<? extends Action>> actionClasses
			= actions.stream().map(Action::getClass).collect(Collectors.toList());

	public short getActionID(Action instance) {
		for (short i = 0; i < actionClasses.size(); i++) {
			if (actionClasses.get(i).isInstance(instance)) {
				return i;
			}
		}
		return -1;
	}

	@Nullable
	public Action getActionFromID(short id) {
		if (0 <= id && id < actions.size()) {
			return actions.get(id);
		}
		return null;
	}

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

	public Dive getDive() {
		return dive;
	}

	public Slide getSlide() {
		return slide;
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

	public WallSlide getWallSlide() {
		return wallSlide;
	}

	public ClimbUp getClimbUp() {
		return climbUp;
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

	public Flipping getFlipping() {
		return flipping;
	}

	public BreakfallReady getBreakfallReady() {
		return breakfall;
	}

	public Tap getTap() {
		return tap;
	}

	public HorizontalWallRun getHorizontalWallRun() {
		return horizontalWallRun;
	}

	public List<Action> getList() {
		return actions;
	}
}
