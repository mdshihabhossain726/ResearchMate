package com.researchmate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimilarityTest {

    private final Similarity jaccard = new JaccardSimilarity();
    private final Similarity cosine = new CosineSimilarity();

    @Test
    void jaccardHalf() {
        assertEquals(0.5, jaccard.compare("neural network training", "neural network protein"), 1e-9);
    }

    @Test
    void jaccardIdenticalAndDifferent() {
        assertEquals(1.0, jaccard.compare("neural network", "network neural"), 1e-9);
        assertEquals(0.0, jaccard.compare("neural network", "protein folding"), 1e-9);
    }

    @Test
    void emptyTextGivesZero() {
        assertEquals(0.0, jaccard.compare("", "neural network"), 1e-9);
        assertEquals(0.0, cosine.compare("", ""), 1e-9);
    }

    @Test
    void cosineIdenticalIsOneAndSymmetric() {
        assertEquals(1.0, cosine.compare("neural neural network", "neural neural network"), 1e-9);
        double ab = cosine.compare("neural neural network", "network protein");
        double ba = cosine.compare("network protein", "neural neural network");
        assertEquals(ab, ba, 1e-9);
    }
}
