package com.alrex.parcool.common.capability.capabilities;

import com.alrex.parcool.common.capability.*;
import com.alrex.parcool.common.capability.impl.*;
import com.alrex.parcool.common.capability.storage.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class Capabilities {
	@CapabilityInject(ICrawl.class)
	public static final Capability<ICrawl> CRAWL_CAPABILITY = null;
	@CapabilityInject(ICatLeap.class)
	public static final Capability<ICatLeap> CAT_LEAP_CAPABILITY = null;
	@CapabilityInject(IDodge.class)
	public static final Capability<IDodge> DODGE_CAPABILITY = null;
	@CapabilityInject(IFastRunning.class)
	public static final Capability<IFastRunning> FAST_RUNNING_CAPABILITY = null;
	@CapabilityInject(IGrabCliff.class)
	public static final Capability<IGrabCliff> GRAB_CLIFF_CAPABILITY = null;
	@CapabilityInject(IRoll.class)
	public static final Capability<IRoll> ROLL_CAPABILITY = null;
	@CapabilityInject(IStamina.class)
	public static final Capability<IStamina> STAMINA_CAPABILITY = null;
	@CapabilityInject(IVault.class)
	public static final Capability<IVault> VAULT_CAPABILITY = null;
	@CapabilityInject(IWallJump.class)
	public static final Capability<IWallJump> WALL_JUMP_CAPABILITY = null;

	public static void registerAll(CapabilityManager manager) {
		manager.register(ICatLeap.class, new CatLeapStorage(), CatLeap::new);
		manager.register(ICrawl.class, new CrawlStorage(), Crawl::new);
		manager.register(IDodge.class, new DodgeStorage(), Dodge::new);
		manager.register(IFastRunning.class, new FastRunningStorage(), FastRunning::new);
		manager.register(IGrabCliff.class, new GrabCliffStorage(), GrabCliff::new);
		manager.register(IRoll.class, new RollStorage(), Roll::new);
		manager.register(IStamina.class, new StaminaStorage(), Stamina::new);
		manager.register(IVault.class, new VaultStorage(), Vault::new);
		manager.register(IWallJump.class, new WallJumpStorage(), WallJump::new);
	}
}
