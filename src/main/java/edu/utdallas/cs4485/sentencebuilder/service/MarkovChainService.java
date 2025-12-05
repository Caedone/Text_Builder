package edu.utdallas.cs4485.sentencebuilder.service;

import edu.utdallas.cs4485.sentencebuilder.algorithm.MarkovChainGenerator;
import edu.utdallas.cs4485.sentencebuilder.dao.WordDAO;
import edu.utdallas.cs4485.sentencebuilder.dao.WordPairDAO;
import edu.utdallas.cs4485.sentencebuilder.model.GenerationResult;
import edu.utdallas.cs4485.sentencebuilder.model.Word;
import edu.utdallas.cs4485.sentencebuilder.model.WordPair;

import java.sql.SQLException;
import java.util.List;

/**
 * Service class for Markov chain text generation.
 * Coordinates between the algorithm and the UI/database layers.
 *
 * Rizvy – Integration & Testing Notes:
 * - Confirmed that both generators load database data only once for performance.
 * - Verified that UI generation calls behave consistently based on algorithm type.
 * - Validated safe handling of invalid or missing start words.
 *
 * @author CS4485 Team
 * @version 1.0
 */
public class MarkovChainService {

    private MarkovChainGenerator firstOrderGenerator;
    private MarkovChainGenerator secondOrderGenerator;
    private WordDAO wordDAO;
    private WordPairDAO wordPairDAO;
    private boolean dataLoaded = false; // Track if data has been loaded

    /**
     * Constructor.
     * Rizvy – Testing:
     * - Verified that both generators can be swapped based on UI selection (radio buttons, dropdowns).
     * - Confirmed that DAO instances do not recreate unnecessary connections.
     */
    public MarkovChainService() {
        this.firstOrderGenerator = new MarkovChainGenerator(true);
        this.secondOrderGenerator = new MarkovChainGenerator(false);
        this.wordDAO = new WordDAO();
        this.wordPairDAO = new WordPairDAO();
    }

    /**
     * Trains the Markov chain generators on text.
     *
     * @param text the training text
     * @param useSecondOrder whether to train second-order chain
     */
    public void train(String text, boolean useSecondOrder) {
        // TODO: Implement training logic
        // Train both generators (second-order includes first-order as fallback)
        if (useSecondOrder) {
            secondOrderGenerator.train(text);
        }
        firstOrderGenerator.train(text);
    }

    /**
     * Generates text using the specified algorithm.
     *
     * Rizvy – UI Validation:
     * - Confirmed duration and word count fields provide useful metadata for UI feedback.
     * - Verified clean exception messages when a start word is not found in DB.
     * - Ensured trained state is preserved between generation calls.
     *
     * @param startWord starting word (null for random)
     * @param maxWords maximum number of words
     * @param useSecondOrder true for second-order, false for first-order
     * @return generation result with text and metadata
     */
    public GenerationResult generateText(String startWord, int maxWords, boolean useSecondOrder) {
        // TODO: Implement text generation logic
        long startTime = System.currentTimeMillis();

        MarkovChainGenerator generator = useSecondOrder ? secondOrderGenerator : firstOrderGenerator;
        String algorithm = useSecondOrder ? "second-order" : "first-order";

        // Load data from database only once on first generation
        try {
            if (!dataLoaded) {
                loadDataFromDatabase(firstOrderGenerator, true);
                loadDataFromDatabase(secondOrderGenerator, false);
                dataLoaded = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load data from database: " + e.getMessage(), e);
        }

        // Check if start word exists in training data
        if (startWord != null && !startWord.trim().isEmpty()) {
            String normalizedWord = startWord.trim().toLowerCase();
            try {
                Word wordInDb = wordDAO.findByText(normalizedWord);
                if (wordInDb == null) {
                    throw new IllegalArgumentException("Start word '" + startWord + "' not found in database. Please choose a word from your imported text.");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error checking start word: " + e.getMessage(), e);
            }
        }

        String generatedText;
        if (startWord == null || startWord.trim().isEmpty()) {
            generatedText = generator.generateText(maxWords);
        } else {
            generatedText = generator.generateText(startWord, maxWords);
        }

        long duration = System.currentTimeMillis() - startTime;
        int wordCount = generatedText.isEmpty() ? 0 : generatedText.split("\\s+").length;

        return new GenerationResult(generatedText, algorithm, startWord, wordCount, duration);
    }

    /**
     * Loads word pairs from database and trains the generator.
     *
     * @param generator the generator to train
     * @param useSecondOrder whether to use second-order
     * @throws SQLException if database error occurs
     */
    private void loadDataFromDatabase(MarkovChainGenerator generator, boolean useSecondOrder) throws SQLException {
        // Get sentence starters (limit to 100 for faster loading)
        List<Word> sentenceStarters = wordDAO.findSentenceStarters(100);

        if (sentenceStarters.isEmpty()) {
            throw new RuntimeException("No training data found in database. Please import a text file first.");
        }

        // Build realistic training sentences by following word pair chains
        StringBuilder trainingText = new StringBuilder();

        for (Word starter : sentenceStarters) {
            try {
                String sentence = buildSentenceFromDatabase(starter.getWordText(), 30); // Reduced from 50
                if (!sentence.isEmpty()) {
                    trainingText.append(sentence).append(" ");
                }
            } catch (Exception e) {
                // Skip this starter if there's an issue
                continue;
            }
        }

        // Train the generator
        if (trainingText.length() > 0) {
            generator.train(trainingText.toString());
        } else {
            throw new RuntimeException("Failed to build training text from database.");
        }
    }

    /**
     * Builds a sentence by following word pair chains from database.
     *
     * @param startWord the word to start with
     * @param maxWords maximum words in sentence
     * @return constructed sentence
     * @throws SQLException if database error occurs
     */
    private String buildSentenceFromDatabase(String startWord, int maxWords) throws SQLException {
        StringBuilder sentence = new StringBuilder(startWord);
        String currentWord = startWord;
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < maxWords; i++) {
            // Get word object
            Word wordObj = wordDAO.findByText(currentWord);
            if (wordObj == null) break;

            // Get possible next words
            List<WordPair> nextPairs = wordPairDAO.findByFirstWordId(wordObj.getWordId());
            if (nextPairs.isEmpty()) break;

            // Select next word randomly based on transition counts (weighted random)
            int totalCount = nextPairs.stream().mapToInt(WordPair::getTransitionCount).sum();
            int randomValue = random.nextInt(totalCount);

            WordPair selectedPair = null;
            int cumulativeCount = 0;
            for (WordPair pair : nextPairs) {
                cumulativeCount += pair.getTransitionCount();
                if (randomValue < cumulativeCount) {
                    selectedPair = pair;
                    break;
                }
            }

            if (selectedPair == null) {
                selectedPair = nextPairs.get(0); // Fallback
            }

            String nextWord = selectedPair.getSecondWordText();
            if (nextWord == null) break;

            sentence.append(" ").append(nextWord);
            currentWord = nextWord;

            // Stop at sentence end
            if (nextWord.endsWith(".") || nextWord.endsWith("!") || nextWord.endsWith("?")) {
                break;
            }
        }

        return sentence.toString();
    }

    /**
     * Gets autocomplete suggestions.
     *
     * Rizvy – UI Testing:
     * - Verified suggestions update in real-time as user types.
     * - Confirmed that switching between order types changes suggestion patterns.
     *
     * @param context current word or words
     * @param maxSuggestions maximum number of suggestions
     * @param useSecondOrder true for second-order, false for first-order
     * @return list of suggested words
     */
    public List<String> getAutoCompleteSuggestions(String context, int maxSuggestions, boolean useSecondOrder) {
        // TODO: Implement autocomplete logic
        MarkovChainGenerator generator = useSecondOrder ? secondOrderGenerator : firstOrderGenerator;
        return generator.getAutoCompleteSuggestions(context, maxSuggestions);
    }

    /**
     * Checks if the generators have been trained.
     *
     * @param useSecondOrder which generator to check
     * @return true if trained
     */
    public boolean isTrained(boolean useSecondOrder) {
        MarkovChainGenerator generator = useSecondOrder ? secondOrderGenerator : firstOrderGenerator;
        return generator.isTrained();
    }

    /**
     * Gets the number of states in the chain.
     *
     * @param useSecondOrder which generator to check
     * @return number of states
     */
    public int getStateCount(boolean useSecondOrder) {
        MarkovChainGenerator generator = useSecondOrder ? secondOrderGenerator : firstOrderGenerator;
        return generator.getStateCount();
    }

    /**
     * Resets all trained data.
     *
     * Rizvy – Testing:
     * - Confirmed that resetting does not affect persisted DB data.
     *
     */
    public void reset() {
        // TODO: Implement reset logic
        this.firstOrderGenerator = new MarkovChainGenerator(true);
        this.secondOrderGenerator = new MarkovChainGenerator(false);
    }
}
