package com.researchmate;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic text processing: split a text into words, remove stop words and count the words.
 * Steps: lower-case -> keep letters only -> remove short words -> remove stop words -> count.
 */
public class TextAnalyzer {

    /** Very common words that say nothing about the topic (COLLECTIONS: a HashSet is fast to search). */
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "the", "and", "for", "are", "but", "not", "you", "all", "any", "can", "had", "her", "was", "one",
            "our", "out", "has", "his", "how", "its", "may", "new", "now", "see", "two", "who", "did", "get",
            "let", "she", "too", "use", "also", "been", "both", "each", "from", "have", "into", "more", "most",
            "much", "only", "other", "over", "same", "some", "such", "than", "that", "their", "them", "then",
            "there", "these", "they", "this", "those", "through", "very", "was", "were", "what", "when",
            "where", "which", "while", "will", "with", "would", "about", "after", "again", "being", "could",
            "does", "doing", "done", "first", "however", "should", "since", "using", "used", "based", "shown",
            "show", "shows", "paper", "papers", "figure", "table", "section", "et", "al", "etc", "http",
            "https", "www", "doi", "pdf", "arxiv", "our", "thus", "therefore", "because", "between", "among"));

    private static final Pattern LETTERS = Pattern.compile("\\p{L}+");

    /** All meaningful words of a text, in the order they appear (words can repeat). */
    public static List<String> words(String text) {
        List<String> result = new ArrayList<>();
        if (text == null) {
            return result;
        }
        // NFKC turns PDF ligatures such as "ﬁ" into "fi"
        String clean = Normalizer.normalize(text, Normalizer.Form.NFKC).toLowerCase();
        Matcher m = LETTERS.matcher(clean);
        while (m.find()) {
            String word = m.group();
            if (word.length() >= 3 && !STOP_WORDS.contains(word)) {
                result.add(word);
            }
        }
        return result;
    }

    /** word -> how many times it appears. */
    public static Map<String, Integer> frequencies(List<String> words) {
        Map<String, Integer> counts = new HashMap<>();
        for (String word : words) {
            counts.merge(word, 1, Integer::sum);
        }
        return counts;
    }

    /** The n most frequent words, most frequent first (same count: alphabetical). */
    public static List<Map.Entry<String, Integer>> topKeywords(String text, int n) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(frequencies(words(text)).entrySet());
        list.sort((a, b) -> {
            int byCount = Integer.compare(b.getValue(), a.getValue());
            return byCount != 0 ? byCount : a.getKey().compareTo(b.getKey());
        });
        return list.size() > n ? new ArrayList<>(list.subList(0, n)) : list;
    }

    /** Only the words of topKeywords() (used to find common and unique keywords). */
    public static Set<String> topWordSet(String text, int n) {
        Set<String> set = new LinkedHashSet<>();
        for (Map.Entry<String, Integer> e : topKeywords(text, n)) {
            set.add(e.getKey());
        }
        return set;
    }
}
