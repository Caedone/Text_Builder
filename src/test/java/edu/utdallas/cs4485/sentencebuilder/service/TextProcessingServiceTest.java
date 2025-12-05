package edu.utdallas.cs4485.sentencebuilder.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/******************************************************************************
 * Text Processing Service Unit Tests.
 *
 * This JUnit 5 test class verifies the behavior of the TextProcessingService,
 * which provides higher-level text utilities for the backend pipeline.
 *
 * While TextTokenizer focuses on low-level token operations, the service
 * wraps those capabilities and exposes methods that the rest of the system
 * can call when importing and cleaning raw documents.
 *
 * The tests cover:
 *
 * 1. Tokenization helpers:
 *    - tokenizeWords: splits input into word tokens.
 *    - tokenizeSentences: splits input into sentence chunks.
 *
 * 2. Normalization and cleaning:
 *    - normalizeText: trims whitespace, collapses multiple spaces, and
 *      converts text to lowercase.
 *    - cleanText: removes or normalizes problematic whitespace such as
 *      tabs and extra newlines, producing a smoother string for downstream
 *      processing.
 *
 * 3. Word counting:
 *    - countWords for regular text.
 *    - countWords for empty strings (should return zero).
 *
 * 4. File format support:
 *    - isSupportedFileFormat enforces the set of allowed import formats
 *      (txt, pdf, doc, docx) and rejects unsupported extensions.
 *
 * These tests ensure that the text-processing layer behaves predictably and
 * defensively, which is critical when transforming user-uploaded files into
 * the normalized text that feeds our database and generation algorithms.
 *
 * Written by Johnathan Pedraza for CS4485.0W1, capstone project,
 * "Sentence Builder / Babble", starting October 2025.
 * NetID: jxp220060
 ******************************************************************************/
class TextProcessingServiceTest {

    private TextProcessingService service;

    @BeforeEach
    void setUp() {
        service = new TextProcessingService();
    }

    @Test
    void testTokenizeWords() {
        // TODO: Implement test
        String text = "Hello world! This is a test.";
        List<String> words = service.tokenizeWords(text);

        assertNotNull(words);
        assertTrue(words.size() > 0);
    }

    @Test
    void testTokenizeSentences() {
        // TODO: Implement test
        String text = "First sentence. Second sentence.";
        List<String> sentences = service.tokenizeSentences(text);

        assertNotNull(sentences);
        assertEquals(2, sentences.size());
    }

    @Test
    void testNormalizeText() {
        // TODO: Implement test
        String text = "  HELLO   WORLD  ";
        String normalized = service.normalizeText(text);

        assertEquals("hello world", normalized);
    }

    @Test
    void testCountWords() {
        // TODO: Implement test
        String text = "The quick brown fox jumps over the lazy dog";
        int count = service.countWords(text);

        assertEquals(9, count);
    }

    @Test
    void testCountWordsEmptyString() {
        // TODO: Implement test
        int count = service.countWords("");
        assertEquals(0, count);
    }

    @Test
    void testIsSupportedFileFormat() {
        // TODO: Implement test
        assertTrue(service.isSupportedFileFormat("test.txt"));
        assertTrue(service.isSupportedFileFormat("test.pdf"));
        assertTrue(service.isSupportedFileFormat("test.doc"));
        assertTrue(service.isSupportedFileFormat("test.docx"));
        assertFalse(service.isSupportedFileFormat("test.jpg"));
    }

    @Test
    void testCleanText() {
        // TODO: Implement test
        String text = "Hello\t\tWorld\n\nTest";
        String cleaned = service.cleanText(text);

        assertNotNull(cleaned);
        assertFalse(cleaned.contains("\t"));
        assertFalse(cleaned.contains("\n\n"));
    }
}