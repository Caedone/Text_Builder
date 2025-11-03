package edu.utdallas.cs4485.sentencebuilder.dao;

import edu.utdallas.cs4485.sentencebuilder.model.NGram;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for NGram entities.
 * Handles all database operations for N-grams.
 *
 * @author CS4485 Team
 * @version 1.0
 */
public class NGramDAO {

    private DatabaseConnection dbConnection;

    /**
     * Constructor.
     */
    public NGramDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Inserts a new N-gram into the database.
     *
     * @param ngram the N-gram to insert
     * @return the inserted N-gram with updated ID
     * @throws SQLException if database error occurs
     */
    public NGram insert(NGram ngram) throws SQLException {
        String sql = "INSERT INTO ngrams (n, ngram_text, next_word_id, transition_count, transition_probability) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, ngram.getN());
            stmt.setString(2, ngram.getNgramText());
            stmt.setInt(3, ngram.getNextWordId());
            stmt.setInt(4, ngram.getTransitionCount());
            stmt.setDouble(5, ngram.getTransitionProbability());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    ngram.setNgramId(keys.getInt(1));
                }
            }
        }

        return ngram;
    }

    /**
     * Updates an existing N-gram in the database.
     *
     * @param ngram the N-gram to update
     * @throws SQLException if database error occurs
     */
    public void update(NGram ngram) throws SQLException {
        String sql = "UPDATE ngrams SET transition_count = ?, transition_probability = ? " +
                     "WHERE ngram_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ngram.getTransitionCount());
            stmt.setDouble(2, ngram.getTransitionProbability());
            stmt.setInt(3, ngram.getNgramId());

            stmt.executeUpdate();
        }
    }

    /**
     * Finds an N-gram by its text and next word ID.
     *
     * @param n the N value
     * @param ngramText the N-gram text
     * @param nextWordId the next word ID
     * @return the N-gram, or null if not found
     * @throws SQLException if database error occurs
     */
    public NGram findByTextAndNextWord(int n, String ngramText, int nextWordId) throws SQLException {
        String sql = "SELECT * FROM ngrams WHERE n = ? AND ngram_text = ? AND next_word_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, n);
            stmt.setString(2, ngramText);
            stmt.setInt(3, nextWordId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNGram(rs);
                }
            }
        }

        return null;
    }

    /**
     * Finds all N-grams matching a given text sequence.
     *
     * @param n the N value
     * @param ngramText the N-gram text
     * @return list of N-grams
     * @throws SQLException if database error occurs
     */
    public List<NGram> findByNgramText(int n, String ngramText) throws SQLException {
        String sql = "SELECT ng.*, w.word_text as next_word_text " +
                     "FROM ngrams ng " +
                     "JOIN words w ON ng.next_word_id = w.word_id " +
                     "WHERE ng.n = ? AND ng.ngram_text = ? " +
                     "ORDER BY ng.transition_probability DESC";

        List<NGram> ngrams = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, n);
            stmt.setString(2, ngramText);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    NGram ngram = mapResultSetToNGram(rs);
                    ngram.setNextWordText(rs.getString("next_word_text"));
                    ngrams.add(ngram);
                }
            }
        }

        return ngrams;
    }

    /**
     * Finds all N-grams for a given N value.
     *
     * @param n the N value
     * @param limit maximum number of results
     * @return list of N-grams
     * @throws SQLException if database error occurs
     */
    public List<NGram> findByN(int n, int limit) throws SQLException {
        String sql = "SELECT ng.*, w.word_text as next_word_text " +
                     "FROM ngrams ng " +
                     "JOIN words w ON ng.next_word_id = w.word_id " +
                     "WHERE ng.n = ? " +
                     "ORDER BY ng.transition_count DESC " +
                     "LIMIT ?";

        List<NGram> ngrams = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, n);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    NGram ngram = mapResultSetToNGram(rs);
                    ngram.setNextWordText(rs.getString("next_word_text"));
                    ngrams.add(ngram);
                }
            }
        }

        return ngrams;
    }

    /**
     * Finds all N-grams in the database.
     *
     * @return list of all N-grams
     * @throws SQLException if database error occurs
     */
    public List<NGram> findAll() throws SQLException {
        String sql = "SELECT ng.*, w.word_text as next_word_text " +
                     "FROM ngrams ng " +
                     "JOIN words w ON ng.next_word_id = w.word_id " +
                     "ORDER BY ng.n, ng.transition_count DESC";

        List<NGram> ngrams = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                NGram ngram = mapResultSetToNGram(rs);
                ngram.setNextWordText(rs.getString("next_word_text"));
                ngrams.add(ngram);
            }
        }

        return ngrams;
    }

    /**
     * Recalculates transition probabilities for all N-grams.
     *
     * @throws SQLException if database error occurs
     */
    public void recalculateProbabilities() throws SQLException {
        String sql = "UPDATE ngrams ng " +
                     "JOIN (SELECT n, ngram_text, SUM(transition_count) as total " +
                     "      FROM ngrams GROUP BY n, ngram_text) totals " +
                     "ON ng.n = totals.n AND ng.ngram_text = totals.ngram_text " +
                     "SET ng.transition_probability = ng.transition_count / totals.total";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
        }
    }

    /**
     * Deletes an N-gram from the database.
     *
     * @param ngramId the ID of the N-gram to delete
     * @throws SQLException if database error occurs
     */
    public void delete(int ngramId) throws SQLException {
        String sql = "DELETE FROM ngrams WHERE ngram_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ngramId);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes all N-grams for a given N value.
     *
     * @param n the N value
     * @throws SQLException if database error occurs
     */
    public void deleteByN(int n) throws SQLException {
        String sql = "DELETE FROM ngrams WHERE n = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, n);
            stmt.executeUpdate();
        }
    }

    /**
     * Gets the count of N-grams for a specific N value.
     *
     * @param n the N value
     * @return count of N-grams
     * @throws SQLException if database error occurs
     */
    public int countByN(int n) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ngrams WHERE n = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, n);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    /**
     * Maps a ResultSet row to an NGram object.
     *
     * @param rs the ResultSet
     * @return the NGram object
     * @throws SQLException if error occurs
     */
    private NGram mapResultSetToNGram(ResultSet rs) throws SQLException {
        NGram ngram = new NGram();
        ngram.setNgramId(rs.getInt("ngram_id"));
        ngram.setN(rs.getInt("n"));
        ngram.setNgramText(rs.getString("ngram_text"));
        ngram.setNextWordId(rs.getInt("next_word_id"));
        ngram.setTransitionCount(rs.getInt("transition_count"));
        ngram.setTransitionProbability(rs.getDouble("transition_probability"));
        ngram.setCreatedAt(rs.getTimestamp("created_at"));
        ngram.setUpdatedAt(rs.getTimestamp("updated_at"));
        return ngram;
    }
}
