package edu.utdallas.cs4485.sentencebuilder.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TextProcessingService.
 *
 * @author CS4485 Team
 * @version 1.0
 */
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