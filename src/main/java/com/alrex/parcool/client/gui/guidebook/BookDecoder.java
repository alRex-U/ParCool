package com.alrex.parcool.client.gui.guidebook;

import com.alrex.parcool.ParCool;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BookDecoder {
	private final static String PATH = "/assets/parcool/book/parcool_guide_content_%s.txt";
	private final static String[] sequenceCode = new String[]{
			"{reset}", "{black}", "{white}", "{yellow}", "{blue}", "{green}", "{red}", "{purple}", "{obfuscate}", "{B}", "{-}", "{_}", "{I}"
	};
	private final static String[] escapedSequences = new String[]{
			"§r", "§0", "§f", "§e", "§9", "§a", "§c", "§5", "§k", "§l", "§m", "§n", "§o"

	};
	private static BookDecoder instance = null;
	private final Map<String, Book> bookCache = new HashMap<>();

	public static BookDecoder getInstance() {
		if (instance == null) instance = new BookDecoder();
		return instance;
	}

	public Book getBook() {
		return getBook(Minecraft.getInstance().getLanguageManager().getSelected().getCode());
	}

	public Book getBook(String langCode) {
		Book book = bookCache.get(langCode);
		if (book != null) return book;

		book = loadBook(langCode);
		bookCache.put(langCode, book);
		return book;
	}

	private String decodeDecoration(String text) {
		return StringUtils.replaceEach(text, sequenceCode, escapedSequences);
	}

	private Book loadBook(String langCode) {
		InputStream stream = ParCool.class.getResourceAsStream(String.format(PATH, langCode));
		if (stream == null) {
			stream = ParCool.class.getResourceAsStream(String.format(PATH, "en_us"));
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		ArrayList<Book.Page> pages = new ArrayList<>();

		Iterator<String> iterator = reader.lines().iterator();
		Pattern division = Pattern.compile("===+");
		while (iterator.hasNext()) {
			String title = iterator.next();
			iterator.next(); //ignore one line
			List<String> content = new ArrayList<>();
			while (true) {
				String line = iterator.next();

				if (division.matcher(line).matches()) {//end page
					pages.add(new Book.Page(
							decodeDecoration(title),
							content.stream().map(this::decodeDecoration).collect(Collectors.toList())
					));
					break;
				} else {
					content.add(line);
				}
			}
		}
		//=======
		return new Book(pages);
	}
}
