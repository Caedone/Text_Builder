package edu.utdallas.cs4485.sentencebuilder.algorithm;

import java.util.*;

/**
 * Implements first-order and second-order Markov chain text generation.
 * Trains on input text and generates new text based on learned patterns.
 *
 * @author CS4485 Team
 * @version 1.0
 */
public class MarkovChainGenerator {

    // First-order chain: word -> list of next words
    private Map<String, List<String>> firstOrderChain;

    // Second-order chain: (word1, word2) -> list of next words
    private Map<String, List<String>> secondOrderChain;

    // Words that can start sentences
    private List<String> sentenceStarters;

    private Random random;
    private boolean isFirstOrder;

    /**
     * Constructor.
     *
     * @param isFirstOrder true for first-order chain, false for second-order
     */
    public MarkovChainGenerator(boolean isFirstOrder) {
        this.isFirstOrder = isFirstOrder;
        this.firstOrderChain = new HashMap<>();
        this.secondOrderChain = new HashMap<>();
        this.sentenceStarters = new ArrayList<>();
        this.random = new Random();
    }

    /**
     * Trains the Markov chain on input text.
     *
     * @param text the training text
     */
    public void train(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        String[] words = text.trim().split("\\s+");
        if (words.length < 2) {
            return;
        }

        // Clean and normalize words
        for (int i = 0; i < words.length; i++) {
            words[i] = cleanWord(words[i]);
        }

        // Track sentence starters
        if (words.length > 0 && !words[0].isEmpty()) {
            sentenceStarters.add(words[0]);
        }

        if (isFirstOrder) {
            trainFirstOrder(words);
        } else {
            trainSecondOrder(words);
        }
    }

    /**
     * Trains a first-order Markov chain.
     *
     * @param words array of words
     */
    private void trainFirstOrder(String[] words) {
        for (int i = 0; i < words.length - 1; i++) {
            String currentWord = words[i];
            String nextWord = words[i + 1];

            if (currentWord.isEmpty() || nextWord.isEmpty()) {
                continue;
            }

            firstOrderChain.putIfAbsent(currentWord, new ArrayList<>());
            firstOrderChain.get(currentWord).add(nextWord);
        }
    }

    /**
     * Trains a second-order Markov chain.
     *
     * @param words array of words
     */
    private void trainSecondOrder(String[] words) {
        for (int i = 0; i < words.length - 2; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            String nextWord = words[i + 2];

            if (word1.isEmpty() || word2.isEmpty() || nextWord.isEmpty()) {
                continue;
            }

            String key = word1 + " " + word2;
            secondOrderChain.putIfAbsent(key, new ArrayList<>());
            secondOrderChain.get(key).add(nextWord);
        }

        // Also train first-order for fallback
        trainFirstOrder(words);
    }

    /**
     * Generates text with a random starting word.
     *
     * @param maxWords maximum number of words to generate
     * @return generated text
     */
    public String generateText(int maxWords) {
        if (sentenceStarters.isEmpty()) {
            return "";
        }

        String startWord = sentenceStarters.get(random.nextInt(sentenceStarters.size()));
        return generateText(startWord, maxWords);
    }

    /**
     * Generates text starting with a specific word.
     *
     * @param startWord the word to start with
     * @param maxWords maximum number of words to generate
     * @return generated text
     */
    public String generateText(String startWord, int maxWords) {
        if (startWord == null || startWord.trim().isEmpty()) {
            return generateText(maxWords);
        }

        startWord = cleanWord(startWord);
        List<String> words = new ArrayList<>();
        words.add(startWord);

        if (isFirstOrder) {
            generateFirstOrder(words, maxWords);
        } else {
            generateSecondOrder(words, maxWords);
        }

        return String.join(" ", words);
    }

    /**
     * Generates text using first-order Markov chain.
     *
     * @param words list to add generated words to
     * @param maxWords maximum number of words
     */
    private void generateFirstOrder(List<String> words, int maxWords) {
        while (words.size() < maxWords) {
            String currentWord = words.get(words.size() - 1);
            List<String> nextWords = firstOrderChain.get(currentWord);

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
    }

    /**
     * Generates text using second-order Markov chain.
     *
     * @param words list to add generated words to
     * @param maxWords maximum number of words
     */
    private void generateSecondOrder(List<String> words, int maxWords) {
        // Need at least 2 words to start second-order generation
        if (words.size() == 1 && !firstOrderChain.isEmpty()) {
            String firstWord = words.get(0);
            List<String> possibleSeconds = firstOrderChain.get(firstWord);
            if (possibleSeconds != null && !possibleSeconds.isEmpty()) {
                words.add(selectWeightedRandom(possibleSeconds));
            }
        }

        while (words.size() < maxWords) {
            if (words.size() < 2) {
                break;
            }

            String word1 = words.get(words.size() - 2);
            String word2 = words.get(words.size() - 1);
            String key = word1 + " " + word2;

            List<String> nextWords = secondOrderChain.get(key);

            if (nextWords == null || nextWords.isEmpty()) {
                // Fallback to first-order
                nextWords = firstOrderChain.get(word2);
                if (nextWords == null || nextWords.isEmpty()) {
                    break;
                }
            }

            String nextWord = selectWeightedRandom(nextWords);
            words.add(nextWord);

            if (isSentenceEnd(nextWord)) {
                break;
            }
        }
    }

    /**
     * Gets autocomplete suggestions for a given word or word pair.
     *
     * @param context the current word or words
     * @param maxSuggestions maximum number of suggestions
     * @return list of suggested next words
     */
    public List<String> getAutoCompleteSuggestions(String context, int maxSuggestions) {
        if (context == null || context.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String[] words = context.trim().split("\\s+");
        List<String> nextWords;

        if (!isFirstOrder && words.length >= 2) {
            // Second-order: use last two words
            String key = words[words.length - 2] + " " + words[words.length - 1];
            nextWords = secondOrderChain.get(key);
        } else if (words.length >= 1) {
            // First-order: use last word
            nextWords = firstOrderChain.get(words[words.length - 1]);
        } else {
            return Collections.emptyList();
        }

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
     * Gets the number of states in the chain.
     *
     * @return number of states
     */
    public int getStateCount() {
        return isFirstOrder ? firstOrderChain.size() : secondOrderChain.size();
    }

    /**
     * Checks if the chain has been trained.
     *
     * @return true if chain has data
     */
    public boolean isTrained() {
        return !firstOrderChain.isEmpty() || !secondOrderChain.isEmpty();
    }
}