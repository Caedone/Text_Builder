package edu.utdallas.cs4485.sentencebuilder.algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/******************************************************************************
 * Markov Chain Generator Unit Tests.
 *
 * This JUnit 5 test class verifies the core behaviors of the
 * MarkovChainGenerator used by the Sentence Builder application.
 *
 * The tests focus on:
 *  1. Training behavior for first-order and second-order Markov models:
 *     - After training, the generator should report that it is "trained"
 *       and should have at least one internal state.
 *
 *  2. Basic text generation:
 *     - Given trained models, the generator should return non-empty strings
 *       for both first-order and second-order generation requests.
 *
 *  3. Starting-word behavior:
 *     - When a starting word is provided, the generated text should begin
 *       with that word (case-insensitive check).
 *
 *  4. Autocomplete behavior:
 *     - After training, the generator should return a non-empty list of
 *       suggestions for a given context word.
 *
 *  5. Defensive behavior for invalid input:
 *     - Training with empty or null text should not mark the generator
 *       as trained, ensuring that later generation calls do not rely on
 *       invalid or missing state.
 *
 * These tests provide regression coverage for the algorithm layer and help
 * ensure that changes to the MarkovChainGenerator do not silently break
 * basic training, generation, or autocomplete functionality.
 *
 * Written by Johnathan Pedraza & Team 37 for CS4485.0W1, capstone project,
 * Test documentation and refinements by Johnathan Pedraza.
 * NetID: jxp220060
 ******************************************************************************/

class MarkovChainGeneratorTest {

    private MarkovChainGenerator firstOrderGenerator;
    private MarkovChainGenerator secondOrderGenerator;
    private String sampleText;

    @BeforeEach
    void setUp() {
        firstOrderGenerator = new MarkovChainGenerator(true);
        secondOrderGenerator = new MarkovChainGenerator(false);
        sampleText = "the quick brown fox jumps over the lazy dog. " +
                     "the quick brown cat runs fast. " +
                     "the lazy dog sleeps all day.";
    }

    @Test
    void testTrainFirstOrder() {
        // TODO: Implement test
        firstOrderGenerator.train(sampleText);
        assertTrue(firstOrderGenerator.isTrained());
        assertTrue(firstOrderGenerator.getStateCount() > 0);
    }

    @Test
    void testTrainSecondOrder() {
        // TODO: Implement test
        secondOrderGenerator.train(sampleText);
        assertTrue(secondOrderGenerator.isTrained());
        assertTrue(secondOrderGenerator.getStateCount() > 0);
    }

    @Test
    void testGenerateTextFirstOrder() {
        // TODO: Implement test
        firstOrderGenerator.train(sampleText);
        String generated = firstOrderGenerator.generateText(10);
        assertNotNull(generated);
        assertFalse(generated.isEmpty());
    }

    @Test
    void testGenerateTextSecondOrder() {
        // TODO: Implement test
        secondOrderGenerator.train(sampleText);
        String generated = secondOrderGenerator.generateText(10);
        assertNotNull(generated);
        assertFalse(generated.isEmpty());
    }

    @Test
    void testGenerateTextWithStartWord() {
        // TODO: Implement test
        firstOrderGenerator.train(sampleText);
        String generated = firstOrderGenerator.generateText("the", 5);
        assertNotNull(generated);
        assertTrue(generated.toLowerCase().startsWith("the"));
    }

    @Test
    void testAutoCompleteSuggestions() {
        // TODO: Implement test
        firstOrderGenerator.train(sampleText);
        List<String> suggestions = firstOrderGenerator.getAutoCompleteSuggestions("the", 5);
        assertNotNull(suggestions);
        assertFalse(suggestions.isEmpty());
    }

    @Test
    void testEmptyTextTraining() {
        // TODO: Implement test
        firstOrderGenerator.train("");
        assertFalse(firstOrderGenerator.isTrained());
    }

    @Test
    void testNullTextTraining() {
        // TODO: Implement test
        firstOrderGenerator.train(null);
        assertFalse(firstOrderGenerator.isTrained());
    }
}