package com.researchmate;

/**
 * ABSTRACTION: this interface says WHAT a similarity method does, not HOW.
 * JaccardSimilarity and CosineSimilarity are two different ways to do it.
 */
public interface Similarity {

    /** Compares two texts. The result is between 0.0 (nothing in common) and 1.0 (same words). */
    double compare(String textA, String textB);
}
