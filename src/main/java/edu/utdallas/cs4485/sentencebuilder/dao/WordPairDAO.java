package edu.utdallas.cs4485.sentencebuilder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.utdallas.cs4485.sentencebuilder.model.WordPair;

/**
 *
 * Data Access Object for WordPair entities. Manages storage and retrieval of
 * word transition relationships used in Markov chain text generation.
 *
 * Word pairs represent sequential word relationships extracted from source texts,
 * storing transition counts and calculated probabilities. These relationships form
 * the foundation of first-order and second-order Markov chain algorithms.
 *
 * Supports operations for inserting new pairs, updating transition statistics,
 * querying pairs by various criteria, and calculating transition probabilities
 * for text generation decisions.
 *
 * @author Johnathan Pedraza
 * @author Rahman-Danish, Rizvy
 */
public class WordPairDAO {

    private DatabaseConnection dbConnection;

    /**
     * Constructor.
     */
    public WordPairDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Inserts a new word pair into the database.
     *
     * @param wordPair the word pair to insert
     * @return the inserted word pair with updated ID
     * @throws SQLException if database error occurs
     */
    public WordPair insert(WordPair wordPair) throws SQLException {
        // TODO: Implement word pair insertion
        String sql = "INSERT INTO word_pairs (first_word_id, second_word_id, transition_count, transition_probability) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, wordPair.getFirstWordId());
            stmt.setInt(2, wordPair.getSecondWordId());
            stmt.setInt(3, wordPair.getTransitionCount());
            stmt.setDouble(4, wordPair.getTransitionProbability());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    wordPair.setPairId(keys.getInt(1));
                }
            }
        }

        return wordPair;
    }

    /**
     * Updates an existing word pair in the database.
     *
     * @param wordPair the word pair to update
     * @throws SQLException if database error occurs
     */
    public void update(WordPair wordPair) throws SQLException {
        // TODO: Implement word pair update
        String sql = "UPDATE word_pairs SET transition_count = ?, transition_probability = ? "
                + "WHERE pair_id = ?";

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, wordPair.getTransitionCount());
            stmt.setDouble(2, wordPair.getTransitionProbability());
            stmt.setInt(3, wordPair.getPairId());

            stmt.executeUpdate();
        }
    }

    /**
     * Finds a word pair by first and second word IDs.
     *
     * @param firstWordId the first word ID
     * @param secondWordId the second word ID
     * @return the word pair, or null if not found
     * @throws SQLException if database error occurs
     */
    public WordPair findByWordIds(int firstWordId, int secondWordId) throws SQLException {
        // TODO: Implement word pair search
        String sql = "SELECT * FROM word_pairs WHERE first_word_id = ? AND second_word_id = ?";

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, firstWordId);
            stmt.setInt(2, secondWordId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWordPair(rs);
                }
            }
        }

        return null;
    }

    /**
     * Finds all word pairs for a given first word.
     *
     * @param firstWordId the first word ID
     * @return list of word pairs
     * @throws SQLException if database error occurs
     */
    public List<WordPair> findByFirstWordId(int firstWordId) throws SQLException {
        // TODO: Implement word pair search by first word
        String sql = "SELECT wp.*, w1.word_text as first_word_text, w2.word_text as second_word_text "
                + "FROM word_pairs wp "
                + "JOIN words w1 ON wp.first_word_id = w1.word_id "
                + "JOIN words w2 ON wp.second_word_id = w2.word_id "
                + "WHERE wp.first_word_id = ? "
                + "ORDER BY wp.transition_probability DESC";

        List<WordPair> wordPairs = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, firstWordId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    WordPair wordPair = mapResultSetToWordPair(rs);
                    wordPair.setFirstWordText(rs.getString("first_word_text"));
                    wordPair.setSecondWordText(rs.getString("second_word_text"));
                    wordPairs.add(wordPair);
                }
            }
        }

        return wordPairs;
    }

    /**
     * Finds all word pairs in the database.
     *
     * @return list of all word pairs
     * @throws SQLException if database error occurs
     */
    public List<WordPair> findAll() throws SQLException {
        // TODO: Implement find all word pairs
        String sql = "SELECT wp.*, w1.word_text as first_word_text, w2.word_text as second_word_text "
                + "FROM word_pairs wp "
                + "JOIN words w1 ON wp.first_word_id = w1.word_id "
                + "JOIN words w2 ON wp.second_word_id = w2.word_id "
                + "ORDER BY wp.transition_count DESC";

        List<WordPair> wordPairs = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                WordPair wordPair = mapResultSetToWordPair(rs);
                wordPair.setFirstWordText(rs.getString("first_word_text"));
                wordPair.setSecondWordText(rs.getString("second_word_text"));
                wordPairs.add(wordPair);
            }
        }

        return wordPairs;
    }

    /**
     * Recalculates transition probabilities for all word pairs.
     *
     * @throws SQLException if database error occurs
     */
    public void recalculateProbabilities() throws SQLException {
        // TODO: Implement probability recalculation
        String sql = "UPDATE word_pairs wp "
                + "JOIN (SELECT first_word_id, SUM(transition_count) as total "
                + "      FROM word_pairs GROUP BY first_word_id) totals "
                + "ON wp.first_word_id = totals.first_word_id "
                + "SET wp.transition_probability = wp.transition_count / totals.total";

        try (Connection conn = dbConnection.getConnection(); Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
        }
    }

    /**
     * Deletes a word pair from the database.
     *
     * @param pairId the ID of the word pair to delete
     * @throws SQLException if database error occurs
     */
    public void delete(int pairId) throws SQLException {
        // TODO: Implement word pair deletion
        String sql = "DELETE FROM word_pairs WHERE pair_id = ?";

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pairId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps a ResultSet row to a WordPair object.
     *
     * @param rs the ResultSet
     * @return the WordPair object
     * @throws SQLException if error occurs
     */
    private WordPair mapResultSetToWordPair(ResultSet rs) throws SQLException {
        WordPair wordPair = new WordPair();
        wordPair.setPairId(rs.getInt("pair_id"));
        wordPair.setFirstWordId(rs.getInt("first_word_id"));
        wordPair.setSecondWordId(rs.getInt("second_word_id"));
        wordPair.setTransitionCount(rs.getInt("transition_count"));
        wordPair.setTransitionProbability(rs.getDouble("transition_probability"));
        wordPair.setCreatedAt(rs.getTimestamp("created_at"));
        wordPair.setUpdatedAt(rs.getTimestamp("updated_at"));
        return wordPair;
    }
}
