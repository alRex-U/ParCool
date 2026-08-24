package com.alrex.parcool.client.md.ui;

import com.alrex.parcool.client.gui.GuiRenderUtil;
import com.alrex.parcool.client.md.CompiledMarkdown;
import com.alrex.parcool.client.md.MarkdownParagraph;
import com.alrex.parcool.client.md.MarkdownText;
import com.alrex.parcool.client.textures.ParCoolGuiTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolGuideImageTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class MarkdownWidget extends AbstractWidget {
    private final CompiledMarkdown content;
    private final List<AbstractWidget> contentRendererWidgets;
    private final Font font;
    private final Style style;
    private final int contentHeight;
    private final float maxScrollY;
    @Nullable
    private final Consumer<ResourceLocation> openLinkListener;
    @Nullable
    private final Consumer<String> openURLListener;

    private float scrollY = 0;

    public MarkdownWidget(Font font, int x, int y, int width, int height, CompiledMarkdown markdown, int color, @Nullable Consumer<ResourceLocation> openLinkListener, @Nullable Consumer<String> openURLListener) {
        super(x, y, width, height, Component.empty());
        this.content = markdown;
        this.font = font;
        this.style = Style.EMPTY.withColor(color);
        this.openLinkListener = openLinkListener;
        this.openURLListener = openURLListener;
        int currentY = 4;
        this.contentRendererWidgets = new ArrayList<>();
        for (var paragraph : markdown.components()) {
            if (paragraph instanceof MarkdownParagraph.Text text) {
                contentRendererWidgets.add(new TextWidget(0, currentY, width, text.text(), style));
            } else if (paragraph instanceof MarkdownParagraph.HorizontalLine horizontalLine) {
                contentRendererWidgets.add(new HorizontalLineWidget(0, currentY, width, horizontalLine));
            } else if (paragraph instanceof MarkdownParagraph.Heading heading) {
                contentRendererWidgets.add(new HeadingWidget(0, currentY, width, heading));
            } else if (paragraph instanceof MarkdownParagraph.UnOrderedList list) {
                contentRendererWidgets.add(new UnorderedListWidget(0, currentY, width, list));
            } else if (paragraph instanceof MarkdownParagraph.OrderedList list) {
                contentRendererWidgets.add(new OrderedListWidget(0, currentY, width, list));
            } else if (paragraph instanceof MarkdownParagraph.Image image) {
                contentRendererWidgets.add(new ImageWidget(25, currentY, width - 50, image));
            } else if (paragraph instanceof MarkdownParagraph.ExtensionMCRecipe recipe) {
                contentRendererWidgets.add(new RecipeWidget(25, currentY, width - 50, recipe));
            } else if (paragraph instanceof MarkdownParagraph.ExtensionMCBrewingRecipe recipe) {
                contentRendererWidgets.add(new BrewingRecipeWidget(20, currentY, width - 40, recipe));
            }
            if (!contentRendererWidgets.isEmpty())
                currentY += contentRendererWidgets.get(contentRendererWidgets.size() - 1).getHeight() + font.lineHeight;
        }
        contentHeight = currentY;
        maxScrollY = contentHeight - height / 2f;
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        var poseStack = graphics.pose();
        poseStack.pushPose();
        {
            GuiRenderUtil.enableScissorTestInGuiCoordinate(getX(), getY(), width, height);

            var mouseXInContent = mouseX - getX();
            if (mouseXInContent < 0 || width < mouseXInContent) mouseXInContent = -1;
            var mouseYInContent = mouseY - getY();
            if (mouseYInContent < 0 || width < mouseYInContent) mouseYInContent = -1;
            else mouseYInContent += (int) scrollY;

            poseStack.translate(getX(), getY() - scrollY, 0);
            for (var widget : contentRendererWidgets) {
                widget.render(graphics, mouseXInContent, mouseYInContent, partial);
            }
            RenderSystem.disableScissor();
        }
        poseStack.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int click) {
        for (var widget : contentRendererWidgets) {
            if (widget.mouseClicked(mouseX - getX(), mouseY - getY() + scrollY, click)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollDelta) {
        this.scrollY -= (float) (scrollDelta * 16);
        if (scrollY > maxScrollY) scrollY = maxScrollY;
        if (scrollY < 0) scrollY = 0;
        return true;
    }

    private int textColor() {
        var color = style.getColor();
        return color != null ? color.getValue() | 0xFF000000 : ~0;
    }

    private static class ComponentWidget<T extends MarkdownParagraph> extends AbstractWidget {
        protected final List<TextWidget> textWidgets = new ArrayList<>();
        protected T content;

        public ComponentWidget(int x, int y, int width, int height, T content) {
            super(x, y, width, height, Component.empty());
            this.content = content;
        }

        @Override
        public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int click) {
            for (var textWidget : textWidgets) {
                if (textWidget.getX() < mouseX && mouseX < textWidget.getX() + textWidget.getWidth() && textWidget.getY() < mouseY && mouseY < textWidget.getY() + textWidget.getHeight()) {
                    if (textWidget.mouseClicked(mouseX, mouseY, click)) return true;
                }
            }
            return false;
        }

        @Override
        public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
            if (isActive()) {
                for (var widget : textWidgets) {
                    widget.render(graphics, mouseX, mouseY, partial);
                }
            }
        }
    }

    private class TextWidget extends AbstractWidget {
        private record InteractiveZone(int idx, int x, int y, int width, int height) {
        }

        private record TextFragment(Component text, float x, float y, float width) {
        }

        private List<MarkdownText> interactiveText;
        private List<TextFragment> fragments;
        private List<Tuple<TextFragment, InteractiveZone>> interactiveFragments;
        @Nullable
        private InteractiveZone interactingZone;

        public TextWidget(int x, int y, int width, List<MarkdownText> content, Style style) {
            super(x, y, width, 0, Component.empty());
            var context = new FormattingContext(width);
            interactiveText = new ArrayList<>();
            fragments = new ArrayList<>();
            interactiveFragments = new ArrayList<>();
            format(font, context, content.iterator(), fragments, interactiveFragments, interactiveText, style);
            interactiveText = Collections.unmodifiableList(interactiveText);
            fragments = Collections.unmodifiableList(fragments);
            interactiveFragments = Collections.unmodifiableList(interactiveFragments);
            setHeight((int) (fragments.stream().map(it -> it.y).max(Float::compareTo).orElse(0f) + font.lineHeight));
        }

        private static class FormattingContext {
            FormattingContext(int width) {
                this.width = width;
            }

            private final int width;
            private float currentX;
            private float currentY;
        }

        private static void format(
                Font font,
                FormattingContext context,
                Iterator<MarkdownText> textIterator,
                List<TextFragment> result,
                List<Tuple<TextFragment, InteractiveZone>> interactions,
                List<MarkdownText> interactiveText,
                Style style
        ) {
            while (textIterator.hasNext()) {
                var text = textIterator.next();
                if (text instanceof MarkdownText.LineBreak) {
                    context.currentX = 0;
                    context.currentY += font.lineHeight;
                } else if (text instanceof MarkdownText.Strong strong) {
                    format(font, context, strong.child().iterator(), result, interactions, interactiveText, style.withBold(true));
                } else if (text instanceof MarkdownText.Emphasis emphasis) {
                    format(font, context, emphasis.child().iterator(), result, interactions, interactiveText, style.withItalic(true));
                } else if (text instanceof MarkdownText.ExternalLink || text instanceof MarkdownText.Link) {
                    String str;
                    if (text instanceof MarkdownText.ExternalLink externalLink) str = externalLink.text();
                    else if (text instanceof MarkdownText.Link link) str = link.text();
                    else continue;
                    var splitter = font.getSplitter();
                    var idx = interactiveText.size();
                    interactiveText.add(text);
                    while (!str.isEmpty()) {
                        int splitPos = splitter.findLineBreak(str, (int) (context.width - context.currentX), style);
                        var thisLine = str.substring(0, splitPos);
                        var component = Component.literal(thisLine).withStyle(style.withColor((TextColor) null));
                        var fragment = new TextFragment(component, context.currentX, context.currentY, font.width(component));
                        interactions.add(new Tuple<>(fragment, new InteractiveZone(idx, (int) fragment.x, (int) fragment.y, Mth.ceil(fragment.width), font.lineHeight)));
                        context.currentX += fragment.width;
                        if (str.length() <= splitPos) {
                            break;
                        } else {
                            context.currentX = 0;
                            context.currentY += font.lineHeight;
                            str = splitPos < str.length() - 1 && str.charAt(splitPos) == ' '
                                    ? str.substring(splitPos + 1)
                                    : str.substring(splitPos);
                        }
                    }
                } else {
                    var splitter = font.getSplitter();
                    String str = "";
                    var textStyle = style;
                    if (text instanceof MarkdownText.Text normalText) {
                        str = normalText.text();
                    } else if (text instanceof MarkdownText.ExtensionMCTranslatable translatable) {
                        str = I18n.get(translatable.translationKey());
                        textStyle = style.withItalic(true);
                    } else if (text instanceof MarkdownText.ExtensionMCKey key) {
                        var keyBind = Arrays.stream(Minecraft.getInstance().options.keyMappings)
                                .filter(keyMapping -> keyMapping.getName().equals(key.keyBindName()))
                                .findAny();
                        if (keyBind.isPresent()) {
                            str = keyBind.get().getTranslatedKeyMessage().getString();
                        }
                        textStyle = style.withItalic(true);
                    } else if (text instanceof MarkdownText.ExtensionMCItemName itemName) {
                        var item = BuiltInRegistries.ITEM.get(itemName.id());
                        str = I18n.get(item.getDescriptionId());
                        textStyle = style.withItalic(true);
                    }
                    while (!str.isEmpty()) {
                        int splitPos = splitter.findLineBreak(str, (int) (context.width - context.currentX), textStyle);
                        var thisLine = str.substring(0, splitPos);
                        var component = Component.literal(thisLine).withStyle(textStyle);
                        var fragment = new TextFragment(component, context.currentX, context.currentY, font.width(component));
                        result.add(fragment);
                        context.currentX += fragment.width;
                        if (str.length() <= splitPos) {
                            break;
                        } else {
                            context.currentX = 0;
                            context.currentY += font.lineHeight;
                            str = splitPos < str.length() - 1 && str.charAt(splitPos) == ' '
                                    ? str.substring(splitPos + 1)
                                    : str.substring(splitPos);
                        }
                    }
                }
            }
        }

        @Nullable
        private MarkdownText getCurrentInteraction() {
            return interactingZone != null ? interactiveText.get(interactingZone.idx) : null;
        }

        @Override
        public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
            int relativeMouseX = mouseX - getX();
            int relativeMouseY = mouseY - getY();
            interactingZone = null;
            for (var interaction : interactiveFragments) {
                var zone = interaction.getB();
                if (zone.x <= relativeMouseX && relativeMouseX < zone.x + zone.width &&
                        zone.y <= relativeMouseY && relativeMouseY < zone.y + zone.height)
                    interactingZone = zone;
            }
            for (var fragment : fragments) {
                graphics.drawString(font, fragment.text, (int) (getX() + fragment.x), (int) (getY() + fragment.y), ~0, false);
            }
            for (var interaction : interactiveFragments) {
                var fragment = interaction.getA();
                var isHovered = interactingZone != null && interactingZone.idx == interaction.getB().idx;
                graphics.drawString(
                        font, fragment.text, (int) (getX() + fragment.x), (int) (getY() + fragment.y),
                        isHovered ? 0xFFFC9527 : 0xFF6C76FA, false
                );
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int click) {
            if (interactingZone == null) return false;
            var zone = interactingZone;
            var relativeMouseX = mouseX - getX();
            var relativeMouseY = mouseY - getY();
            if (zone.x <= relativeMouseX && relativeMouseX < zone.x + zone.width &&
                    zone.y <= relativeMouseY && relativeMouseY < zone.y + zone.height) {
                var clickedItem = interactiveText.get(interactingZone.idx);
                if (clickedItem instanceof MarkdownText.ExternalLink externalLink) {
                    if (openURLListener != null) openURLListener.accept(externalLink.url());
                } else if (clickedItem instanceof MarkdownText.Link link) {
                    if (openLinkListener != null) openLinkListener.accept(link.location());
                }
            }
            return false;
        }

        @Override
        public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
        }
    }

    private class HeadingWidget extends ComponentWidget<MarkdownParagraph.Heading> {
        private final TextWidget widget;
        private final float scale;
        private final boolean bottomLine;
        public HeadingWidget(int x, int y, int width, MarkdownParagraph.Heading content) {
            super(x, y, width, 0, content);
            scale = Mth.map(Mth.clamp(content.level(), 1, 6), 1, 6, 1.3f, 1.0f);
            widget = new TextWidget(1, 0, (int) (width / scale), content.text(), style.withBold(true));
            setHeight(Mth.ceil(widget.getHeight() * scale) + 1);
            bottomLine = content.level() <= 2;
        }

        @Override
        public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
            var poseStack = graphics.pose();
            poseStack.pushPose();
            {
                poseStack.translate(getX(), getY(), 0);
                poseStack.scale(scale, scale, 0);
                widget.render(graphics, (int) ((mouseX - getX()) / scale), (int) ((mouseY - getY()) / scale), partial);
            }
            poseStack.popPose();
            if (bottomLine) {
                RenderSystem.setShaderColor(1f, 1f, 1f, 0.4f);
                graphics.hLine(getX(), getX() + width, getY() + Mth.ceil(widget.getHeight() * scale), textColor());
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            }
        }
    }

    private class HorizontalLineWidget extends ComponentWidget<MarkdownParagraph.HorizontalLine> {
        public HorizontalLineWidget(int x, int y, int width, MarkdownParagraph.HorizontalLine content) {
            super(x, y, width, 3, content);
        }

        @Override
        public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
            RenderSystem.setShaderColor(1, 1, 1, 0.5f);
            graphics.hLine(getX() + 5, getX() + width - 5, getY() + 1, textColor());
            RenderSystem.setShaderColor(1, 1, 1, 1f);
        }
    }

    private class OrderedListWidget extends ComponentWidget<MarkdownParagraph.OrderedList> {

        public OrderedListWidget(int x, int y, int width, MarkdownParagraph.OrderedList content) {
            super(x, y, width, 0, content);
            int currentY = 0;
            var offset = font.lineHeight;
            for (var item : content.items()) {
                var widget = new TextWidget(x + offset, y + currentY, width, item.text(), style);
                textWidgets.add(widget);
                currentY += widget.getHeight();
            }
            if (!textWidgets.isEmpty()) {
                setHeight(currentY);
            }
        }

        @Override
        public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
            int i = 0;
            for (var widget : textWidgets) {
                graphics.drawString(font, (++i) + ".", getX(), widget.getY(), textColor(), false);
            }
            super.renderWidget(graphics, mouseX, mouseY, partial);
        }
    }

    private class UnorderedListWidget extends ComponentWidget<MarkdownParagraph.UnOrderedList> {
        public UnorderedListWidget(int x, int y, int width, MarkdownParagraph.UnOrderedList content) {
            super(x, y, width, 0, content);
            int currentY = 0;
            var offset = font.lineHeight;
            for (var item : content.items()) {
                var widget = new TextWidget(x + offset, y + currentY, width - offset, item.text(), style);
                textWidgets.add(widget);
                currentY += widget.getHeight();
            }
            if (!textWidgets.isEmpty()) {
                setHeight(currentY);
            }
        }

        @Override
        public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
            var offset = font.lineHeight / 2;
            for (var widget : textWidgets) {
                graphics.fill(getX() + offset - 1, widget.getY() + offset - 1, getX() + offset + 1, widget.getY() + offset + 1, textColor());
            }
            super.renderWidget(graphics, mouseX, mouseY, partial);
        }
    }

    private static class ImageWidget extends ComponentWidget<MarkdownParagraph.Image> {
        private final TextureAtlasSprite sprite;
        public ImageWidget(int x, int y, int width, MarkdownParagraph.Image content) {
            super(0, y, 0, 0, content);
            sprite = ParCoolTextures.guideResourceSprite(content.spriteLocation());
            var contents = sprite.contents();
            this.setX(x + (width - Math.min(width, contents.width())) / 2);
            setWidth(Math.min(width, contents.width()));
            setHeight(getWidth() * contents.height() / contents.width());
        }

        @Override
        public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
            RenderSystem.setShaderTexture(0, ParCoolGuideImageTextureAtlas.TEXTURE_LOCATION);
            graphics.blit(getX(), getY(), 0, width, height, sprite);
        }
    }

    private class RecipeWidget extends ComponentWidget<MarkdownParagraph.ExtensionMCRecipe> {
        private static final ResourceLocation CRAFTING_TABLE_LOCATION = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/crafting_table.png");
        @Nullable
        private final RecipeHolder<?> recipe;
        private final float scale;
        private final ItemRenderer itemRenderer;
        private final RegistryAccess registryAccess;

        public RecipeWidget(int x, int y, int width, MarkdownParagraph.ExtensionMCRecipe content) {
            super(x, y, width, 0, content);
            var connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                var manager = connection.getRecipeManager();
                var recipe = manager.byKey(content.recipeId());
                this.recipe = recipe.orElse(null);
            } else {
                this.recipe = null;
            }
            itemRenderer = Minecraft.getInstance().getItemRenderer();
            scale = width / 118f;
            setHeight((int) (scale * 56f));
            registryAccess = new RegistryAccess.ImmutableRegistryAccess(Collections.emptyList());
        }

        @Override
        public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
            var poseStack = graphics.pose();
            poseStack.pushPose();
            {
                poseStack.translate(getX(), getY(), 0);
                poseStack.scale(scale, scale, 1);
                graphics.blit(CRAFTING_TABLE_LOCATION, 0, 0, 28, 15, 118, 56);
            }
            poseStack.popPose();
            var time = Util.getMillis() / 1000;
            if (recipe != null) {
                var recipeValue = recipe.value();
                if (recipeValue instanceof ShapelessRecipe || recipeValue instanceof ShapedRecipe) {
                    var craftWidth = 3;
                    NonNullList<Ingredient> ingredients;
                    ItemStack result;

                    if (recipeValue instanceof ShapelessRecipe craftingRecipe) {
                        ingredients = craftingRecipe.getIngredients();
                        result = craftingRecipe.getResultItem(registryAccess);
                    } else if (recipeValue instanceof ShapedRecipe craftingRecipe) {
                        ingredients = craftingRecipe.getIngredients();
                        result = craftingRecipe.getResultItem(registryAccess);
                    } else return;
                    int i = -1;
                    for (var ingredient : ingredients) {
                        i++;
                        var items = ingredient.getItems();
                        if (items.length == 0) continue;
                        var item = items[(int) (time % items.length)];
                        var column = i % craftWidth;
                        var row = i / craftWidth;
                        GuiRenderUtil.renderScaledGuiItem(graphics, item, getX(), getY(), 2 + 18 * column, 2 + 18 * row, scale);
                    }
                    GuiRenderUtil.renderScaledGuiItem(graphics, result, getX(), getY(), 96, 20, scale);
                }
            }
        }
    }

    private class BrewingRecipeWidget extends ComponentWidget<MarkdownParagraph.ExtensionMCBrewingRecipe> {
        private final float scale;
        private final ItemRenderer itemRenderer;
        private List<PotionMix> recipes;

        private record PotionMix(ItemStack input, ItemStack ingredient, ItemStack output) {
        }

        public BrewingRecipeWidget(int x, int y, int width, MarkdownParagraph.ExtensionMCBrewingRecipe content) {
            super(x, y, width, 0, content);
            itemRenderer = Minecraft.getInstance().getItemRenderer();
            scale = width / 127f;
            setHeight((int) (scale * 20f));
            recipes = new ArrayList<>();
            var outputPotion = BuiltInRegistries.POTION.get(content.potionId());
            if (outputPotion == null) return;
            var connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                for (var brewingRecipe : connection.potionBrewing().potionMixes) {
                    if (outputPotion == brewingRecipe.to().value()) {
                        var input = new ItemStack(Items.POTION);
                        input.set(DataComponents.POTION_CONTENTS, new PotionContents(brewingRecipe.from()));
                        var output = new ItemStack(Items.POTION);
                        output.set(DataComponents.POTION_CONTENTS, new PotionContents(brewingRecipe.to()));
                        for (var ingredient : brewingRecipe.ingredient().getItems()) {
                            recipes.add(new PotionMix(input, ingredient, output));
                        }
                    }
                }
            }
            ((ArrayList<?>) recipes).trimToSize();
        }

        @Override
        public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
            var poseStack = graphics.pose();
            RenderSystem.setShaderTexture(0, ParCoolGuiTextureAtlas.TEXTURE_LOCATION);
            poseStack.pushPose();
            {
                poseStack.translate(getX(), getY(), 0);
                poseStack.scale(scale, scale, 1);
                graphics.blit(0, 0, 0, 127, 20, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BREWING_RECIPE_BOX));
            }
            poseStack.popPose();
            var time = Util.getMillis() / 1000;
            if (recipes.isEmpty()) return;
            int idx = (int) (time % recipes.size());
            var mix = recipes.get(idx);
            GuiRenderUtil.renderScaledGuiItem(graphics, mix.input, getX(), getY(), 2, 2, scale);
            GuiRenderUtil.renderScaledGuiItem(graphics, mix.ingredient, getX(), getY(), 51, 2, scale);
            GuiRenderUtil.renderScaledGuiItem(graphics, mix.output, getX(), getY(), 109, 2, scale);
        }
    }
}
