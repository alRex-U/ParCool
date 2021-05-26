package com.alrex.parcool.common.capability.provider;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.ICrawl;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CrawlProvider implements ICapabilityProvider {
	public static final ResourceLocation CAPABILITY_LOCATION = new ResourceLocation(ParCool.MOD_ID, "capability.parcool.crawl");

	private LazyOptional<ICrawl> instance = LazyOptional.of(Capabilities.CRAWL_CAPABILITY::getDefaultInstance);

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return cap == Capabilities.CRAWL_CAPABILITY ? instance.cast() : LazyOptional.empty();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
		return cap == Capabilities.CRAWL_CAPABILITY ? instance.cast() : LazyOptional.empty();
	}
}
