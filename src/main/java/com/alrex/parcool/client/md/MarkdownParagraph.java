package com.alrex.parcool.client.md;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public interface MarkdownParagraph {
    record Image(ResourceLocation textureLocation, @Nullable String caption) implements MarkdownParagraph {
    }

    class HorizontalLine implements MarkdownParagraph {
    }

    record OrderedList(List<MarkdownParagraph.Text> items) implements MarkdownParagraph {
    }

    record UnOrderedList(List<MarkdownParagraph.Text> items) implements MarkdownParagraph {
    }

    record Heading(int level, List<MarkdownText> text) implements MarkdownParagraph {
    }

    record Text(List<MarkdownText> text) implements MarkdownParagraph {
    }
}
