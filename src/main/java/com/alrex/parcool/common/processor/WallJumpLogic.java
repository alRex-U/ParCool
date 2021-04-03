package com.alrex.parcool.common.processor;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.IWallJump;
import com.alrex.parcool.common.network.ResetFallDistanceMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WallJumpLogic {
    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event){
        if (event.phase == TickEvent.Phase.END)return;
        ClientPlayerEntity player=Minecraft.getInstance().player;
        if (event.player != player)return;
        if (!ParCool.isActive())return;

        IWallJump wallJump;
        IStamina stamina;
        {
            LazyOptional<IStamina> staminaOptional = player.getCapability(IStamina.StaminaProvider.STAMINA_CAPABILITY);
            LazyOptional<IWallJump> wallJumpOptional=player.getCapability(IWallJump.WallJumpProvider.WALL_JUMP_CAPABILITY);
            if (!wallJumpOptional.isPresent() || !staminaOptional.isPresent())return;
            stamina = staminaOptional.resolve().get();
            wallJump= wallJumpOptional.resolve().get();
        }

        if (wallJump.canWallJump(player)){
            Vector3d jumpDirection=wallJump.getJumpDirection(player);
            if (jumpDirection==null) return;

            Vector3d direction=new Vector3d(jumpDirection.getX(), 1.4, jumpDirection.getZ()).scale(wallJump.getJumpPower());
            Vector3d motion=player.getMotion();

            stamina.consume(wallJump.getStaminaConsumption());
            player.setMotion(
                    motion.getX()+direction.getX(),
                    motion.getY() > direction.getY() ? motion.y+direction.getY() : direction.getY(),
                    motion.getZ()+direction.getZ()
            );
            ResetFallDistanceMessage.sync(player);
        }
    }
}
