package edu.utdallas.cs4485.sentencebuilder.service;

import java.sql.SQLException;
import java.util.List;

import edu.utdallas.cs4485.sentencebuilder.dao.DatabaseConnection;
import edu.utdallas.cs4485.sentencebuilder.dao.ImportedFileDAO;
import edu.utdallas.cs4485.sentencebuilder.dao.WordDAO;
import edu.utdallas.cs4485.sentencebuilder.dao.WordPairDAO;
import edu.utdallas.cs4485.sentencebuilder.model.ImportedFile;
import edu.utdallas.cs4485.sentencebuilder.model.Word;
import edu.utdallas.cs4485.sentencebuilder.model.WordPair;

/**
 * Service class for database operations. Coordinates between DAOs and provides
 * transaction management.
 *
 * Rizvy – Testing Notes:
 * - Verified DAO interactions over full UI workflow (import → parse → save).
 * - Confirmed that service methods expose clean API for controllers.
 * - Ensured safe fallback behavior for missing words or pairs.
 *
 * @author CS4485 Team
 * @version 1.0
 */
public class DatabaseService {

    private WordDAO wordDAO;
    private WordPairDAO wordPairDAO;
    private ImportedFileDAO importedFileDAO;
    private DatabaseConnection databaseConnection;

    /**
     * Constructor.
     * Rizvy – Testing:
     * - Ensured DAOs are instantiated once, preventing excess connections.
     * - Verified integration with UI: repeated inserts and lookups are stable.
     */
    public DatabaseService() {
        // TODO: Initialize DAOs and database connection
        this.databaseConnection = DatabaseConnection.getInstance();
        this.wordDAO = new WordDAO();
        this.wordPairDAO = new WordPairDAO();
        this.importedFileDAO = new ImportedFileDAO();
    }

    /**
     * Saves a word to the database.
     *
     * @param word the word to save
     * @return the saved word with updated ID
     * @throws SQLException if database error occurs
     */
    public Word saveWord(Word word) throws SQLException {
        // TODO: Implement word saving logic
        return wordDAO.insert(word);
    }

    /**
     * Saves a word pair to the database.
     *
     * @param wordPair the word pair to save
     * @return the saved word pair with updated ID
     * @throws SQLException if database error occurs
     */
    public WordPair saveWordPair(WordPair wordPair) throws SQLException {
        // TODO: Implement word pair saving logic
        return wordPairDAO.insert(wordPair);
    }

    /**
     * Saves an imported file record to the database.
     *
     * @param file the file record to save
     * @return the saved file with updated ID
     * @throws SQLException if database error occurs
     */
    public ImportedFile saveImportedFile(ImportedFile file) throws SQLException {
        // TODO: Implement file saving logic
        return importedFileDAO.insert(file);
    }

    /**
     * Gets all words from the database.
     *
     * @return list of all words
     * @throws SQLException if database error occurs
     */
    public List<Word> getAllWords() throws SQLException {
        // TODO: Implement word retrieval logic
        return wordDAO.findAll();
    }

    /**
     * Gets all word pairs from the database.
     *
     * @return list of all word pairs
     * @throws SQLException if database error occurs
     */
    public List<WordPair> getAllWordPairs() throws SQLException {
        // TODO: Implement word pair retrieval logic
        return wordPairDAO.findAll();
    }

    /**
     * Gets all imported files from the database.
     *
     * @return list of all imported files
     * @throws SQLException if database error occurs
     */
    public List<ImportedFile> getAllImportedFiles() throws SQLException {
        // TODO: Implement file retrieval logic
        return importedFileDAO.findAll();
    }

    /**
     * Finds a word by its text.
     *
     * @param wordText the word text to search for
     * @return the word, or null if not found
     * @throws SQLException if database error occurs
     */
    public Word findWordByText(String wordText) throws SQLException {
        // TODO: Implement word search logic
        return wordDAO.findByText(wordText);
    }

    /**
     * Gets word pairs for a given first word.
     *
     * @param firstWordId the ID of the first word
     * @return list of word pairs
     * @throws SQLException if database error occurs
     */
    public List<WordPair> getWordPairsByFirstWord(int firstWordId) throws SQLException {
        // TODO: Implement word pair search logic
        return wordPairDAO.findByFirstWordId(firstWordId);
    }

    /**
     * Updates word statistics.
     *
     * @param word the word to update
     * @throws SQLException if database error occurs
     */
    public void updateWord(Word word) throws SQLException {
        // TODO: Implement word update logic
        wordDAO.update(word);
    }

    /**
     * Updates word pair statistics.
     *
     * @param wordPair the word pair to update
     * @throws SQLException if database error occurs
     */
    public void updateWordPair(WordPair wordPair) throws SQLException {
        // TODO: Implement word pair update logic
        wordPairDAO.update(wordPair);
    }

    /**
     * Updates imported file status.
     *
     * @param file the file to update
     * @throws SQLException if database error occurs
     */
    public void updateImportedFile(ImportedFile file) throws SQLException {
        // TODO: Implement file update logic
        importedFileDAO.update(file);
    }

    /**
     * Deletes an imported file record from the database.
     *
     * Rizvy – Testing:
     * - Validated UI delete action removes entry without breaking word data.
     * - Ensured that shared word/pair statistics remain intact.
     *
     * This method will only remove the file record from the inported_files
     * table. Training data associated with this file (words, word pairs,
     * N-grams) will remain in the database to preserve data integrity, as they
     * may be shared with other imported files.
     *
     * @param fileId the ID of the file to delete
     * @throws SQLException if database error occurs
     */
    public void deleteImportedFile(int fileId) throws SQLException {
        // TODO: Implement file deletion logic
        importedFileDAO.delete(fileId);
    }

    /**
     * Recalculates transition probabilities for all word pairs.
     *
     * @throws SQLException if database error occurs
     */
    public void recalculateProbabilities() throws SQLException {
        // TODO: Implement probability recalculation logic
        wordPairDAO.recalculateProbabilities();
    }

    /**
     * Increments word count, handling both new and existing words.
     *
     * Rizvy Integration Testing:
     * - Confirmed that new words initialize properly.
     * - Ensured sentence start/end counters reflect parsing logic.
     *
     * @param wordText the word text
     * @param isStart true if this word starts a sentence
     * @param isEnd true if this word ends a sentence
     * @throws SQLException if database error occurs
     */
    public void incrementWordCount(String wordText, boolean isStart, boolean isEnd) throws SQLException {
        Word word = wordDAO.findByText(wordText);

        if (word == null) {
            // Create new word
            word = new Word();
            word.setWordText(wordText);
            word.setTotalCount(1);
            word.setSentenceStartCount(isStart ? 1 : 0);
            word.setSentenceEndCount(isEnd ? 1 : 0);
            wordDAO.insert(word);
        } else {
            // Update existing word
            word.setTotalCount(word.getTotalCount() + 1);
            if (isStart) {
                word.setSentenceStartCount(word.getSentenceStartCount() + 1);
            }
            if (isEnd) {
                word.setSentenceEndCount(word.getSentenceEndCount() + 1);
            }
            wordDAO.update(word);
        }
    }

    /**
     * Increments word pair transition count.
     *
     * Rizvy Testing:
     * - Confirmed new pairs initialize properly.
     * - Verified updates do not duplicate records.
     *
     * @param firstWordText the first word text
     * @param secondWordText the second word text
     * @throws SQLException if database error occurs
     */
    public void incrementWordPairCount(String firstWordText, String secondWordText) throws SQLException {
        // Get or create both words
        Word firstWord = wordDAO.findByText(firstWordText);
        if (firstWord == null) {
            firstWord = new Word();
            firstWord.setWordText(firstWordText);
            firstWord.setTotalCount(0);
            firstWord = wordDAO.insert(firstWord);
        }

        Word secondWord = wordDAO.findByText(secondWordText);
        if (secondWord == null) {
            secondWord = new Word();
            secondWord.setWordText(secondWordText);
            secondWord.setTotalCount(0);
            secondWord = wordDAO.insert(secondWord);
        }

        // Get or create word pair
        WordPair pair = wordPairDAO.findByWordIds(firstWord.getWordId(), secondWord.getWordId());
        if (pair == null) {
            pair = new WordPair();
            pair.setFirstWordId(firstWord.getWordId());
            pair.setSecondWordId(secondWord.getWordId());
            pair.setTransitionCount(1);
            wordPairDAO.insert(pair);
        } else {
            pair.setTransitionCount(pair.getTransitionCount() + 1);
            wordPairDAO.update(pair);
        }
    }

    /**
     * Closes database connections.
     * Rizvy – Final Testing:
     * - Confirmed shutdown does not interrupt UI session.
     * - Validated safe closing after imports and large dataset testing.
     */
    public void close() {
        // TODO: Implement cleanup logic
        try {
            if (databaseConnection != null) {
                databaseConnection.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
