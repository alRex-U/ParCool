package com.alrex.parcool.client.md;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

@Environment(EnvType.CLIENT)
public interface MarkdownParagraph {
    record Image(ResourceLocation spriteLocation, @Nullable String caption) implements MarkdownParagraph {
        public static Image from(ResourceLocation sprite, @Nullable String caption) {
            return new Image(sprite, caption);
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

    /// `<brewing-recipe input="minecraft:awkward_potion" ingredient="minecraft:chicken"/>`
    record ExtensionMCBrewingRecipe(ResourceLocation potionId) implements MarkdownParagraph {
    }
}
