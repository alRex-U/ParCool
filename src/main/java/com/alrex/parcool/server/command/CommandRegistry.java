package com.alrex.parcool.server.command;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.server.command.args.ActionArgumentType;
import com.alrex.parcool.server.command.args.LimitationItemArgumentType;
import com.alrex.parcool.server.command.impl.ChangeIndividualLimitationCommand;
import com.alrex.parcool.server.command.impl.StaminaControlCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommandRegistry {
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal(ParCool.MOD_ID)
						.then(StaminaControlCommand.getBuilder())
						.then(ChangeIndividualLimitationCommand.getBuilder())
		);
	}

	public static void registerArgumentTypes(FMLCommonSetupEvent event) {
		ArgumentTypes.register("parcool:action_name", ActionArgumentType.class, new ArgumentSerializer<>(ActionArgumentType::new));
		ArgumentTypes.register("parcool:limitation_item_name", LimitationItemArgumentType.class, new LimitationItemArgumentType.Serializer());
	}
}
