package edu.utdallas.cs4485.sentencebuilder.algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MarkovChainGenerator.
 *
 * @author CS4485 Team
 * @version 1.0
 */
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