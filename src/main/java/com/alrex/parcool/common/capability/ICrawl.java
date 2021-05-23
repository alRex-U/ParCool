package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

public interface ICrawl {
	@OnlyIn(Dist.CLIENT)
	public boolean canCrawl(PlayerEntity player);

	@OnlyIn(Dist.CLIENT)
	public boolean canSliding(PlayerEntity player);

	public boolean isCrawling();

	public void setCrawling(boolean crawling);

	public boolean isSliding();

	public void setSliding(boolean sliding);

	//only in Client
	public void updateSlidingTime(PlayerEntity player);

	public static ICrawl get(PlayerEntity entity) {
		LazyOptional<ICrawl> optional = entity.getCapability(Capabilities.CRAWL_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}
}
