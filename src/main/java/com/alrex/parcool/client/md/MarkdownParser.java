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

            currentParagraph.addAll(toText(line));
            currentParagraph.add(LINE_BREAK);
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
            if (0 < i && i < line.length() - 2 && c == '.') {
                if (line.charAt(i + 1) == ' ') return new MarkdownParagraph.Text(toText(line.substring(i + 2)));
                return null;
            }
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
        return new MarkdownParagraph.Image(location, alt.isEmpty() ? null : alt);
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
                list.add(new MarkdownText.Text(builder.toString()));
                builder = new StringBuilder();
                if (match(line, i, "**")) {
                    var found = find(line, i + 1, "**");
                    if (found == -1) found = line.length();
                    if (i + 1 < line.length()) list.add(new MarkdownText.Strong(toText(line, i + 1, found)));
                    i = found + 2;
                } else {
                    var found = find(line, i + 1, "*");
                    if (found == -1) found = line.length();
                    if (i + 1 < line.length()) list.add(new MarkdownText.Emphasis(toText(line, i + 1, found)));
                    i = found + 1;
                }
            } else if (c == '_' && !match(line, i - 1, " _ ")) {
                list.add(new MarkdownText.Text(builder.toString()));
                builder = new StringBuilder();
                if (match(line, i, "__")) {
                    var found = find(line, i + 1, "__");
                    if (found == -1) found = line.length();
                    if (i + 1 < line.length()) list.add(new MarkdownText.Strong(toText(line, i + 1, found)));
                    i = found + 2;
                } else {
                    var found = find(line, i + 1, "_");
                    if (found == -1) found = line.length();
                    if (i + 1 < line.length())
                        list.add(new MarkdownText.Emphasis(toText(line, i + 1, found)));
                    i = found + 1;
                }
            } else if (c == '[') {
                int found = find(line, i + 1, "]");
                if (found != -1 && match(line, found + 1, "(")) {
                    var text = line.substring(i + 1, found);
                    var closeFound = find(line, found + 2, ")");
                    if (closeFound != -1) {
                        var location = line.substring(found + 2, closeFound);
                        if (location.startsWith("https://")) { // http(not https) is unavailable for users security
                            list.add(new MarkdownText.Text(builder.toString()));
                            builder = new StringBuilder();
                            list.add(new MarkdownText.ExternalLink(text, location));
                            i = closeFound;
                        } else {
                            var linkText = ResourceLocation.tryParse(location);
                            if (linkText != null) {
                                list.add(new MarkdownText.Text(builder.toString()));
                                builder = new StringBuilder();
                                list.add(new MarkdownText.Link(text, linkText));
                                i = closeFound;
                            }
                        }
                    }
                }
            } else builder.append(c);
        }
        list.add(new MarkdownText.Text(builder.toString()));
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
