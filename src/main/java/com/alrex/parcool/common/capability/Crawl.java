package com.alrex.parcool.common.capability;

import com.alrex.parcool.client.input.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.common.util.LazyOptional;

public class Crawl implements ICrawl{
    private static final int maxSlidingTime=15;

    private boolean crawling=false;
    private boolean sliding=false;
    private int slidingTime=-1;
    @Override
    public boolean isCrawling() {
        return crawling;
    }
    @Override
    public void setCrawling(boolean crawling) {
        this.crawling = crawling;
    }
    @Override
    public boolean isSliding() { return sliding; }
    @Override
    public void setSliding(boolean sliding) { this.sliding = sliding; }

    @Override
    public boolean canCrawl(ClientPlayerEntity player) {
        return KeyBindings.getKeyCrawl().isKeyDown() && !player.isInWaterOrBubbleColumn() && player.isOnGround();
    }

    @Override
    public boolean canSliding(ClientPlayerEntity player) {
        LazyOptional<IFastRunning> fastOptional=player.getCapability(IFastRunning.FastRunningProvider.FAST_RUNNING_CAPABILITY);
        if (!fastOptional.isPresent())return false;
        IFastRunning fastRunning=fastOptional.resolve().get();

    if (!isSliding() && fastRunning.isFastRunning() && KeyBindings.getKeyCrawl().isKeyDown() && slidingTime>=0){
            return true;
        }
        if (isSliding() && slidingTime<=maxSlidingTime)return true;
        return false;
    }

    @Override
    public void updateSlidingTime(ClientPlayerEntity player) {
        if (slidingTime<0 && KeyBindings.getKeyCrawl().isKeyDown())return;

        if (isSliding()) slidingTime++; else slidingTime=0;
        if ((slidingTime>maxSlidingTime && player.isOnGround())|| player.isInWaterOrBubbleColumn()){
            slidingTime=-1;
            setSliding(false);
        }
    }
}
