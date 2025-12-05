package edu.utdallas.cs4485.sentencebuilder.algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/******************************************************************************
 * N-gram Generator Unit Tests.
 *
 * This JUnit 5 test class verifies the behavior of the NGramGenerator used
 * by the Sentence Builder application for variable-order N-gram text
 * generation (bigram, trigram, etc.).
 *
 * The tests cover several categories:
 *
 * 1. Constructor validation:
 *    - Accepts valid N values (e.g., 1 through 5).
 *    - Rejects invalid N values (zero or negative) with an exception.
 *
 * 2. Training behavior:
 *    - Training with a non-empty corpus should mark the generator as trained
 *      and produce a positive state count.
 *    - Training with empty or null text should not mark the generator as
 *      trained, and the internal state count should remain zero.
 *
 * 3. Text generation:
 *    - Generating text with a random start should return a non-empty string
 *      with a bounded word count.
 *    - Generating text with a specific starting context (e.g., "the") should
 *      produce text that begins with that context (case-insensitive).
 *    - Both bigram (N=2) and trigram (N=3) generators are exercised.
 *
 * 4. Autocomplete behavior:
 *    - getAutoCompleteSuggestions should return at most the requested number
 *      of suggestions and handle both single-word and multi-word contexts.
 *    - An empty context should return an empty suggestion list, so the
 *      caller can safely detect "no context" scenarios.
 *
 * 5. State inspection and lifecycle:
 *    - isTrained and getStateCount expose whether the model has usable
 *      internal state after training.
 *    - getStatistics returns a non-empty summary string that includes
 *      details like the N value and the number of unique N-grams.
 *    - clear resets the internal model so it can be retrained from scratch.
 *
 * 6. Multiple training calls:
 *    - Re-training with additional text should maintain or increase the
 *      internal state count, not reduce it unexpectedly.
 *
 * 7. Generation consistency:
 *    - Multiple generations with the same starting context should always
 *      respect that context, even if the actual generated sequences differ
 *      due to randomness.
 *
 * These tests provide safety nets around the N-gram algorithm so that future
 * changes or optimizations do not silently break core training, generation,
 * or autocomplete behavior.
 *
 * Written by Johnathan Pedraza for CS4485.0W1, capstone project,
 * "Sentence Builder / Babble", starting October 2025.
 * NetID: jxp220060
 ******************************************************************************/

class NGramGeneratorTest {

    private NGramGenerator bigramGenerator;
    private NGramGenerator trigramGenerator;
    private String sampleText;

    @BeforeEach
    void setUp() {
        bigramGenerator = new NGramGenerator(2);
        trigramGenerator = new NGramGenerator(3);
        sampleText = "The quick brown fox jumps over the lazy dog. " +
                     "The dog was sleeping under the tree. " +
                     "The fox was very quick and clever.";
    }

    @Test
    void testConstructorWithValidN() {
        assertDoesNotThrow(() -> new NGramGenerator(1));
        assertDoesNotThrow(() -> new NGramGenerator(5));
    }

    @Test
    void testConstructorWithInvalidN() {
        assertThrows(IllegalArgumentException.class, () -> new NGramGenerator(0));
        assertThrows(IllegalArgumentException.class, () -> new NGramGenerator(-1));
    }

    @Test
    void testGetN() {
        assertEquals(2, bigramGenerator.getN());
        assertEquals(3, trigramGenerator.getN());
    }

    @Test
    void testTrainWithValidText() {
        bigramGenerator.train(sampleText);
        assertTrue(bigramGenerator.isTrained());
        assertTrue(bigramGenerator.getStateCount() > 0);
    }

    @Test
    void testTrainWithEmptyText() {
        bigramGenerator.train("");
        assertFalse(bigramGenerator.isTrained());
        assertEquals(0, bigramGenerator.getStateCount());
    }

    @Test
    void testTrainWithNullText() {
        bigramGenerator.train(null);
        assertFalse(bigramGenerator.isTrained());
        assertEquals(0, bigramGenerator.getStateCount());
    }

    @Test
    void testGenerateTextWithRandomStart() {
        bigramGenerator.train(sampleText);
        String generated = bigramGenerator.generateText(20);

        assertNotNull(generated);
        assertFalse(generated.isEmpty());

        String[] words = generated.split("\\s+");
        assertTrue(words.length > 0);
        assertTrue(words.length <= 20);
    }

    @Test
    void testGenerateTextWithSpecificStart() {
        bigramGenerator.train(sampleText);
        String generated = bigramGenerator.generateText("the", 10);

        assertNotNull(generated);
        assertTrue(generated.toLowerCase().startsWith("the"));
    }

    @Test
    void testGenerateTextWithTrigramGenerator() {
        trigramGenerator.train(sampleText);
        String generated = trigramGenerator.generateText(15);

        assertNotNull(generated);
        assertFalse(generated.isEmpty());
    }

    @Test
    void testGetAutoCompleteSuggestions() {
        bigramGenerator.train(sampleText);
        List<String> suggestions = bigramGenerator.getAutoCompleteSuggestions("the", 5);

        assertNotNull(suggestions);
        // May be empty or have suggestions depending on training data
        assertTrue(suggestions.size() <= 5);
    }

    @Test
    void testGetAutoCompleteSuggestionsWithMultipleWords() {
        trigramGenerator.train(sampleText);
        List<String> suggestions = trigramGenerator.getAutoCompleteSuggestions("the quick", 3);

        assertNotNull(suggestions);
        // May be empty if exact sequence not found
        assertTrue(suggestions.size() <= 3);
    }

    @Test
    void testGetAutoCompleteSuggestionsWithEmptyContext() {
        bigramGenerator.train(sampleText);
        List<String> suggestions = bigramGenerator.getAutoCompleteSuggestions("", 5);

        assertTrue(suggestions.isEmpty());
    }

    @Test
    void testIsTrained() {
        assertFalse(bigramGenerator.isTrained());
        bigramGenerator.train(sampleText);
        assertTrue(bigramGenerator.isTrained());
    }

    @Test
    void testGetStateCount() {
        assertEquals(0, bigramGenerator.getStateCount());
        bigramGenerator.train(sampleText);
        assertTrue(bigramGenerator.getStateCount() > 0);
    }

    @Test
    void testGetStatistics() {
        bigramGenerator.train(sampleText);
        String stats = bigramGenerator.getStatistics();

        assertNotNull(stats);
        assertFalse(stats.isEmpty());
        assertTrue(stats.contains("N=2"));
        assertTrue(stats.contains("Unique N-grams"));
    }

    @Test
    void testClear() {
        bigramGenerator.train(sampleText);
        assertTrue(bigramGenerator.isTrained());

        bigramGenerator.clear();
        assertFalse(bigramGenerator.isTrained());
        assertEquals(0, bigramGenerator.getStateCount());
    }

    @Test
    void testMultipleTrainingCalls() {
        bigramGenerator.train(sampleText);
        int firstCount = bigramGenerator.getStateCount();

        bigramGenerator.train(sampleText);
        int secondCount = bigramGenerator.getStateCount();

        // Second training should add more or maintain state count
        assertTrue(secondCount >= firstCount);
    }

    @Test
    void testGenerationConsistency() {
        bigramGenerator.train(sampleText);

        // Generate multiple times with same start word
        String gen1 = bigramGenerator.generateText("the", 10);
        String gen2 = bigramGenerator.generateText("the", 10);

        // Both should start with "the"
        assertTrue(gen1.toLowerCase().startsWith("the"));
        assertTrue(gen2.toLowerCase().startsWith("the"));

        // May or may not be identical due to randomness
        assertNotNull(gen1);
        assertNotNull(gen2);
    }
}
