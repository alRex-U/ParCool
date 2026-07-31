package com.alrex.parcool.client.md;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class MarkdownParser {
    private static final MarkdownParagraph.HorizontalLine HORIZONTAL_LINE = new MarkdownParagraph.HorizontalLine();
    private static final MarkdownText.LineBreak LINE_BREAK = new MarkdownText.LineBreak();

    public static CompiledMarkdown parse(BufferedReader reader) throws IOException {
        var paragraphs = new ArrayList<MarkdownParagraph>();
        var currentParagraph = new ArrayList<MarkdownText>();
        var strQueue = new ArrayDeque<String>();
        var listItems = new ArrayList<MarkdownParagraph.Text>();
        String line;
        while ((line = enqueueIfEmptyAndPoll(reader, strQueue)) != null) {
            if (isEmptyLine(line)) {
                if (!currentParagraph.isEmpty()) {
                    paragraphs.add(new MarkdownParagraph.Text(currentParagraph.stream().toList()));
                    currentParagraph.clear();
                }
                continue;
            }
            var heading = checkHeading(line);
            if (heading != null) {
                paragraphs.add(new MarkdownParagraph.Heading(heading.getA(), toText(heading.getB())));
                continue;
            }
            MarkdownParagraph listItem;

            while ((listItem = checkUnOrderedList(line)) != null) {
                if (listItem instanceof MarkdownParagraph.Text text) listItems.add(text);
                line = enqueueIfEmptyAndPoll(reader, strQueue);
                if (line == null) break;
            }
            if (!listItems.isEmpty()) {
                paragraphs.add(new MarkdownParagraph.UnOrderedList(listItems.stream().toList()));
                if (line != null) strQueue.add(line);
                listItems.clear();
                continue;
            }

            while ((listItem = checkOrderedList(line)) != null) {
                if (listItem instanceof MarkdownParagraph.Text text) listItems.add(text);
                line = enqueueIfEmptyAndPoll(reader, strQueue);
                if (line == null) break;
            }
            if (!listItems.isEmpty()) {
                paragraphs.add(new MarkdownParagraph.OrderedList(listItems.stream().toList()));
                if (line != null) strQueue.add(line);
                listItems.clear();
                continue;
            }

            if (checkHorizontalLine(line)) {
                paragraphs.add(HORIZONTAL_LINE);
                continue;
            }
            var image = checkImageParagraph(line);
            if (image != null) {
                paragraphs.add(image);
                continue;
            }
            var extensions = checkExtensionsParagraph(line);
            if (extensions != null) {
                paragraphs.add(extensions);
                continue;
            }

            currentParagraph.addAll(toText(line));
        }
        if (!currentParagraph.isEmpty()) {
            paragraphs.add(new MarkdownParagraph.Text(currentParagraph.stream().toList()));
        }

        return new CompiledMarkdown(paragraphs);
    }

    @Nullable
    private static String enqueueIfEmptyAndPoll(BufferedReader reader, Queue<String> queue) throws IOException {
        if (!queue.isEmpty()) return queue.poll();
        return reader.readLine();
    }

    private static boolean checkHorizontalLine(String line) {
        return line.codePoints().allMatch(c -> c == '-') || line.codePoints().allMatch(c -> c == '*') || line.codePoints().allMatch(c -> c == '_');
    }

    private static boolean isEmptyLine(String line) {
        if (line.isEmpty()) return true;
        return line.codePoints().allMatch(c -> c == ' ');
    }

    @Nullable
    private static Tuple<Integer, String> checkHeading(String line) {
        for (int i = 0; i < 7 && i < line.length(); i++) {
            var c = line.charAt(i);
            if (c == '#') continue;
            if (i > 0 && c == ' ') {
                return new Tuple<>(i, line.substring(i + 1));
            } else break;
        }
        return null;
    }

    @Nullable
    private static MarkdownParagraph checkUnOrderedList(String line) {
        if (line.startsWith("- ") || line.startsWith("* ") || line.startsWith("+ ")) {
            return new MarkdownParagraph.Text(toText(line.substring(2)));
        }
        return null;
    }

    @Nullable
    private static MarkdownParagraph checkOrderedList(String line) {
        for (int i = 0; i < line.length(); i++) {
            var c = line.charAt(i);
            if ('0' <= c && c <= '9') continue;
            if (c == '.') {
                if (0 < i && i < line.length() - 2) {
                    if (line.charAt(i + 1) == ' ') return new MarkdownParagraph.Text(toText(line.substring(i + 2)));
                    return null;
                }
            } else return null;
        }
        return null;
    }

    @Nullable
    private static MarkdownParagraph.Image checkImageParagraph(String line) {
        if (!line.startsWith("![")) return null;
        int found = find(line, 2, "](");
        if (found == -1) return null;
        var alt = line.substring(2, found);
        var closeFound = find(line, found + 2, ")");
        if (closeFound == -1) return null;
        var location = ResourceLocation.tryParse(line.substring(found + 2, closeFound));
        if (location == null) return null;
        return MarkdownParagraph.Image.from(location, alt.isEmpty() ? null : alt);
    }

    @Nullable
    private static MarkdownParagraph checkExtensionsParagraph(String line) {
        var tag = HtmlLikeTagParser.parse(line, 0);
        if (tag == null) return null;
        for (var i = tag.getB() + 1; i < line.length(); i++) {
            if (line.charAt(i) != ' ') return null;
        }
        var extTag = tag.getA();
        if (extTag.name().equals("recipe")) {
            var id = extTag.attributes().get("id");
            if (id == null) return null;
            var location = ResourceLocation.tryParse(id);
            if (location == null) return null;
            return new MarkdownParagraph.ExtensionMCRecipe(location);
        }
        return null;
    }

    private static List<MarkdownText> toText(String line) {
        return toText(line, 0, line.length());
    }

    private static List<MarkdownText> toText(String line, int start, int end) {
        var list = new ArrayList<MarkdownText>();
        var builder = new StringBuilder();
        for (int i = start; i < end && i < line.length(); i++) {
            var c = line.charAt(i);
            if (c == '*' && !match(line, i - 1, " * ")) {
                int skip = match(line, i, "**") ? 2 : 1;
                int found = find(line, i + 1, "*".repeat(skip));
                if (found == -1 || found >= end) {
                    builder.append("*".repeat(skip));
                    continue;
                }
                if (!builder.isEmpty()) {
                    list.add(new MarkdownText.Text(builder.toString()));
                    builder = new StringBuilder();
                }
                if (i + 1 < line.length()) list.add(skip == 1
                        ? new MarkdownText.Emphasis(toText(line, i + skip, found))
                        : new MarkdownText.Strong(toText(line, i + skip, found))
                );
                i = found + skip - 1;
            } else if (c == '_' && !match(line, i - 1, " _ ")) {
                int skip = match(line, i, "__") ? 2 : 1;
                int found = find(line, i + 1, "_".repeat(skip));
                if (found == -1 || found >= end) {
                    builder.append("_".repeat(skip));
                    continue;
                }
                if (!builder.isEmpty()) {
                    list.add(new MarkdownText.Text(builder.toString()));
                    builder = new StringBuilder();
                }
                if (i + 1 < line.length()) list.add(skip == 1
                        ? new MarkdownText.Emphasis(toText(line, i + 1, found))
                        : new MarkdownText.Strong(toText(line, i + 1, found))
                );
                i = found + skip - 1;
            } else if (c == '[') {
                int found = find(line, i + 1, "]");
                if ((found != -1 && found < end) && match(line, found + 1, "(")) {
                    var text = line.substring(i + 1, found);
                    var closeFound = find(line, found + 2, ")");
                    if (closeFound != -1) {
                        var location = line.substring(found + 2, closeFound);
                        if (location.startsWith("https://")) { // http(not https) is unavailable for users security
                            if (!builder.isEmpty()) {
                                list.add(new MarkdownText.Text(builder.toString()));
                                builder = new StringBuilder();
                            }
                            list.add(new MarkdownText.ExternalLink(text, location));
                            i = closeFound;
                        } else {
                            var linkText = ResourceLocation.tryParse(location);
                            if (linkText != null) {
                                if (!builder.isEmpty()) {
                                    list.add(new MarkdownText.Text(builder.toString()));
                                    builder = new StringBuilder();
                                }
                                list.add(new MarkdownText.Link(text, linkText));
                                i = closeFound;
                            }
                        }
                    }
                }
            } else if (c == '<') {
                var result = HtmlLikeTagParser.parse(line, i);
                if (result == null) {
                    builder.append(c);
                    continue;
                }
                i = result.getB();
                var tag = result.getA();
                if (!builder.isEmpty()) {
                    list.add(new MarkdownText.Text(builder.toString()));
                    builder = new StringBuilder();
                }
                switch (tag.name()) {
                    case "translation" -> {
                        var translationKey = tag.attributes().get("key");
                        if (translationKey != null) {
                            list.add(new MarkdownText.ExtensionMCTranslatable(translationKey));
                        }
                    }
                    case "key" -> {
                        var name = tag.attributes().get("name");
                        if (name != null) {
                            list.add(new MarkdownText.ExtensionMCKey(name));
                        }
                    }
                    case "item" -> {
                        var itemId = tag.attributes().get("id");
                        if (itemId != null) {
                            var location = ResourceLocation.tryParse(itemId);
                            if (location != null) list.add(new MarkdownText.ExtensionMCItemName(location));
                        }
                    }
                }
            } else builder.append(c);
        }
        if (start == 0 && end == line.length()) {
            int lastSpacesCount;
            for (lastSpacesCount = 0; lastSpacesCount < builder.length(); lastSpacesCount++) {
                if (builder.charAt(builder.length() - 1 - lastSpacesCount) != ' ') break;
            }
            String lastStr = builder.substring(0, builder.length() - lastSpacesCount);
            if (lastSpacesCount <= 1) {
                lastStr = lastStr + " ";
                list.add(new MarkdownText.Text(lastStr));
            } else {
                if (!lastStr.isEmpty()) list.add(new MarkdownText.Text(lastStr));
                list.add(LINE_BREAK);
            }
        } else {
            String lastStr = builder.toString();
            if (!lastStr.isEmpty()) list.add(new MarkdownText.Text(lastStr));
        }
        list.trimToSize();
        return Collections.unmodifiableList(list);
    }

    private static boolean match(String line, int start, String token) {
        boolean match = true;
        for (var j = 0; j < token.length(); j++) {
            var idx = start + j;
            if ((idx < 0 || line.length() <= idx) || line.charAt(start + j) != token.charAt(j)) {
                match = false;
                break;
            }
        }
        return match;
    }

    private static int find(String line, int start, String token) {
        for (int i = start; i < line.length(); i++) {
            if (match(line, i, token)) {
                return i;
            }
        }
        return -1;
    }
}
