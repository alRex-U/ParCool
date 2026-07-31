package com.alrex.parcool.client.md;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public interface MarkdownParagraph {
    record Image(ResourceLocation textureLocation, @Nullable String caption, int texX, int texY, int texAreaWidth,
                 int texAreaHeight, int texWidth, int texHeight) implements MarkdownParagraph {
        public static Image from(ResourceLocation textureLocation, @Nullable String caption) {
            return new Image(textureLocation, caption, 0, 0, 128, 128, 128, 128);
        }
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

    /// `<recipe id="parcool:parcool_guide"/>`
    record ExtensionMCRecipe(ResourceLocation recipeId) implements MarkdownParagraph {
    }
}
