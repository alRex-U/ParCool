package com.alrex.parcool.server.command.args;

import com.alrex.parcool.ParCool;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ParCoolArgumentTypeInfos {
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, ParCool.MOD_ID);
    private static final Holder<ArgumentTypeInfo<?, ?>> ACTION_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("action", () -> ArgumentTypeInfos.registerByClass(ActionArgumentType.class, SingletonArgumentInfo.contextFree(ActionArgumentType::action)));
    private static final Holder<ArgumentTypeInfo<?, ?>> LIMITATION_BOOL_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("limitation_bool", () -> ArgumentTypeInfos.registerByClass(LimitationItemArgumentType.Booleans.class, SingletonArgumentInfo.contextFree(LimitationItemArgumentType::booleans)));
    private static final Holder<ArgumentTypeInfo<?, ?>> LIMITATION_INT_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("limitation_int", () -> ArgumentTypeInfos.registerByClass(LimitationItemArgumentType.Integers.class, SingletonArgumentInfo.contextFree(LimitationItemArgumentType::integers)));
    private static final Holder<ArgumentTypeInfo<?, ?>> LIMITATION_REAL_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("limitation_reals", () -> ArgumentTypeInfos.registerByClass(LimitationItemArgumentType.Doubles.class, SingletonArgumentInfo.contextFree(LimitationItemArgumentType::doubles)));
    private static final Holder<ArgumentTypeInfo<?, ?>> LIMITATION_ID_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("limitation_id", () -> ArgumentTypeInfos.registerByClass(LimitationIDArgumentType.class, SingletonArgumentInfo.contextFree(LimitationIDArgumentType::new)));
    private static final Holder<ArgumentTypeInfo<?, ?>> STAMINA_TYPE_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("stamina_type", () -> ArgumentTypeInfos.registerByClass(StaminaTypeArgumentType.class, SingletonArgumentInfo.contextFree(StaminaTypeArgumentType::new)));

    public static void registerAll(IEventBus bus) {
        COMMAND_ARGUMENT_TYPES.register(bus);
    }
}
