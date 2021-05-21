package com.alrex.parcool.common.capability;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.impl.Crawl;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ICrawl {
	//only in Client
	public boolean canCrawl(ClientPlayerEntity player);

	//only in Client
	public boolean canSliding(ClientPlayerEntity player);

	public boolean isCrawling();

	public void setCrawling(boolean crawling);

	public boolean isSliding();

	public void setSliding(boolean sliding);

	//only in Client
	public void updateSlidingTime(ClientPlayerEntity player);

	public static class CrawlStorage implements Capability.IStorage<ICrawl> {
		@Override
		public void readNBT(Capability<ICrawl> capability, ICrawl instance, Direction side, INBT nbt) {
		}

		@Nullable
		@Override
		public INBT writeNBT(Capability<ICrawl> capability, ICrawl instance, Direction side) {
			return null;
		}
	}

	public static ICrawl get(PlayerEntity entity) {
		LazyOptional<ICrawl> optional = entity.getCapability(CrawlProvider.CRAWL_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}

	public static class CrawlProvider implements ICapabilityProvider {
		@CapabilityInject(ICrawl.class)
		public static final Capability<ICrawl> CRAWL_CAPABILITY = null;
		public static final ResourceLocation CAPABILITY_LOCATION = new ResourceLocation(ParCool.MOD_ID, "capability.parcool.crawl");

		private LazyOptional<ICrawl> instance = LazyOptional.of(CRAWL_CAPABILITY::getDefaultInstance);

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			return cap == CRAWL_CAPABILITY ? instance.cast() : LazyOptional.empty();
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
			return cap == CRAWL_CAPABILITY ? instance.cast() : LazyOptional.empty();
		}
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class CrawlRegistry {
		@SubscribeEvent
		public static void register(FMLCommonSetupEvent event) {
			CapabilityManager.INSTANCE.register(ICrawl.class, new ICrawl.CrawlStorage(), Crawl::new);
		}
	}
}
