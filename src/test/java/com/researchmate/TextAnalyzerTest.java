package com.researchmate;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextAnalyzerTest {

    @Test
    void removesStopWordsShortWordsAndNumbers() {
        List<String> w = TextAnalyzer.words("The Data and the 2024 AI of data!");
        assertEquals(List.of("data", "data"), w);
    }

    @Test
    void nullAndEmptyGiveNoWords() {
        assertTrue(TextAnalyzer.words(null).isEmpty());
        assertTrue(TextAnalyzer.words("").isEmpty());
    }

    @Test
    void ligaturesAreNormalized() {
        assertEquals(List.of("final"), TextAnalyzer.words("ﬁnal"));
    }

    @Test
    void topKeywordsSortedByCountThenAlphabet() {
        List<Map.Entry<String, Integer>> top = TextAnalyzer.topKeywords("zebra apple apple mango mango", 3);
        assertEquals("apple", top.get(0).getKey());
        assertEquals("mango", top.get(1).getKey());
        assertEquals("zebra", top.get(2).getKey());
        assertEquals(2, (int) top.get(0).getValue());
    }

    @Test
    void topKeywordsRespectsLimit() {
        assertEquals(2, TextAnalyzer.topKeywords("alpha beta gamma delta", 2).size());
    }
}
