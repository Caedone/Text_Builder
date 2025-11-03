package edu.utdallas.cs4485.sentencebuilder.service;

import edu.utdallas.cs4485.sentencebuilder.model.GenerationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MarkovChainService.
 *
 * @author CS4485 Team
 * @version 1.0
 */
class MarkovChainServiceTest {

    private MarkovChainService service;
    private String sampleText;

    @BeforeEach
    void setUp() {
        service = new MarkovChainService();
        sampleText = "the quick brown fox jumps over the lazy dog. " +
                     "the quick brown cat runs fast.";
    }

    @Test
    void testTrainFirstOrder() {
        // TODO: Implement test
        service.train(sampleText, false);
        assertTrue(service.isTrained(true));
    }

    @Test
    void testTrainSecondOrder() {
        // TODO: Implement test
        service.train(sampleText, true);
        assertTrue(service.isTrained(false));
    }

    @Test
    void testGenerateTextFirstOrder() {
        // TODO: Implement test
        service.train(sampleText, false);
        GenerationResult result = service.generateText(null, 10, false);

        assertNotNull(result);
        assertNotNull(result.getGeneratedText());
        assertEquals("first-order", result.getAlgorithm());
    }

    @Test
    void testGenerateTextSecondOrder() {
        // TODO: Implement test
        service.train(sampleText, true);
        GenerationResult result = service.generateText(null, 10, true);

        assertNotNull(result);
        assertNotNull(result.getGeneratedText());
        assertEquals("second-order", result.getAlgorithm());
    }

    @Test
    void testGenerateTextWithStartWord() {
        // TODO: Implement test
        service.train(sampleText, false);
        GenerationResult result = service.generateText("the", 5, false);

        assertNotNull(result);
        assertTrue(result.getGeneratedText().toLowerCase().startsWith("the"));
        assertEquals("the", result.getStartWord());
    }

    @Test
    void testGetAutoCompleteSuggestions() {
        // TODO: Implement test
        service.train(sampleText, false);
        List<String> suggestions = service.getAutoCompleteSuggestions("the", 5, false);

        assertNotNull(suggestions);
        // Suggestions list may be empty if no matches found
    }

    @Test
    void testReset() {
        // TODO: Implement test
        service.train(sampleText, false);
        assertTrue(service.isTrained(true));

        service.reset();
        assertFalse(service.isTrained(true));
        assertFalse(service.isTrained(false));
    }

    @Test
    void testGetStateCount() {
        // TODO: Implement test
        service.train(sampleText, false);
        int stateCount = service.getStateCount(true);

        assertTrue(stateCount > 0);
    }
}