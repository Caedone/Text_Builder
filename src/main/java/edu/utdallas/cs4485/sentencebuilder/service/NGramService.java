package edu.utdallas.cs4485.sentencebuilder.service;

import edu.utdallas.cs4485.sentencebuilder.algorithm.NGramGenerator;
import edu.utdallas.cs4485.sentencebuilder.algorithm.TextTokenizer;
import edu.utdallas.cs4485.sentencebuilder.dao.NGramDAO;
import edu.utdallas.cs4485.sentencebuilder.dao.WordDAO;
import edu.utdallas.cs4485.sentencebuilder.model.GenerationResult;
import edu.utdallas.cs4485.sentencebuilder.model.NGram;
import edu.utdallas.cs4485.sentencebuilder.model.Word;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * Service class managing N-gram operations including text processing, sequence
 * extraction, generation, and database persistence.
 *
 * Coordinates between the NGramGenerator algorithm and database layer, processing
 * imported text to extract N-gram sequences with configurable N values (bigrams,
 * trigrams, etc.). The extracted N-gram data feeds both text generation and
 * autocomplete functionality throughout the application.
 *
 * Provides a clean API for JavaFX controllers to request N-gram-based text
 * generation with various parameters, ensuring efficient DAO usage for database
 * operations. Validates behavior across different N values and maintains data
 * quality for downstream consumers.
 *
 * @author Caedon Ewing
 * @author Rahman-Danish, Rizvy
 */
public class NGramService {

    private NGramDAO ngramDAO;
    private WordDAO wordDAO;

    /**
     * Constructor.
     * Rizvy:
     * - Ensured DAO objects are reused to keep DB access efficient.
     */
    public NGramService() {
        this.ngramDAO = new NGramDAO();
        this.wordDAO = new WordDAO();
    }

    /**
     * Processes text and stores N-grams in the database.
     *
     * @param text the text to process
     * @param n the N value
     * @throws SQLException if database error occurs
     */
    public void processAndStoreNGrams(String text, int n) throws SQLException {
        if (text == null || text.trim().isEmpty() || n < 1) {
            return;
        }

        // Tokenize text into words
        String normalizedText = TextTokenizer.normalizeText(text);
        String[] words = normalizedText.split("\\s+");

        if (words.length < n + 1) {
            return;
        }

        // Process each N-gram
        for (int i = 0; i <= words.length - n - 1; i++) {
            // Build N-gram sequence
            StringBuilder ngramBuilder = new StringBuilder();
            for (int j = 0; j < n; j++) {
                if (j > 0) {
                    ngramBuilder.append(" ");
                }
                ngramBuilder.append(words[i + j].toLowerCase().trim());
            }
            String ngramText = ngramBuilder.toString();
            String nextWord = words[i + n].toLowerCase().trim();

            // Get or create word in database
            Word wordObj = wordDAO.findByText(nextWord);
            if (wordObj == null) {
                wordObj = new Word(nextWord);
                wordObj.setTotalCount(1);
                wordObj = wordDAO.insert(wordObj);
            }

            // Get or create N-gram in database
            NGram ngram = ngramDAO.findByTextAndNextWord(n, ngramText, wordObj.getWordId());
            if (ngram == null) {
                ngram = new NGram(n, ngramText, wordObj.getWordId());
                ngramDAO.insert(ngram);
            } else {
                ngram.incrementTransitionCount();
                ngramDAO.update(ngram);
            }
        }

        // Recalculate probabilities
        ngramDAO.recalculateProbabilities();
    }

    /**
     * Generates text using N-gram algorithm.
     *
     * @param startText optional starting text
     * @param maxWords maximum number of words
     * @param n the N value
     * @return generation result
     * @throws SQLException if database error occurs
     */
    public GenerationResult generateText(String startText, int maxWords, int n) throws SQLException {
        long startTime = System.currentTimeMillis();

        // Load N-grams from database
        NGramGenerator generator = new NGramGenerator(n);
        loadNGramsIntoGenerator(generator, n);

        // Generate text
        String generatedText;
        if (startText != null && !startText.trim().isEmpty()) {
            generatedText = generator.generateText(startText, maxWords);
        } else {
            generatedText = generator.generateText(maxWords);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Create result
        GenerationResult result = new GenerationResult();
        result.setGeneratedText(generatedText);
        result.setAlgorithm("N-gram (N=" + n + ")");
        result.setDurationMs(duration);
        result.setWordCount(generatedText.isEmpty() ? 0 : generatedText.split("\\s+").length);

        return result;
    }

    /**
     * Gets autocomplete suggestions using N-grams.
     *
     * @param context the current text context
     * @param n the N value
     * @param maxSuggestions maximum number of suggestions
     * @return list of suggestions
     * @throws SQLException if database error occurs
     */
    public List<String> getAutoCompleteSuggestions(String context, int n, int maxSuggestions) throws SQLException {
        if (context == null || context.trim().isEmpty()) {
            return List.of();
        }

        String[] words = context.trim().split("\\s+");
        String ngramText;

        if (words.length >= n) {
            // Use last N words
            StringBuilder builder = new StringBuilder();
            for (int i = words.length - n; i < words.length; i++) {
                if (i > words.length - n) {
                    builder.append(" ");
                }
                builder.append(words[i].toLowerCase());
            }
            ngramText = builder.toString();
        } else {
            // Use all available words
            ngramText = context.toLowerCase();
        }

        // Get N-grams from database
        List<NGram> ngrams = ngramDAO.findByNgramText(n, ngramText);

        // Sort by probability and return top suggestions
        return ngrams.stream()
                .sorted((a, b) -> Double.compare(b.getTransitionProbability(), a.getTransitionProbability()))
                .limit(maxSuggestions)
                .map(NGram::getNextWordText)
                .toList();
    }

    /**
     * Loads N-grams from database into a generator for text generation.
     *
     * @param generator the generator to load data into
     * @param n the N value
     * @throws SQLException if database error occurs
     */
    private void loadNGramsIntoGenerator(NGramGenerator generator, int n) throws SQLException {
        List<NGram> ngrams = ngramDAO.findByN(n, 10000); // Load top 10000 N-grams

        // Build training text from N-grams
        StringBuilder trainingText = new StringBuilder();
        for (NGram ngram : ngrams) {
            // Repeat N-gram based on frequency to maintain probability distribution
            int repetitions = Math.min(ngram.getTransitionCount(), 100); // Cap at 100
            for (int i = 0; i < repetitions; i++) {
                trainingText.append(ngram.getNgramText()).append(" ")
                        .append(ngram.getNextWordText()).append(" ");
            }
        }

        if (trainingText.length() > 0) {
            generator.train(trainingText.toString());
        }
    }

    /**
     * Gets N-gram statistics from the database.
     *
     * @param n the N value
     * @return statistics string
     * @throws SQLException if database error occurs
     */
    public String getNGramStatistics(int n) throws SQLException {
        int count = ngramDAO.countByN(n);
        return String.format("N-gram Statistics (N=%d):\n  Total N-grams in database: %d", n, count);
    }

    /**
     * Clears all N-grams for a specific N value from the database.
     *
     * @param n the N value
     * @throws SQLException if database error occurs
     */
    public void clearNGrams(int n) throws SQLException {
        ngramDAO.deleteByN(n);
    }

    /**
     * Gets all N-grams for a specific N value.
     *
     * @param n the N value
     * @param limit maximum number to return
     * @return list of N-grams
     * @throws SQLException if database error occurs
     */
    public List<NGram> getNGrams(int n, int limit) throws SQLException {
        return ngramDAO.findByN(n, limit);
    }
}
