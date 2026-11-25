package com.researchmate;

import java.util.Map;

/**
 * Cosine similarity of word-count vectors. Words that appear often count more.
 * cosine = (A . B) / (|A| x |B|)
 */
public class CosineSimilarity implements Similarity {

    @Override
    public double compare(String textA, String textB) {
        Map<String, Integer> a = TextAnalyzer.frequencies(TextAnalyzer.words(textA));
        Map<String, Integer> b = TextAnalyzer.frequencies(TextAnalyzer.words(textB));

        double dot = 0;
        for (Map.Entry<String, Integer> e : a.entrySet()) {
            Integer other = b.get(e.getKey());
            if (other != null) {
                dot += e.getValue() * (double) other;
            }
        }
        double lengthA = length(a);
        double lengthB = length(b);
        if (lengthA == 0 || lengthB == 0) {
            return 0.0;
        }
        return dot / (lengthA * lengthB);
    }

    private double length(Map<String, Integer> counts) {
        double sum = 0;
        for (int count : counts.values()) {
            sum += count * (double) count;
        }
        return Math.sqrt(sum);
    }

    @Override
    public String toString() {
        return "Cosine (word counts)";
    }
}
