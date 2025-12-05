package edu.utdallas.cs4485.sentencebuilder.service;

import edu.utdallas.cs4485.sentencebuilder.model.GenerationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/******************************************************************************
 * Markov Chain Service Unit Tests.
 *
 * This JUnit 5 test class verifies the behavior of the MarkovChainService,
 * which is the backend service layer that connects the Markov generation
 * algorithms to the rest of the application.
 *
 * The service is responsible for:
 *  - Training first-order and second-order Markov models on input text.
 *  - Tracking which models are currently trained and ready to use.
 *  - Generating text with or without a specific starting word.
 *  - Providing autocomplete suggestions based on a given context.
 *  - Resetting internal state so the service can be retrained.
 *  - Exposing simple query-style methods (e.g., getStateCount) that the
 *    UI and higher-level components can call without touching the
 *    underlying algorithm classes directly.
 *
 * These tests focus on end-to-end service behavior rather than the internal
 * implementation details of the MarkovChainGenerator itself. They treat the
 * service as a black box: train it, ask it to generate text, and assert that
 * it returns a well-formed GenerationResult and consistent state flags.
 *
 * Written by Johnathan Pedraza for CS4485.0W1, capstone project,
 * "Sentence Builder / Babble", starting October 2025.
 * NetID: jxp220060
 ******************************************************************************/

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