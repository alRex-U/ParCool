package com.alrex.parcool.common.processor;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.capability.FastRunning;
import com.alrex.parcool.common.capability.IFastRunning;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.network.SyncFastRunningMessage;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FastRunningLogic {
    private static final String FAST_RUNNING_MODIFIER_NAME="parCool.modifier.fastrunnning";
    private static final UUID FAST_RUNNING_MODIFIER_UUID=UUID.randomUUID();
    private static final AttributeModifier FAST_RUNNING_MODIFIER
            = new AttributeModifier(
                    FAST_RUNNING_MODIFIER_UUID,
                    FAST_RUNNING_MODIFIER_NAME,
                    0.041,
                    AttributeModifier.Operation.ADDITION
            );

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if (!event.player.world.isRemote || event.phase != TickEvent.Phase.START)return;
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != event.player)return;
        LazyOptional<IFastRunning> fastRunningOptional=player.getCapability(IFastRunning.FastRunningProvider.FAST_RUNNING_CAPABILITY);
        if (!fastRunningOptional.isPresent())return;
        IFastRunning fastRunning=fastRunningOptional.resolve().get();

        ModifiableAttributeInstance attr=player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr==null)return;

        boolean oldFastRunning=fastRunning.isFastRunning();
        fastRunning.setFastRunning(fastRunning.canFastRunning());

        if (fastRunning.isFastRunning()!=oldFastRunning) SyncFastRunningMessage.sync(player);

        if (fastRunning.isFastRunning()){
            if (!attr.hasModifier(FAST_RUNNING_MODIFIER))attr.applyPersistentModifier(FAST_RUNNING_MODIFIER);
        }else {
            if (attr.hasModifier(FAST_RUNNING_MODIFIER))attr.removeModifier(FAST_RUNNING_MODIFIER);
        }
    }
}
