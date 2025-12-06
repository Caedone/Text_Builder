package edu.utdallas.cs4485.sentencebuilder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.utdallas.cs4485.sentencebuilder.model.ImportedFile;
import edu.utdallas.cs4485.sentencebuilder.model.ImportedFile.FileStatus;

/**
 *
 * Data Access Object for ImportedFile entities. Tracks metadata and processing
 * status of text files imported into the system for analysis.
 *
 * Maintains records of all imported files including file paths, processing status,
 * word counts, and any error messages encountered during import. This enables the
 * system to track import history, avoid duplicate processing, and display import
 * status to users.
 *
 * Supports querying files by status, retrieving import history, and updating
 * processing results as files are analyzed and loaded into the database.
 *
 * @author Manraj Singh
 */
public class ImportedFileDAO {

    private DatabaseConnection dbConnection;

    /**
     * Constructor.
     */
    public ImportedFileDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Inserts a new imported file record into the database.
     *
     * @param file the file to insert
     * @return the inserted file with updated ID
     * @throws SQLException if database error occurs
     */
    public ImportedFile insert(ImportedFile file) throws SQLException {
        // TODO: Implement file insertion
        String sql = "INSERT INTO imported_files (filename, file_path, word_count, status, error_message) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, file.getFilename());
            stmt.setString(2, file.getFilePath());
            stmt.setInt(3, file.getWordCount());
            stmt.setString(4, file.getStatus().name().toLowerCase());
            stmt.setString(5, file.getErrorMessage());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    file.setFileId(keys.getInt(1));
                }
            }
        }

        return file;
    }

    /**
     * Updates an existing imported file record.
     *
     * @param file the file to update
     * @throws SQLException if database error occurs
     */
    public void update(ImportedFile file) throws SQLException {
        // TODO: Implement file update
        String sql = "UPDATE imported_files SET word_count = ?, status = ?, error_message = ? "
                + "WHERE file_id = ?";

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, file.getWordCount());
            stmt.setString(2, file.getStatus().name().toLowerCase());
            stmt.setString(3, file.getErrorMessage());
            stmt.setInt(4, file.getFileId());

            stmt.executeUpdate();
        }
    }

    /**
     * Finds an imported file by its ID.
     *
     * @param fileId the file ID
     * @return the file, or null if not found
     * @throws SQLException if database error occurs
     */
    public ImportedFile findById(int fileId) throws SQLException {
        // TODO: Implement file search by ID
        String sql = "SELECT * FROM imported_files WHERE file_id = ?";

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, fileId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToImportedFile(rs);
                }
            }
        }

        return null;
    }

    /**
     * Finds an imported file by its path.
     *
     * @param filePath the file path
     * @return the file, or null if not found
     * @throws SQLException if database error occurs
     */
    public ImportedFile findByPath(String filePath) throws SQLException {
        // TODO: Implement file search by path
        String sql = "SELECT * FROM imported_files WHERE file_path = ?";

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, filePath);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToImportedFile(rs);
                }
            }
        }

        return null;
    }

    /**
     * Finds all imported files.
     *
     * @return list of all imported files
     * @throws SQLException if database error occurs
     */
    public List<ImportedFile> findAll() throws SQLException {
        // TODO: Implement find all files
        String sql = "SELECT * FROM imported_files ORDER BY import_date DESC";
        List<ImportedFile> files = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                files.add(mapResultSetToImportedFile(rs));
            }
        }

        return files;
    }

    /**
     * Finds imported files by status.
     *
     * @param status the file status
     * @return list of files with the given status
     * @throws SQLException if database error occurs
     */
    public List<ImportedFile> findByStatus(FileStatus status) throws SQLException {
        // TODO: Implement file search by status
        String sql = "SELECT * FROM imported_files WHERE status = ? ORDER BY import_date DESC";
        List<ImportedFile> files = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name().toLowerCase());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    files.add(mapResultSetToImportedFile(rs));
                }
            }
        }

        return files;
    }

    /**
     * Deletes an imported file record.
     *
     * @param fileId the ID of the file to delete
     * @throws SQLException if database error occurs
     */
    public void delete(int fileId) throws SQLException {
        // TODO: Implement file deletion
        String sql = "DELETE FROM imported_files WHERE file_id = ?";

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, fileId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps a ResultSet row to an ImportedFile object.
     *
     * @param rs the ResultSet
     * @return the ImportedFile object
     * @throws SQLException if error occurs
     */
    private ImportedFile mapResultSetToImportedFile(ResultSet rs) throws SQLException {
        ImportedFile file = new ImportedFile();
        file.setFileId(rs.getInt("file_id"));
        file.setFilename(rs.getString("filename"));
        file.setFilePath(rs.getString("file_path"));
        file.setWordCount(rs.getInt("word_count"));
        file.setImportDate(rs.getTimestamp("import_date"));
        file.setStatus(FileStatus.valueOf(rs.getString("status").toUpperCase()));
        file.setErrorMessage(rs.getString("error_message"));
        return file;
    }
}
