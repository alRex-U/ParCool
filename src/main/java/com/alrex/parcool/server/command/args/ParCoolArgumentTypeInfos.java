package com.alrex.parcool.server.command.args;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.fabric.IEventBus;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.ResourceLocation;

public class ParCoolArgumentTypeInfos {
    public static void registerAll(IEventBus bus) {
        register(
                "action",
                ActionArgumentType.class,
                SingletonArgumentInfo.contextFree(ActionArgumentType::action));
        register(
                "limitation_bool",
                LimitationItemArgumentType.Booleans.class,
                SingletonArgumentInfo.contextFree(LimitationItemArgumentType::booleans));
        register(
                "limitation_int",
                LimitationItemArgumentType.Integers.class,
                SingletonArgumentInfo.contextFree(LimitationItemArgumentType::integers));
        register(
                "limitation_reals",
                LimitationItemArgumentType.Doubles.class,
                SingletonArgumentInfo.contextFree(LimitationItemArgumentType::doubles));
        register(
                "limitation_id",
                LimitationIDArgumentType.class,
                SingletonArgumentInfo.contextFree(LimitationIDArgumentType::new));
        register(
                "stamina_type",
                StaminaTypeArgumentType.class,
                SingletonArgumentInfo.contextFree(StaminaTypeArgumentType::new));
    }

    private static <
                    A extends com.mojang.brigadier.arguments.ArgumentType<?>,
                    T extends net.minecraft.commands.synchronization.ArgumentTypeInfo.Template<A>>
            void register(
                    String name,
                    Class<A> type,
                    net.minecraft.commands.synchronization.ArgumentTypeInfo<A, T> info) {
        ArgumentTypeRegistry.registerArgumentType(
                ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, name), type, info);
    }
}
