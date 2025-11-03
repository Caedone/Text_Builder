package edu.utdallas.cs4485.sentencebuilder.algorithm;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TextTokenizer.
 *
 * @author CS4485 Team
 * @version 1.0
 */
class TextTokenizerTest {

    @Test
    void testTokenizeWords() {
        // TODO: Implement test
        String text = "The quick brown fox jumps.";
        List<String> words = TextTokenizer.tokenizeWords(text);

        assertNotNull(words);
        assertTrue(words.size() > 0);
        assertTrue(words.contains("quick"));
    }

    @Test
    void testTokenizeSentences() {
        // TODO: Implement test
        String text = "First sentence. Second sentence! Third sentence?";
        List<String> sentences = TextTokenizer.tokenizeSentences(text);

        assertNotNull(sentences);
        assertEquals(3, sentences.size());
    }

    @Test
    void testNormalizeText() {
        // TODO: Implement test
        String text = "  Hello   World  ";
        String normalized = TextTokenizer.normalizeText(text);

        assertEquals("hello world", normalized);
    }

    @Test
    void testIsPunctuation() {
        // TODO: Implement test
        assertTrue(TextTokenizer.isPunctuation("."));
        assertTrue(TextTokenizer.isPunctuation("!"));
        assertFalse(TextTokenizer.isPunctuation("hello"));
    }

    @Test
    void testIsSentenceEnd() {
        // TODO: Implement test
        assertTrue(TextTokenizer.isSentenceEnd("."));
        assertTrue(TextTokenizer.isSentenceEnd("!"));
        assertTrue(TextTokenizer.isSentenceEnd("?"));
        assertFalse(TextTokenizer.isSentenceEnd(","));
    }

    @Test
    void testRemovePunctuation() {
        // TODO: Implement test
        String word = "hello!";
        String cleaned = TextTokenizer.removePunctuation(word);

        assertEquals("hello", cleaned);
    }

    @Test
    void testIsValidWord() {
        // TODO: Implement test
        assertTrue(TextTokenizer.isValidWord("hello"));
        assertFalse(TextTokenizer.isValidWord("!!!"));
        assertFalse(TextTokenizer.isValidWord("   "));
    }

    @Test
    void testEmptyString() {
        // TODO: Implement test
        List<String> words = TextTokenizer.tokenizeWords("");
        assertTrue(words.isEmpty());
    }

    @Test
    void testNullString() {
        // TODO: Implement test
        List<String> words = TextTokenizer.tokenizeWords(null);
        assertTrue(words.isEmpty());
    }
}