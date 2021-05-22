package com.alrex.parcool.common.registries;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.capability.*;
import com.alrex.parcool.common.network.*;
import net.minecraftforge.eventbus.api.IEventBus;

public class EventBusModRegistry {
	public static void registry(IEventBus bus) {
		bus.register(KeyBindings.class);

		bus.register(ICatLeap.CatLeapRegistry.class);
		bus.register(ICrawl.CrawlRegistry.class);
		bus.register(IFastRunning.FastRunningRegistry.class);
		bus.register(IGrabCliff.GrabCliffRegistry.class);
		bus.register(IRoll.RollRegistry.class);
		bus.register(IStamina.StaminaRegistry.class);
		bus.register(IVault.VaultRegistry.class);
		bus.register(IWallJump.WallJumpRegistry.class);

		bus.register(ResetFallDistanceMessage.MessageRegistry.class);
		bus.register(SetActionPossibilityMessage.MessageRegistry.class);
		bus.register(ShowActionPossibilityMessage.MessageRegistry.class);
		bus.register(StartRollMessage.MessageRegistry.class);
		bus.register(SyncCatLeapMessage.MessageRegistry.class);
		bus.register(SyncCrawlMessage.MessageRegistry.class);
		bus.register(SyncDodgeMessage.MessageRegistry.class);
		bus.register(SyncFastRunningMessage.MessageRegistry.class);
		bus.register(SyncGrabCliffMessage.MessageRegistry.class);
		bus.register(SyncRollReadyMessage.MessageRegistry.class);
		bus.register(SyncStaminaMessage.MessageRegistry.class);
	}
}
