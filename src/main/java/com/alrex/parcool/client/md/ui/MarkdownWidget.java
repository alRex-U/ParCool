package com.alrex.parcool.client.md.ui;

import com.alrex.parcool.client.gui.GuiRenderUtil;
import com.alrex.parcool.client.md.CompiledMarkdown;
import com.alrex.parcool.client.md.MarkdownParagraph;
import com.alrex.parcool.client.md.MarkdownText;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
                contentRendererWidgets.add(new ImageWidget(10, currentY, width - 20, image));
            } else if (paragraph instanceof MarkdownParagraph.ExtensionMCRecipe recipe) {
                contentRendererWidgets.add(new RecipeWidget(10, currentY, width - 20, recipe));
            }
            if (!contentRendererWidgets.isEmpty())
                currentY += contentRendererWidgets.get(contentRendererWidgets.size() - 1).getHeight() + font.lineHeight;
        }
        contentHeight = currentY;
        maxScrollY = contentHeight - height / 2f;
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        poseStack.pushPose();
        {
            GuiRenderUtil.enableScissorTestInGuiCoordinate(x, y, width, height);

            var mouseXInContent = mouseX - x;
            if (mouseXInContent < 0 || width < mouseXInContent) mouseXInContent = -1;
            var mouseYInContent = mouseY - y;
            if (mouseYInContent < 0 || width < mouseYInContent) mouseYInContent = -1;
            else mouseYInContent += (int) scrollY;

            poseStack.translate(x, y - scrollY, 0);
            for (var widget : contentRendererWidgets) {
                widget.render(poseStack, mouseXInContent, mouseYInContent, partial);
            }
            RenderSystem.disableScissor();
        }
        poseStack.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int click) {
        for (var widget : contentRendererWidgets) {
            if (widget.mouseClicked(mouseX - x, mouseY - y + scrollY, click)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        this.scrollY -= (float) (scrollDelta * 8);
        if (scrollY > maxScrollY) scrollY = maxScrollY;
        if (scrollY < 0) scrollY = 0;
        return true;
    }

    private int textColor() {
        var color = style.getColor();
        return color != null ? color.getValue() : ~0;
    }

    private static class ComponentWidget<T extends MarkdownParagraph> extends AbstractWidget {
        protected final List<TextWidget> textWidgets = new ArrayList<>();
        protected T content;

        public ComponentWidget(int x, int y, int width, int height, T content) {
            super(x, y, width, height, Component.empty());
            this.content = content;
        }

        @Override
        public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int click) {
            for (var textWidget : textWidgets) {
                if (textWidget.x < mouseX && mouseX < textWidget.x + textWidget.getWidth() && textWidget.y < mouseY && mouseY < textWidget.y + textWidget.getHeight()) {
                    if (textWidget.mouseClicked(mouseX, mouseY, click)) return true;
                }
            }
            return false;
        }

        @Override
        public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
            if (isActive()) {
                for (var widget : textWidgets) {
                    widget.render(poseStack, mouseX, mouseY, partial);
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
                        var item = Registry.ITEM.get(itemName.id());
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
        public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
            int relativeMouseX = mouseX - x;
            int relativeMouseY = mouseY - y;
            interactingZone = null;
            for (var interaction : interactiveFragments) {
                var zone = interaction.getB();
                if (zone.x <= relativeMouseX && relativeMouseX < zone.x + zone.width &&
                        zone.y <= relativeMouseY && relativeMouseY < zone.y + zone.height)
                    interactingZone = zone;
            }
            for (var fragment : fragments) {
                font.draw(poseStack, fragment.text, x + fragment.x, y + fragment.y, ~0);
            }
            for (var interaction : interactiveFragments) {
                var fragment = interaction.getA();
                var isHovered = interactingZone != null && interactingZone.idx == interaction.getB().idx;
                font.draw(
                        poseStack, fragment.text, x + fragment.x, y + fragment.y,
                        isHovered ? 0xFFFC9527 : 0xFF6C76FA
                );
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int click) {
            if (interactingZone == null) return false;
            var zone = interactingZone;
            var relativeMouseX = mouseX - x;
            var relativeMouseY = mouseY - y;
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
        public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
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
        public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
            poseStack.pushPose();
            {
                poseStack.translate(x, y, 0);
                poseStack.scale(scale, scale, 0);
                widget.render(poseStack, (int) ((mouseX - x) / scale), (int) ((mouseY - y) / scale), partial);
            }
            poseStack.popPose();
            if (bottomLine) {
                RenderSystem.setShaderColor(1f, 1f, 1f, 0.4f);
                hLine(poseStack, x, x + width, y + Mth.ceil(widget.getHeight() * scale), textColor());
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            }
        }
    }

    private class HorizontalLineWidget extends ComponentWidget<MarkdownParagraph.HorizontalLine> {
        public HorizontalLineWidget(int x, int y, int width, MarkdownParagraph.HorizontalLine content) {
            super(x, y, width, 3, content);
        }

        @Override
        public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
            RenderSystem.setShaderColor(1, 1, 1, 0.5f);
            hLine(poseStack, x + 5, x + width - 5, y + 1, textColor());
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
        public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
            int i = 0;
            for (var widget : textWidgets) {
                font.draw(poseStack, (++i) + ".", x, widget.y, textColor());
            }
            super.render(poseStack, mouseX, mouseY, partial);
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
        public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
            var offset = font.lineHeight / 2;
            for (var widget : textWidgets) {
                fill(poseStack, x + offset - 1, widget.y + offset - 1, x + offset + 1, widget.y + offset + 1, textColor());
            }
            super.render(poseStack, mouseX, mouseY, partial);
        }
    }

    private static class ImageWidget extends ComponentWidget<MarkdownParagraph.Image> {
        public ImageWidget(int x, int y, int width, MarkdownParagraph.Image content) {
            super(x + (width - Math.min(width, content.texAreaWidth())) / 2, y,
                    Math.min(width, content.texAreaWidth()),
                    Math.min(width, content.texAreaWidth()) * content.texAreaHeight() / content.texAreaWidth(),
                    content
            );
        }

        @Override
        public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
            RenderSystem.setShaderTexture(0, content.textureLocation());
            blit(poseStack,
                    x, y, width, height,
                    content.texX(), content.texY(),
                    content.texAreaWidth(), content.texAreaHeight(),
                    content.texWidth(), content.texHeight()
            );
        }
    }

    private class RecipeWidget extends ComponentWidget<MarkdownParagraph.ExtensionMCRecipe> {
        private static final ResourceLocation CRAFTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/crafting_table.png");
        @Nullable
        private final Recipe<?> recipe;
        private final float scale;
        private final ItemRenderer itemRenderer;

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
        }

        @Override
        public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
            RenderSystem.setShaderTexture(0, CRAFTING_TABLE_LOCATION);
            var modelViewStack = RenderSystem.getModelViewStack();
            poseStack.pushPose();
            {
                poseStack.translate(x, y, 0);
                poseStack.scale(scale, scale, 1);
                blit(poseStack, 0, 0, 28, 15, 118, 56);
            }
            poseStack.popPose();
            modelViewStack.pushPose();
            modelViewStack.translate(MarkdownWidget.this.x + x, MarkdownWidget.this.y - scrollY + y, 0);
            if (recipe instanceof ShapedRecipe craftingRecipe) {
                int i = -1;
                for (var ingredient : craftingRecipe.getIngredients()) {
                    i++;
                    var items = ingredient.getItems();
                    if (items.length == 0) continue;
                    var item = items[Mth.floor(partial / 20f) % items.length];
                    var column = i % craftingRecipe.getWidth();
                    var row = i / craftingRecipe.getWidth();
                    modelViewStack.pushPose();
                    {
                        modelViewStack.scale(scale, scale, 1);
                        itemRenderer.renderGuiItem(item, 2 + 18 * column, 2 + 18 * row);
                    }
                    modelViewStack.popPose();
                }
                var result = craftingRecipe.getResultItem();
                modelViewStack.pushPose();
                {
                    modelViewStack.scale(scale, scale, 1);
                    itemRenderer.renderGuiItem(result, 96, 20);
                }
                modelViewStack.popPose();
            }
            modelViewStack.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }
}
