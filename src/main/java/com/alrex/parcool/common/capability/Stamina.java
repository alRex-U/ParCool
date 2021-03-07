package com.alrex.parcool.common.capability;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Stamina implements IStamina{
    private int stamina = 0;
    private boolean exhausted=false;

    @Override
    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    @Override
    public int getStamina() {
        return stamina;
    }

    @Override
    public int getMaxStamina() {
        return 1000;
    }

    @Override
    public void consume(int amount) {
        stamina-=amount;
        if (stamina<=0){
            stamina=0;
            setExhausted(true);
        }
    }

    @Override
    public void recover(int amount) {
        stamina+=amount;
        if (stamina>=getMaxStamina()){
            stamina=getMaxStamina();
            setExhausted(false);
        }
    }

    @Override
    public void setExhausted(boolean exhausted) {
        this.exhausted = exhausted;
    }
    @Override
    public boolean isExhausted() {
        return exhausted;
    }

    @OnlyIn(Dist.CLIENT)
    public void syncState(ClientPlayerEntity player){

    }
}
