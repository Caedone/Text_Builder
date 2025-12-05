package edu.utdallas.cs4485.sentencebuilder.model;

import java.sql.Timestamp;

/******************************************************************************
 * Imported File Model
 *
 * This class represents a single file that has been imported (or is in the
 * process of being imported) into the Sentence Builder system. It mirrors the
 * imported_files table in the database and is used by the backend services
 * and the UI to track progress and results of the import pipeline.
 *
 * Each ImportedFile instance captures:
 *  - A unique file ID from the database
 *  - The original filename and file system path
 *  - The total word count discovered during processing
 *  - The date/time the import was attempted
 *  - A status flag showing whether the import is pending, processing,
 *    completed, or failed
 *  - An optional error message when something goes wrong
 *
 * The status and helper methods (markAsProcessing, markAsCompleted,
 * markAsFailed) make it easy for the service layer to update progress in a
 * consistent way so the JavaFX UI can show accurate import history and
 * error information to the user.
 *
 * Written by Johnathan Pedraza for CS4485.0W1, capstone project
 * "Sentence Builder / Babble", starting October 2025.
 * NetID: jxp220060
 ******************************************************************************/

/**
 * Model class representing an imported file in the database.
 * Tracks file import history and status through the full pipeline.
 */

public class ImportedFile {

    private int fileId;
    private String filename;
    private String filePath;
    private int wordCount;
    private Timestamp importDate;
    private FileStatus status;
    private String errorMessage;

    /**
     * Enum for file import status.
     */
    public enum FileStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }

    /**
     * Default constructor.
     */
    public ImportedFile() {
        this.status = FileStatus.PENDING;
        this.wordCount = 0;
    }

    /**
     * Constructor with filename and path.
     *
     * @param filename the name of the file
     * @param filePath the full path to the file
     */
    public ImportedFile(String filename, String filePath) {
        this.filename = filename;
        this.filePath = filePath;
        this.status = FileStatus.PENDING;
        this.wordCount = 0;
    }

    /**
     * Full constructor.
     *
     * @param fileId the unique identifier
     * @param filename the name of the file
     * @param filePath the full path to the file
     * @param wordCount number of words processed
     * @param importDate when the file was imported
     * @param status the import status
     */
    public ImportedFile(int fileId, String filename, String filePath,
                       int wordCount, Timestamp importDate, FileStatus status) {
        this.fileId = fileId;
        this.filename = filename;
        this.filePath = filePath;
        this.wordCount = wordCount;
        this.importDate = importDate;
        this.status = status;
    }

    // Getters and Setters

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public Timestamp getImportDate() {
        return importDate;
    }

    public void setImportDate(Timestamp importDate) {
        this.importDate = importDate;
    }

    public FileStatus getStatus() {
        return status;
    }

    public void setStatus(FileStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Checks if the file import is complete.
     *
     * @return true if status is COMPLETED
     */
    public boolean isCompleted() {
        return status == FileStatus.COMPLETED;
    }

    /**
     * Checks if the file import failed.
     *
     * @return true if status is FAILED
     */
    public boolean isFailed() {
        return status == FileStatus.FAILED;
    }

    /**
     * Marks the file as processing.
     */
    public void markAsProcessing() {
        this.status = FileStatus.PROCESSING;
    }

    /**
     * Marks the file as completed.
     *
     * @param wordCount the final word count
     */
    public void markAsCompleted(int wordCount) {
        this.status = FileStatus.COMPLETED;
        this.wordCount = wordCount;
    }

    /**
     * Marks the file as failed.
     *
     * @param errorMessage the error message
     */
    public void markAsFailed(String errorMessage) {
        this.status = FileStatus.FAILED;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ImportedFile{" +
                "fileId=" + fileId +
                ", filename='" + filename + '\'' +
                ", filePath='" + filePath + '\'' +
                ", wordCount=" + wordCount +
                ", importDate=" + importDate +
                ", status=" + status +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}