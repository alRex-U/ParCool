package com.alrex.parcool.client.md.ui;

import com.alrex.parcool.client.md.CompiledMarkdown;
import com.alrex.parcool.client.md.MarkdownParagraph;
import com.alrex.parcool.client.md.MarkdownText;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MarkdownWidget extends AbstractWidget {
    private final CompiledMarkdown content;
    private final List<AbstractWidget> contentRendererWidgets;
    private final Font font;
    private final Style style;

    public MarkdownWidget(Font font, int x, int y, int width, int height, CompiledMarkdown markdown, int color) {
        super(x, y, width, height, Component.empty());
        this.content = markdown;
        this.font = font;
        this.style = Style.EMPTY.withColor(color);
        int currentY = 0;
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
            }
            if (!contentRendererWidgets.isEmpty())
                currentY += contentRendererWidgets.get(contentRendererWidgets.size() - 1).getHeight() + font.lineHeight;
        }
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }

    private static class ComponentWidget<T extends MarkdownParagraph> extends AbstractWidget {
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
            return false;
        }
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        poseStack.pushPose();
        {
            poseStack.translate(x, y, 0);
            for (var widget : contentRendererWidgets) {
                widget.render(poseStack, mouseX - x, mouseY - y, partial);
            }
        }
        poseStack.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int click) {
        for (var widget : contentRendererWidgets) {
            if (widget.mouseClicked(mouseX - x, mouseY - y, click)) return true;
        }
        return false;
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
                    context.currentY += font.lineHeight + 4;
                } else if (text instanceof MarkdownText.Text normalText) {
                    var splitter = font.getSplitter();
                    var str = normalText.text();
                    while (!str.isEmpty()) {
                        int splitPos = splitter.findLineBreak(str, (int) (context.width - context.currentX), style);
                        var thisLine = str.substring(0, splitPos);
                        var fragment = new TextFragment(Component.literal(thisLine).withStyle(style), context.currentX, context.currentY, font.width(thisLine));
                        result.add(fragment);
                        context.currentX += fragment.width;
                        if (str.length() <= splitPos) {
                            break;
                        } else {
                            context.currentX = 0;
                            context.currentY += font.lineHeight + 4;
                            str = str.substring(splitPos);
                        }
                    }
                } else if (text instanceof MarkdownText.Strong strong) {
                    format(font, context, strong.child().iterator(), result, interactions, interactiveText, style.withBold(true));
                } else if (text instanceof MarkdownText.Emphasis emphasis) {
                    format(font, context, emphasis.child().iterator(), result, interactions, interactiveText, style.withItalic(true));
                } else {
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
                        var fragment = new TextFragment(Component.literal(thisLine).withStyle(style.withColor((TextColor) null)), context.currentX, context.currentY, font.width(thisLine));
                        interactions.add(new Tuple<>(fragment, new InteractiveZone(idx, (int) fragment.x, (int) fragment.y, Mth.ceil(fragment.width), font.lineHeight)));
                        context.currentX += fragment.width;
                        if (str.length() <= splitPos) {
                            break;
                        } else {
                            context.currentX = 0;
                            context.currentY += font.lineHeight + 4;
                            str = str.substring(splitPos);
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
            poseStack.pushPose();
            {
                RenderSystem.enableDepthTest();
                RenderSystem.depthFunc(GL11.GL_LEQUAL);
                RenderSystem.colorMask(false, false, false, false);
                fill(poseStack, x, y, x + width, y + height, ~0);
                RenderSystem.depthFunc(GL11.GL_GEQUAL);
                RenderSystem.colorMask(true, true, true, true);
            }
            poseStack.popPose();

            poseStack.pushPose();
            {
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
            poseStack.popPose();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
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

        public HeadingWidget(int x, int y, int width, MarkdownParagraph.Heading content) {
            super(x, y, width, 0, content);
            scale = Mth.map(Mth.clamp(content.level(), 1, 6), 1, 6, 2, 1.1f);
            widget = new TextWidget(0, 0, (int) (width / scale), content.text(), style.withBold(true));
            setHeight((int) (widget.getHeight() * scale));
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
        }
    }

    private static class HorizontalLineWidget extends ComponentWidget<MarkdownParagraph.HorizontalLine> {
        public HorizontalLineWidget(int x, int y, int width, MarkdownParagraph.HorizontalLine content) {
            super(x, y, width, 10, content);
        }

        @Override
        public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
            hLine(poseStack, x + 5, x + width - 5, y + height / 2 - 1, ~0);
        }
    }

    private class OrderedListWidget extends ComponentWidget<MarkdownParagraph.OrderedList> {
        private final ArrayList<TextWidget> widgets;

        public OrderedListWidget(int x, int y, int width, MarkdownParagraph.OrderedList content) {
            super(x, y, width, 0, content);
            int currentY = 0;
            var offset = font.lineHeight * 2;
            widgets = new ArrayList<>();
            for (var item : content.items()) {
                var widget = new TextWidget(x + offset, y + currentY, width, item.text(), style);
                widgets.add(widget);
                currentY += widget.getHeight() + 4;
            }
            if (!widgets.isEmpty()) {
                setHeight(currentY - 4);
            }
        }

        @Override
        public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
            int i = 0;
            for (var widget : widgets) {
                font.draw(poseStack, (++i) + ".", x, widget.y, ~0);
                widget.render(poseStack, mouseX, mouseY, partial);
            }
        }
    }

    private class UnorderedListWidget extends ComponentWidget<MarkdownParagraph.UnOrderedList> {
        private final ArrayList<TextWidget> widgets;

        public UnorderedListWidget(int x, int y, int width, MarkdownParagraph.UnOrderedList content) {
            super(x, y, width, 0, content);
            int currentY = 0;
            widgets = new ArrayList<>();
            var offset = font.lineHeight * 2;
            for (var item : content.items()) {
                var widget = new TextWidget(x + offset, y + currentY, width - offset, item.text(), style);
                widgets.add(widget);
                currentY += widget.getHeight() + 4;
            }
            if (!widgets.isEmpty()) {
                setHeight(currentY - 4);
            }
        }

        @Override
        public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
            var offset = font.lineHeight / 2;
            for (var widget : widgets) {
                fill(poseStack, x + offset - 1, widget.y + offset - 1, x + offset + 1, widget.y + offset + 1, 0xFFFFFFFF);
                widget.render(poseStack, mouseX, mouseY, partial);
            }
        }
    }

    private static class ImageWidget extends ComponentWidget<MarkdownParagraph.Image> {
        public ImageWidget(int x, int y, int width, int height, MarkdownParagraph.Image content) {
            super(x, y, width, height, content);
        }
    }
}
