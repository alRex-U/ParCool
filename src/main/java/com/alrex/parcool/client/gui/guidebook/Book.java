package com.alrex.parcool.client.gui.guidebook;


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
		private String title;
		private List<String> content;

		public String getTitle() {
			return title;
		}

		public List<String> getContent() {
			return content;
		}

		public Page(String title, List<String> content) {
			this.title = title;
			this.content = content;
		}
	}
}
