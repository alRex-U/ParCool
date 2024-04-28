package com.alrex.parcool.server.command;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.server.command.args.ActionArgumentType;
import com.alrex.parcool.server.command.args.LimitationIDArgumentType;
import com.alrex.parcool.server.command.args.LimitationItemArgumentType;
import com.alrex.parcool.server.command.impl.ControlLimitationCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommandRegistry {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal(ParCool.MOD_ID)
						.then(ControlLimitationCommand.getBuilder())
		);
	}

	public static void registerArgumentTypes(FMLCommonSetupEvent event) {
		ArgumentTypeInfos.registerByClass(ActionArgumentType.class, SingletonArgumentInfo.contextFree(ActionArgumentType::action));
		ArgumentTypeInfos.registerByClass(LimitationIDArgumentType.class, SingletonArgumentInfo.contextFree(LimitationIDArgumentType::limitation));
		ArgumentTypeInfos.registerByClass(LimitationItemArgumentType.Booleans.class, SingletonArgumentInfo.contextFree(LimitationItemArgumentType::booleans));
		ArgumentTypeInfos.registerByClass(LimitationItemArgumentType.Integers.class, SingletonArgumentInfo.contextFree(LimitationItemArgumentType::integers));
		ArgumentTypeInfos.registerByClass(LimitationItemArgumentType.Doubles.class, SingletonArgumentInfo.contextFree(LimitationItemArgumentType::doubles));
	}
}
