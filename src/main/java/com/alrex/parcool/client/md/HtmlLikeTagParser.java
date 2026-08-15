package com.alrex.parcool.client.md;

import net.minecraft.util.Tuple;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.TreeMap;

@OnlyIn(Dist.CLIENT)
public class HtmlLikeTagParser {
    /// Only supporting self-closing tag
    @Nullable
    public static Tuple<HtmlLikeTag, Integer> parse(String tagString, int startIdx) {
        var length = tagString.length();
        if (length < 3) return null;
        int i = startIdx;
        i = skipChars(tagString, i, ' ');
        if (i == -1 || tagString.charAt(i) != '<') return null;
        var tagName = Word.find(tagString, i + 1);
        if (tagName == null) return null;
        var attrs = new TreeMap<String, String>();
        i = tagName.endIndex;
        for (; i < tagString.length(); i++) {
            var attrName = Word.find(tagString, i);
            if (attrName == null) break;
            i = skipChars(tagString, attrName.endIndex, ' ');
            if (i == -1) return null;
            if (tagString.charAt(i) != '=') break;
            var quoted = Word.findQuoted(tagString, ++i);
            if (quoted == null) break;
            attrs.put(attrName.word, quoted.word);
            i = quoted.endIndex - 1;
        }
        if (i >= tagString.length()) return null; // not closed
        i = skipChars(tagString, i, ' ');
        if (i == -1 || i >= tagString.length() - 1) return null;
        if (tagString.charAt(i) != '/' && tagString.charAt(i + 1) != '>') return null;

        return new Tuple<>(new HtmlLikeTag(tagName.word, attrs), i + 1);
    }

    private static int skipChars(String str, int current, char skipped) {
        for (int i = current; i < str.length(); i++) {
            if (str.charAt(i) != skipped) return i;
        }
        return -1;
    }

    private static int findChars(String str, int current, char[] splitter) {
        for (int i = current; i < str.length(); i++) {
            for (char c : splitter) {
                if (str.charAt(i) == c) return i;
            }
        }
        return -1;
    }

    private record Word(String word, int endIndex) {
        private static final char[] TERMINATOR = new char[]{' ', '=', '/', '"', '>'};

        @Nullable
        private static Word find(String str, int start) {
            int wordStart = skipChars(str, start, ' ');
            if (wordStart == -1) return null;
            int wordEnd = findChars(str, wordStart, TERMINATOR);
            if (wordEnd == -1) return null;
            if (wordEnd <= wordStart) return null;
            return new Word(str.substring(wordStart, wordEnd), wordEnd);
        }

        private static Word findQuoted(String str, int start) {
            int wordStart = skipChars(str, start, ' ');
            if (wordStart == -1 || str.charAt(wordStart) != '"') return null;
            wordStart += 1;
            int wordEnd = str.indexOf('"', wordStart);
            if (wordEnd == -1) return null;
            if (wordEnd <= wordStart) return null;
            return new Word(str.substring(wordStart, wordEnd), wordEnd + 1);
        }
    }
}
