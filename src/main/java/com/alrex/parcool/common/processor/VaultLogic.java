package com.alrex.parcool.common.processor;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IVault;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VaultLogic {
    private static double wallHeight=0;
    private static Vector3d stepDirection=null;
    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event){
        if (event.phase!= TickEvent.Phase.START || !event.player.world.isRemote)return;

        ClientPlayerEntity player= Minecraft.getInstance().player;
        if (player!=event.player)return;
        if (!ParCool.isActive())return;

        IVault vault;
        {
            LazyOptional<IVault> vaultOptional = player.getCapability(IVault.VaultProvider.VAULT_CAPABILITY);
            if (!vaultOptional.isPresent())return;
            vault=vaultOptional.resolve().get();
        }
        if (!vault.isVaulting() && vault.canVault(player)){
            vault.setVaulting(true);
            stepDirection= WorldUtil.getStep(player);
            wallHeight= WorldUtil.getWallHeight(player);
        }

        if (vault.isVaulting()){
            player.setMotion(stepDirection.getX()/10,(wallHeight+0.05)/vault.getVaultAnimateTime(),stepDirection.getZ()/10);
        }
        if (vault.getVaultingTime() >= vault.getVaultAnimateTime()){
            vault.setVaulting(false);
            stepDirection=stepDirection.normalize();
            player.setMotion(stepDirection.getX()*0.45,0.15,stepDirection.getZ()*0.45d);
        }

        vault.updateVaultingTime();
    }
}
