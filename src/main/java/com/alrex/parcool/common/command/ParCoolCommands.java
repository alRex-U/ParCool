package com.alrex.parcool.common.command;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.command.impl.AllowInfiniteStaminaCommand;
import com.alrex.parcool.common.command.impl.ChangePossibilityCommand;
import com.alrex.parcool.common.command.impl.SetInfiniteStaminaCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class ParCoolCommands {
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal(ParCool.MOD_ID)
						.then(ChangePossibilityCommand.register(dispatcher))
						.then(AllowInfiniteStaminaCommand.register(dispatcher))
						.then(SetInfiniteStaminaCommand.register(dispatcher))
		);
	}
}