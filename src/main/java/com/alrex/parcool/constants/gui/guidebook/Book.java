package com.alrex.parcool.constants.gui.guidebook;

import net.minecraft.util.text.ITextProperties;

import java.util.List;

public class Book {
	public List<Page> getPages() {
		return pages;
	}

	List<Page> pages;

	public Book(List<Page> pages) {
		this.pages = pages;
	}

	public static class Page {
		private ITextProperties title;
		private List<ITextProperties> content;

		public ITextProperties getTitle() {
			return title;
		}

		public List<ITextProperties> getContent() {
			return content;
		}

		public Page(ITextProperties title, List<ITextProperties> content) {
			this.title = title;
			this.content = content;
		}
	}
}
