package com.researchmate;

import java.util.HashSet;
import java.util.Set;

/** Jaccard = (words in both texts) / (all different words of both texts). It ignores how often a word appears. */
public class JaccardSimilarity implements Similarity {

    @Override
    public double compare(String textA, String textB) {
        Set<String> a = new HashSet<>(TextAnalyzer.words(textA));
        Set<String> b = new HashSet<>(TextAnalyzer.words(textB));

        Set<String> both = new HashSet<>(a);
        both.retainAll(b);                     // intersection
        Set<String> all = new HashSet<>(a);
        all.addAll(b);                         // union

        return all.isEmpty() ? 0.0 : both.size() / (double) all.size();
    }

    @Override
    public String toString() {
        return "Jaccard (shared words)";
    }
}
