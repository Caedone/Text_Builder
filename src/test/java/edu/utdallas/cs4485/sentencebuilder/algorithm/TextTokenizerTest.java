package edu.utdallas.cs4485.sentencebuilder.algorithm;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/******************************************************************************
 * Text Tokenizer Unit Tests.
 *
 * This JUnit 5 test class verifies the behavior of the TextTokenizer utility
 * that is responsible for low-level text processing in the Sentence Builder
 * pipeline.
 *
 * The tokenizer is a core part of the backend: it converts raw input text
 * into normalized words and sentences that the Markov and N-gram models
 * can train on safely.
 *
 * The tests cover:
 *
 * 1. Word tokenization:
 *    - Splits input strings into individual words.
 *    - Ensures basic non-empty behavior and checks for expected tokens.
 *
 * 2. Sentence tokenization:
 *    - Splits input text on '.', '!' and '?' boundaries.
 *    - Confirms the correct number of sentences for a basic example.
 *
 * 3. Normalization:
 *    - Trims excess whitespace.
 *    - Converts text to lowercase.
 *    - Collapses multiple spaces into single spaces.
 *
 * 4. Punctuation handling:
 *    - isPunctuation identifies punctuation-only tokens.
 *    - isSentenceEnd identifies sentence terminators.
 *    - removePunctuation strips end punctuation from words.
 *
 * 5. Word validity:
 *    - isValidWord returns true for real words.
 *    - Returns false for pure punctuation or whitespace-only input.
 *
 * 6. Edge cases:
 *    - tokenization on empty strings should return empty lists.
 *    - tokenization on null strings should also return empty lists.
 *
 * These tests help guarantee that the text-processing layer is stable and
 * predictable, which is critical for feeding clean data into the database
 * and generation algorithms.
 *
 * Written by Johnathan Pedraza for CS4485.0W1, capstone project,
 * "Sentence Builder / Babble", starting October 2025.
 * NetID: jxp220060
 ******************************************************************************/

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