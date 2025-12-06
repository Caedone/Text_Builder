package edu.utdallas.cs4485.sentencebuilder.service;

import edu.utdallas.cs4485.sentencebuilder.dao.WordDAO;
import edu.utdallas.cs4485.sentencebuilder.dao.WordPairDAO;
import edu.utdallas.cs4485.sentencebuilder.dao.NGramDAO;
import edu.utdallas.cs4485.sentencebuilder.model.Word;
import edu.utdallas.cs4485.sentencebuilder.model.WordPair;
import edu.utdallas.cs4485.sentencebuilder.model.NGram;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Service class providing intelligent autocomplete functionality with context-aware
 * word suggestions based on database-stored word relationships.
 *
 * Leverages word frequency data, word pairs, and N-gram sequences to generate
 * relevant suggestions as users type in the UI. Supports first-order (single word
 * context), second-order (two word context), and higher-order N-gram contexts for
 * increasingly sophisticated predictions.
 *
 * DAO initialization occurs once during construction to maintain fast response times
 * during user typing interactions. Queries are optimized to return ranked suggestions
 * based on transition probabilities and frequency statistics from the learned corpus.
 *
 * @author Bhaskar Atmakuri
 * @author Rahman-Danish, Rizvy
 */
public class AutoCompleteService {

    private WordDAO wordDAO;
    private WordPairDAO wordPairDAO;
    private NGramDAO ngramDAO;

    /**
     * Constructor.
     *
     * Rizvy: Ensured DAO initialization happens once here so autocomplete
     * calls remain fast during user typing in the GUI.
     */
    public AutoCompleteService() {
        this.wordDAO = new WordDAO();
        this.wordPairDAO = new WordPairDAO();
        this.ngramDAO = new NGramDAO();
    }

    /**
     * Gets autocomplete suggestions for the given context.
     *
     * @param context the current word or words
     * @param n the N-gram order (1 for first-order, 2 for second-order, 3+ for N-gram)
     * @param maxSuggestions maximum number of suggestions
     * @return list of suggested words
     */
    public List<String> getSuggestions(String context, int n, int maxSuggestions) {
        if (context == null || context.trim().isEmpty()) {
            return List.of();
        }

        try {
            String[] words = context.trim().toLowerCase().split("\\s+");

            if (n == 1) {
                // First-order: use the last word
                return getFirstOrderSuggestions(words[words.length - 1], maxSuggestions);
            } else if (n == 2) {
                // Second-order: use the last two words (or one if only one available)
                return getSecondOrderSuggestions(words, maxSuggestions);
            } else {
                // N-gram: use the last N words
                return getNGramSuggestions(words, n, maxSuggestions);
            }
        } catch (SQLException e) {
            // Rizvy: Ensured this error case does not break UI typing flow.
            System.err.println("Error getting autocomplete suggestions: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Gets first-order autocomplete suggestions (based on single word).
     *
     * Rizvy Testing:
     * - Verified that suggestions appear ordered by transition count.
     * - Ensured null handling is correct when no next word exists.
     * @param word the current word
     * @param maxSuggestions maximum number of suggestions
     * @return list of suggested words
     */
    private List<String> getFirstOrderSuggestions(String word, int maxSuggestions) throws SQLException {
        Word wordObj = wordDAO.findByText(word);
        if (wordObj == null) {
            return List.of();
        }

        // Get all word pairs starting with this word, ordered by transition count
        List<WordPair> pairs = wordPairDAO.findByFirstWordId(wordObj.getWordId());

        return pairs.stream()
                .sorted((a, b) -> Integer.compare(b.getTransitionCount(), a.getTransitionCount()))
                .limit(maxSuggestions)
                .map(pair -> {
                    try {
                        Word nextWord = wordDAO.findById(pair.getSecondWordId());
                        return nextWord != null ? nextWord.getWordText() : null;
                    } catch (SQLException e) {
                        return null;
                    }
                })
                .filter(w -> w != null)
                .collect(Collectors.toList());
    }

    /**
     * Gets second-order autocomplete suggestions (based on word pair).
     * For context "word1 word2", we want to find what typically follows this pair.
     * We verify that the pair (word1, word2) exists in training data, then suggest what follows word2.
     *
     * Rizvy Testing:
     * - Checked that fallback to first-order works when pair does not exist.
     * - Verified UI does not freeze even when DB returns no results.
     *
     * @param words the current words
     * @param maxSuggestions maximum number of suggestions
     * @return list of suggested words
     */
    private List<String> getSecondOrderSuggestions(String[] words, int maxSuggestions) throws SQLException {
        if (words.length < 1) {
            return List.of();
        }

        if (words.length < 2) {
            // Fallback to first-order if only one word
            return getFirstOrderSuggestions(words[words.length - 1], maxSuggestions);
        }

        // Get the last two words
        String word1 = words[words.length - 2];
        String word2 = words[words.length - 1];

        // Find the words in database
        Word word1Obj = wordDAO.findByText(word1);
        Word word2Obj = wordDAO.findByText(word2);

        if (word1Obj == null || word2Obj == null) {
            // Fallback to first-order if words not found
            if (word2Obj != null) {
                return getFirstOrderSuggestions(word2, maxSuggestions);
            }
            return List.of();
        }

        // First verify that the pair (word1, word2) exists in our training data
        WordPair contextPair = wordPairDAO.findByWordIds(word1Obj.getWordId(), word2Obj.getWordId());

        if (contextPair == null) {
            // This word pair never appeared together in training, fallback to first-order
            return getFirstOrderSuggestions(word2, maxSuggestions);
        }

        // Now get all word pairs where word2 is the first word
        // This gives us all words that follow word2 in the training data
        List<WordPair> pairs = wordPairDAO.findByFirstWordId(word2Obj.getWordId());

        if (pairs.isEmpty()) {
            return List.of();
        }

        // Convert to word strings and return, already sorted by transition count
        return pairs.stream()
                .limit(maxSuggestions)
                .map(pair -> {
                    try {
                        Word nextWord = wordDAO.findById(pair.getSecondWordId());
                        return nextWord != null ? nextWord.getWordText() : null;
                    } catch (SQLException e) {
                        return null;
                    }
                })
                .filter(w -> w != null)
                .collect(Collectors.toList());
    }

    /**
     * Gets N-gram autocomplete suggestions (based on N words).
     *
     * Rizvy Testing:
     * - Verified fallback to second-order and first-order works correctly.
     * - Ensured autocomplete stays responsive even with long input sequences.
     *
     * @param words the current words
     * @param n the N-gram order
     * @param maxSuggestions maximum number of suggestions
     * @return list of suggested words
     */
    private List<String> getNGramSuggestions(String[] words, int n, int maxSuggestions) throws SQLException {
        if (words.length < n - 1) {
            // Not enough context, fallback
            if (words.length >= 2) {
                return getSecondOrderSuggestions(words, maxSuggestions);
            } else {
                return getFirstOrderSuggestions(words[words.length - 1], maxSuggestions);
            }
        }

        // Build the N-gram context (last N-1 words)
        StringBuilder contextBuilder = new StringBuilder();
        for (int i = Math.max(0, words.length - (n - 1)); i < words.length; i++) {
            if (contextBuilder.length() > 0) {
                contextBuilder.append(" ");
            }
            contextBuilder.append(words[i]);
        }
        String ngramContext = contextBuilder.toString();

        // Find matching N-grams
        List<NGram> ngrams = ngramDAO.findByNgramText(n, ngramContext);

        if (ngrams.isEmpty()) {
            // Fallback to second-order
            if (words.length >= 2) {
                return getSecondOrderSuggestions(words, maxSuggestions);
            } else {
                return getFirstOrderSuggestions(words[words.length - 1], maxSuggestions);
            }
        }

        // Sort by transition count and get suggestions
        return ngrams.stream()
                .sorted((a, b) -> Integer.compare(b.getTransitionCount(), a.getTransitionCount()))
                .limit(maxSuggestions)
                .map(ngram -> {
                    try {
                        Word nextWord = wordDAO.findById(ngram.getNextWordId());
                        return nextWord != null ? nextWord.getWordText() : null;
                    } catch (SQLException e) {
                        return null;
                    }
                })
                .filter(w -> w != null)
                .collect(Collectors.toList());
    }
}
