package com.alrex.parcool;

import com.alrex.parcool.api.*;
import com.alrex.parcool.common.action.ActionProcessor;
import com.alrex.parcool.common.action.ActionRegistry;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.common.architectury.event.RegisterParCoolActionArchEvent;
import com.alrex.parcool.common.architectury.event.RegisterParCoolStaminaArchEvent;
import com.alrex.parcool.common.block.TileEntities;
import com.alrex.parcool.common.item.ParCoolItems;
import com.alrex.parcool.common.item.recipe.Recipes;
import com.alrex.parcool.common.potion.PotionRecipeRegistry;
import com.alrex.parcool.common.stamina.StaminaTypeRegistry;
import com.alrex.parcool.common.stamina.StaminaTypes;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.proxy.ClientProxy;
import com.alrex.parcool.proxy.CommonProxy;
import com.alrex.parcool.proxy.ServerProxy;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.resources.ResourceLocation;

public class ParCool {
    public static final String MOD_ID = "parcool";
    public static final CommonProxy PROXY = EnvExecutor.getEnvSpecific(
            () -> ClientProxy::new,
            () -> ServerProxy::new
    );

    protected static final ActionRegistry actionRegistry = new ActionRegistry();
    protected static final StaminaTypeRegistry staminaTypeRegistry = new StaminaTypeRegistry();
    protected static final ActionProcessor actionProcessor = new ActionProcessor();
    protected static final LogicalSide side = EnvExecutor.getEnvSpecific(() -> () -> LogicalSide.CLIENT, () -> () -> LogicalSide.SERVER);
    protected static ParCoolConfig config;

    public static ResourceLocation resourceLocation(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static ActionRegistry getActionRegistry() {
        return actionRegistry;
    }

    public static StaminaTypeRegistry getStaminaTypeRegistry() {
        return staminaTypeRegistry;
    }

    public static ParCoolConfig getConfig() {
        return config;
    }

    public static ActionProcessor getActionProcessor() {
        return actionProcessor;
    }

    public static LogicalSide logicalSide() {
        return side;
    }

    protected void init() {
        ParCoolMobEffects.register();
        ParCoolPotions.register();
        ParCoolAttributes.register();
        ParCoolSoundEvents.register();
        ParCoolBlocks.register();
        ParCoolItems.register();
        Recipes.register();
        TileEntities.register();
        PotionRecipeRegistry.register();

        RegisterParCoolActionArchEvent.EVENT.register(ParCoolActions::onRegister);
        RegisterParCoolStaminaArchEvent.EVENT.register(StaminaTypes::onRegister);

        PROXY.init();
        LifecycleEvent.SETUP.register(PROXY::registerMessages);
        TickEvent.SERVER_LEVEL_POST.register(actionProcessor::onTickLevel);
        TickEvent.PLAYER_POST.register(actionProcessor::onTickPlayer);

        RegisterParCoolStaminaArchEvent.EVENT.invoker().onRegisterParCoolStamina(staminaTypeRegistry);
        staminaTypeRegistry.freeze();
        RegisterParCoolActionArchEvent.EVENT.invoker().onRegisterParCoolAction(actionRegistry);
        actionRegistry.freeze();
    }
}
