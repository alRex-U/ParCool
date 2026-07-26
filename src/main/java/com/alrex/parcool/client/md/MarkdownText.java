package com.alrex.parcool.client.md;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface MarkdownText {
    record ExternalLink(String text, String url) implements MarkdownText {
    }

    record Link(String text, ResourceLocation location) implements MarkdownText {
    }

    record Emphasis(List<MarkdownText> child) implements MarkdownText {
    }

    record Strong(List<MarkdownText> child) implements MarkdownText {
    }

    record Text(String text) implements MarkdownText {
    }

    class LineBreak implements MarkdownText {
    }
}
