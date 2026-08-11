package com.alrex.parcool.client.md;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

@Environment(EnvType.CLIENT)
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

    /// Extension for display key bind
    /// - `<key name = "key.parcool.crawl" />` -> `C`
    record ExtensionMCKey(String keyBindName) implements MarkdownText {
    }

    /// Extension for display translation
    /// - `<translation key = "parcool.action.parcool.crawl" />` -> `Crawl`
    record ExtensionMCTranslatable(String translationKey) implements MarkdownText {
    }

    /// Extension for display item name
    /// - `<item id = "parcool:parcool_guide" />` -> `ParCool Guide`
    record ExtensionMCItemName(ResourceLocation id) implements MarkdownText {
    }
}
