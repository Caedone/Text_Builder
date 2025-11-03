package edu.utdallas.cs4485.sentencebuilder.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tokenizes text into words and sentences.
 * Handles punctuation and special characters.
 *
 * @author CS4485 Team
 * @version 1.0
 */
public class TextTokenizer {

    private static final Pattern WORD_PATTERN = Pattern.compile("\\b[\\w']+\\b|[.!?,;:]");
    private static final Pattern SENTENCE_END_PATTERN = Pattern.compile("[.!?]\\s*");

    /**
     * Tokenizes text into individual words.
     *
     * @param text the text to tokenize
     * @return list of words
     */
    public static List<String> tokenizeWords(String text) {
        List<String> words = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            return words;
        }

        Matcher matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            String word = matcher.group();
            if (word != null && !word.trim().isEmpty()) {
                words.add(word);
            }
        }

        return words;
    }

    /**
     * Tokenizes text into sentences.
     *
     * @param text the text to tokenize
     * @return list of sentences
     */
    public static List<String> tokenizeSentences(String text) {
        List<String> sentences = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            return sentences;
        }

        String[] splits = SENTENCE_END_PATTERN.split(text);
        for (String sentence : splits) {
            String trimmed = sentence.trim();
            if (!trimmed.isEmpty()) {
                sentences.add(trimmed);
            }
        }

        return sentences;
    }

    /**
     * Normalizes text by removing extra whitespace and converting to lowercase.
     *
     * @param text the text to normalize
     * @return normalized text
     */
    public static String normalizeText(String text) {
        if (text == null) {
            return "";
        }

        // Remove extra whitespace
        text = text.replaceAll("\\s+", " ");

        // Convert to lowercase
        text = text.toLowerCase();

        return text.trim();
    }

    /**
     * Checks if a token is punctuation.
     *
     * @param token the token to check
     * @return true if token is punctuation
     */
    public static boolean isPunctuation(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        return token.matches("[.!?,;:]+");
    }

    /**
     * Checks if a token ends a sentence.
     *
     * @param token the token to check
     * @return true if token is sentence-ending punctuation
     */
    public static boolean isSentenceEnd(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        return token.matches("[.!?]+") || token.endsWith(".") ||
               token.endsWith("!") || token.endsWith("?");
    }

    /**
     * Removes punctuation from a word.
     *
     * @param word the word to clean
     * @return word without punctuation
     */
    public static String removePunctuation(String word) {
        if (word == null) {
            return "";
        }

        return word.replaceAll("[^\\w']", "");
    }

    /**
     * Checks if a string is a valid word (not just punctuation or whitespace).
     *
     * @param word the string to check
     * @return true if valid word
     */
    public static boolean isValidWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            return false;
        }

        String cleaned = removePunctuation(word);
        return !cleaned.isEmpty();
    }
}