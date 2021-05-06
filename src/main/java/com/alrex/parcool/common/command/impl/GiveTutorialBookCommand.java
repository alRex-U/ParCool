package com.alrex.parcool.common.command.impl;

import com.alrex.parcool.ParCool;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class GiveTutorialBookCommand implements Command<CommandSource> {
	private static final GiveTutorialBookCommand CMD = new GiveTutorialBookCommand();
	private static CompoundNBT tutorialBookNBT = null;

	private static CompoundNBT getTutorialBookNBT() {
		if (tutorialBookNBT == null) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putString("title", "ParCool-Tutorial-Guide");
			nbt.putString("author", "ParCool Mod");
			ListNBT pages = new ListNBT();
			getPages().forEach(pages::add);
			nbt.put("pages", pages);
			tutorialBookNBT = nbt;
		}
		return tutorialBookNBT;
	}

	public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
		return Commands
				.literal("giveTutorialBook")
				.requires(commandSource -> commandSource.hasPermissionLevel(0))
				.executes(CMD);
	}

	@Override
	public int run(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
		commandContext.getSource().asPlayer().addItemStackToInventory(getTutorialBook());
		return 0;
	}

	private ItemStack getTutorialBook() {
		ItemStack stack = new ItemStack(Items.WRITTEN_BOOK, 1);
		stack.setTag(getTutorialBookNBT());
		return stack;
	}

	private static Stream<StringNBT> getPages() {
		final String path = "/assets/parcool/book/parcool_tutorial_book.txt";
		ArrayList<String> arrayList = new ArrayList<>();
		try {
			InputStream stream = new BufferedInputStream(ParCool.MOD_ID.getClass().getResourceAsStream(path));

			LineIterator iterator = IOUtils.lineIterator(stream, StandardCharsets.UTF_8);

			//=======
			// replace division line -> \\n and set to list
			Pattern division = Pattern.compile("===+");
			AtomicReference<StringBuilder> builder = new AtomicReference<>(new StringBuilder());
			iterator.forEachRemaining(line -> {
				if (!division.matcher(line).matches()) {
					builder.get().append(line).append("\\n");
				} else {//division line
					arrayList.add(builder.toString());
					if (iterator.hasNext()) builder.set(new StringBuilder());
				}
			});
			//=======

			stream.close();
		} catch (IOException | NullPointerException e) {
			return Stream.of(StringNBT.valueOf("Data were invalid."));
		}
		return arrayList.stream().map(item -> StringNBT.valueOf(String.format("{\"text\":\"%s\"}", item)));
	}
}