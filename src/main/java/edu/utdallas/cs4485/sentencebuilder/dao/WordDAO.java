/**
 *  Written by Manraj Singh for CS Project, starting Oct 28, 2025.
 *  NetID: mxs220007
 */
package edu.utdallas.cs4485.sentencebuilder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.utdallas.cs4485.sentencebuilder.model.Word;

/**
 * Data Access Object for Word entities. Handles all database operations for
 * words.
 *
 * @author CS4485 Team
 * @version 1.0
 */
public class WordDAO {

    private DatabaseConnection dbConnection;

    /**
     * Constructor.
     */
    public WordDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Inserts a new word into the database.
     *
     * @param word the word to insert
     * @return the inserted word with updated ID
     * @throws SQLException if database error occurs
     */
    public Word insert(Word word) throws SQLException {
        // TODO: Implement word insertion
        String sql = "INSERT INTO words (word_text, total_count, sentence_start_count, sentence_end_count) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, word.getWordText());
            stmt.setInt(2, word.getTotalCount());
            stmt.setInt(3, word.getSentenceStartCount());
            stmt.setInt(4, word.getSentenceEndCount());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    word.setWordId(keys.getInt(1));
                }
            }
        }

        return word;
    }

    /**
     * Updates an existing word in the database.
     *
     * @param word the word to update
     * @throws SQLException if database error occurs
     */
    public void update(Word word) throws SQLException {
        // TODO: Implement word update
        String sql = "UPDATE words SET total_count = ?, sentence_start_count = ?, "
                + "sentence_end_count = ? WHERE word_id = ?";

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, word.getTotalCount());
            stmt.setInt(2, word.getSentenceStartCount());
            stmt.setInt(3, word.getSentenceEndCount());
            stmt.setInt(4, word.getWordId());

            stmt.executeUpdate();
        }
    }

    /**
     * Finds a word by its text.
     *
     * @param wordText the word text
     * @return the word, or null if not found
     * @throws SQLException if database error occurs
     */
    public Word findByText(String wordText) throws SQLException {
        // TODO: Implement word search by text
        String sql = "SELECT * FROM words WHERE word_text = ?";

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, wordText);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWord(rs);
                }
            }
        }

        return null;
    }

    /**
     * Finds a word by its ID.
     *
     * @param wordId the word ID
     * @return the word, or null if not found
     * @throws SQLException if database error occurs
     */
    public Word findById(int wordId) throws SQLException {
        // TODO: Implement word search by ID
        String sql = "SELECT * FROM words WHERE word_id = ?";

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, wordId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWord(rs);
                }
            }
        }

        return null;
    }

    /**
     * Finds all words in the database.
     *
     * @return list of all words
     * @throws SQLException if database error occurs
     */
    public List<Word> findAll() throws SQLException {
        // TODO: Implement find all words
        String sql = "SELECT * FROM words ORDER BY total_count DESC";
        List<Word> words = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                words.add(mapResultSetToWord(rs));
            }
        }

        return words;
    }

    /**
     * Gets words that can start sentences.
     *
     * @param limit maximum number of words to return
     * @return list of sentence starter words
     * @throws SQLException if database error occurs
     */
    public List<Word> findSentenceStarters(int limit) throws SQLException {
        // TODO: Implement sentence starters query
        String sql = "SELECT * FROM words WHERE sentence_start_count > 0 "
                + "ORDER BY sentence_start_count DESC LIMIT ?";
        List<Word> words = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    words.add(mapResultSetToWord(rs));
                }
            }
        }

        return words;
    }

    /**
     * Deletes a word from the database.
     *
     * @param wordId the ID of the word to delete
     * @throws SQLException if database error occurs
     */
    public void delete(int wordId) throws SQLException {
        // TODO: Implement word deletion
        String sql = "DELETE FROM words WHERE word_id = ?";

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, wordId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps a ResultSet row to a Word object.
     *
     * @param rs the ResultSet
     * @return the Word object
     * @throws SQLException if error occurs
     */
    private Word mapResultSetToWord(ResultSet rs) throws SQLException {
        Word word = new Word();
        word.setWordId(rs.getInt("word_id"));
        word.setWordText(rs.getString("word_text"));
        word.setTotalCount(rs.getInt("total_count"));
        word.setSentenceStartCount(rs.getInt("sentence_start_count"));
        word.setSentenceEndCount(rs.getInt("sentence_end_count"));
        word.setCreatedAt(rs.getTimestamp("created_at"));
        word.setUpdatedAt(rs.getTimestamp("updated_at"));
        return word;
    }
}
