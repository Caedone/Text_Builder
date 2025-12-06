package edu.utdallas.cs4485.sentencebuilder.algorithm;

import java.util.*;

/**
 *
 * Algorithm implementing N-gram based text generation with configurable context
 * window lengths for sophisticated pattern matching.
 *
 * Extends basic Markov chain concepts by using sequences of N consecutive words
 * as context for predicting the next word. Supports variable N values (typically
 * 2-5) allowing users to balance between creative diversity (lower N) and closer
 * adherence to source text patterns (higher N).
 *
 * Trains on input text to build N-gram sequence maps, tracking both individual
 * word starters and complete N-gram starters for sentence generation. Implements
 * fallback strategies when exact N-gram matches aren't found, ensuring robust
 * text generation across diverse inputs and generation parameters.
 *
 * @author Manraj Singh
 */
public class NGramGenerator {

    // N-gram chains: ngram sequence -> list of next words
    private Map<String, List<String>> ngramChains;

    // Words that can start sentences
    private List<String> sentenceStarters;

    // N-grams that can start sentences (for N > 1)
    private List<String> ngramStarters;

    private Random random;
    private int n; // The N in N-gram

    /**
     * Constructor.
     *
     * @param n the N value (must be >= 1)
     */
    public NGramGenerator(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("N must be >= 1");
        }
        this.n = n;
        this.ngramChains = new HashMap<>();
        this.sentenceStarters = new ArrayList<>();
        this.ngramStarters = new ArrayList<>();
        this.random = new Random();
    }

    /**
     * Gets the N value for this generator.
     *
     * @return the N value
     */
    public int getN() {
        return n;
    }

    /**
     * Trains the N-gram model on input text.
     *
     * @param text the training text
     */
    public void train(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        String[] words = text.trim().split("\\s+");
        if (words.length < n + 1) {
            return;
        }

        // Clean and normalize words
        for (int i = 0; i < words.length; i++) {
            words[i] = cleanWord(words[i]);
        }

        // Track sentence starters
        if (n == 1 && words.length > 0 && !words[0].isEmpty()) {
            sentenceStarters.add(words[0]);
        } else if (n > 1 && words.length >= n) {
            String starter = buildNGramKey(words, 0, n);
            if (!starter.isEmpty()) {
                ngramStarters.add(starter);
            }
        }

        // Build N-gram chains
        for (int i = 0; i <= words.length - n - 1; i++) {
            String ngramKey = buildNGramKey(words, i, n);
            String nextWord = words[i + n];

            if (ngramKey.isEmpty() || nextWord.isEmpty()) {
                continue;
            }

            ngramChains.putIfAbsent(ngramKey, new ArrayList<>());
            ngramChains.get(ngramKey).add(nextWord);

            // Check for sentence boundaries
            if (isSentenceStart(words, i)) {
                if (n == 1) {
                    sentenceStarters.add(words[i]);
                } else {
                    ngramStarters.add(ngramKey);
                }
            }
        }
    }

    /**
     * Generates text with a random starting N-gram.
     *
     * @param maxWords maximum number of words to generate
     * @return generated text
     */
    public String generateText(int maxWords) {
        if (ngramChains.isEmpty()) {
            return "";
        }

        String startKey;
        if (n == 1 && !sentenceStarters.isEmpty()) {
            startKey = sentenceStarters.get(random.nextInt(sentenceStarters.size()));
        } else if (n > 1 && !ngramStarters.isEmpty()) {
            startKey = ngramStarters.get(random.nextInt(ngramStarters.size()));
        } else {
            // Fall back to any N-gram
            List<String> keys = new ArrayList<>(ngramChains.keySet());
            startKey = keys.get(random.nextInt(keys.size()));
        }

        return generateText(startKey, maxWords);
    }

    /**
     * Generates text starting with a specific word or N-gram sequence.
     *
     * @param startText the word(s) to start with
     * @param maxWords maximum number of words to generate
     * @return generated text
     */
    public String generateText(String startText, int maxWords) {
        if (startText == null || startText.trim().isEmpty()) {
            return generateText(maxWords);
        }

        String[] startWords = startText.trim().split("\\s+");
        for (int i = 0; i < startWords.length; i++) {
            startWords[i] = cleanWord(startWords[i]);
        }

        List<String> words = new ArrayList<>();
        Collections.addAll(words, startWords);

        // Generate additional words
        while (words.size() < maxWords) {
            String ngramKey = buildNGramKeyFromList(words, words.size() - n, n);

            if (ngramKey.isEmpty()) {
                break;
            }

            List<String> nextWords = ngramChains.get(ngramKey);

            if (nextWords == null || nextWords.isEmpty()) {
                break;
            }

            String nextWord = selectWeightedRandom(nextWords);
            words.add(nextWord);

            // Stop at sentence end punctuation
            if (isSentenceEnd(nextWord)) {
                break;
            }
        }

        return String.join(" ", words);
    }

    /**
     * Gets autocomplete suggestions for a given context.
     *
     * @param context the current word(s)
     * @param maxSuggestions maximum number of suggestions
     * @return list of suggested next words
     */
    public List<String> getAutoCompleteSuggestions(String context, int maxSuggestions) {
        if (context == null || context.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String[] words = context.trim().split("\\s+");
        for (int i = 0; i < words.length; i++) {
            words[i] = cleanWord(words[i]);
        }

        String ngramKey;
        if (words.length >= n) {
            // Use the last N words
            ngramKey = buildNGramKey(words, words.length - n, n);
        } else {
            // Context is shorter than N, use what we have
            ngramKey = String.join(" ", words);
        }

        List<String> nextWords = ngramChains.get(ngramKey);

        if (nextWords == null || nextWords.isEmpty()) {
            return Collections.emptyList();
        }

        // Get unique suggestions sorted by frequency
        Map<String, Integer> frequency = new HashMap<>();
        for (String word : nextWords) {
            frequency.put(word, frequency.getOrDefault(word, 0) + 1);
        }

        return frequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(maxSuggestions)
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * Builds an N-gram key from an array of words.
     *
     * @param words the word array
     * @param startIndex starting index
     * @param length number of words to include
     * @return the N-gram key
     */
    private String buildNGramKey(String[] words, int startIndex, int length) {
        if (startIndex < 0 || startIndex + length > words.length) {
            return "";
        }

        StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                key.append(" ");
            }
            key.append(words[startIndex + i]);
        }

        return key.toString();
    }

    /**
     * Builds an N-gram key from a list of words.
     *
     * @param words the word list
     * @param startIndex starting index
     * @param length number of words to include
     * @return the N-gram key
     */
    private String buildNGramKeyFromList(List<String> words, int startIndex, int length) {
        if (startIndex < 0 || startIndex + length > words.size()) {
            return "";
        }

        StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                key.append(" ");
            }
            key.append(words.get(startIndex + i));
        }

        return key.toString();
    }

    /**
     * Checks if a position in the word array is a sentence start.
     *
     * @param words the word array
     * @param index the index to check
     * @return true if sentence start
     */
    private boolean isSentenceStart(String[] words, int index) {
        if (index == 0) {
            return true;
        }

        // Check if previous word ends with sentence-ending punctuation
        String prevWord = words[index - 1];
        return isSentenceEnd(prevWord);
    }

    /**
     * Selects a random word from a list (weighted by frequency).
     *
     * @param words list of words
     * @return randomly selected word
     */
    private String selectWeightedRandom(List<String> words) {
        if (words.isEmpty()) {
            return "";
        }
        return words.get(random.nextInt(words.size()));
    }

    /**
     * Cleans and normalizes a word.
     *
     * @param word the word to clean
     * @return cleaned word
     */
    private String cleanWord(String word) {
        if (word == null) {
            return "";
        }
        return word.trim().toLowerCase();
    }

    /**
     * Checks if a word ends a sentence.
     *
     * @param word the word to check
     * @return true if word ends with sentence-ending punctuation
     */
    private boolean isSentenceEnd(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        char lastChar = word.charAt(word.length() - 1);
        return lastChar == '.' || lastChar == '!' || lastChar == '?';
    }

    /**
     * Gets the number of N-grams in the model.
     *
     * @return number of N-grams
     */
    public int getStateCount() {
        return ngramChains.size();
    }

    /**
     * Checks if the model has been trained.
     *
     * @return true if model has data
     */
    public boolean isTrained() {
        return !ngramChains.isEmpty();
    }

    /**
     * Gets statistics about the N-gram model.
     *
     * @return statistics string
     */
    public String getStatistics() {
        int totalTransitions = ngramChains.values().stream()
                .mapToInt(List::size)
                .sum();

        return String.format("N-gram Statistics (N=%d):\n" +
                           "  Unique N-grams: %d\n" +
                           "  Total transitions: %d\n" +
                           "  Sentence starters: %d",
                n, ngramChains.size(), totalTransitions,
                n == 1 ? sentenceStarters.size() : ngramStarters.size());
    }

    /**
     * Clears all training data.
     */
    public void clear() {
        ngramChains.clear();
        sentenceStarters.clear();
        ngramStarters.clear();
    }
}
