/******************************************************************************
 * File Operations Utility
 *
 * This utility class provides static methods for common file operations and
 * validation used throughout the Sentence Builder application. It focuses
 * specifically on operations needed for text file import functionality.
 *
 * The class provides the following categories of functionality:
 *
 * 1. File Validation:
 *    - Checks if files exist, are readable, and are regular files (not directories)
 *    - Validates file sizes to prevent memory issues with extremely large files
 *    - Enforces a 50 MB maximum file size limit for imported text files
 *    This validation prevents the application from attempting to load files
 *    that could cause performance problems or out-of-memory errors.
 *
 * 2. Format Detection:
 *    - Extracts file extensions from filenames
 *    - Verifies that file formats are supported (txt, pdf, doc, docx)
 *    This ensures users only import compatible file types that the application
 *    can properly process for text extraction.
 *
 * 3. Filename Sanitization:
 *    - Removes invalid or potentially dangerous characters from filenames
 *    - Replaces problematic characters with underscores
 *    This is a security measure to prevent path traversal attacks and ensures
 *    filenames are safe to store in the database and display in the UI.
 *
 * All methods are static because this is a pure utility class with no instance
 * state. The class cannot be instantiated (though it lacks a private constructor
 * which would be a best practice enhancement).
 *
 * These utilities are primarily used by the FileImportController when users
 * select files to import, ensuring only valid, safe files are processed.
 *
 * Written by Caedon Ewing for CS4485.0W1, capstone project, starting October 2025.
 *    NetID: CSE220000
 ******************************************************************************/
package edu.utdallas.cs4485.sentencebuilder.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for file operations.
 *
 * @author CS4485 Team
 * @version 1.0
 */
public class FileUtils {

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50 MB

    /**
     * Validates if a file exists and is readable.
     *
     * @param filePath path to the file
     * @return true if file is valid
     */
    public static boolean isValidFile(Path filePath) {
        // TODO: Implement file validation
        return filePath != null &&
               Files.exists(filePath) &&
               Files.isRegularFile(filePath) &&
               Files.isReadable(filePath);
    }

    /**
     * Checks if a file size is within acceptable limits.
     *
     * @param filePath path to the file
     * @return true if file size is acceptable
     * @throws IOException if file size cannot be determined
     */
    public static boolean isAcceptableSize(Path filePath) throws IOException {
        // TODO: Implement size check
        long size = Files.size(filePath);
        return size > 0 && size <= MAX_FILE_SIZE;
    }

    /**
     * Gets the file extension.
     *
     * @param filename the filename
     * @return the extension without the dot, or empty string if none
     */
    public static String getFileExtension(String filename) {
        // TODO: Implement extension extraction
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot + 1).toLowerCase();
        }

        return "";
    }

    /**
     * Checks if a file format is supported.
     *
     * @param filename the filename
     * @return true if format is supported
     */
    public static boolean isSupportedFormat(String filename) {
        // TODO: Implement format check
        String extension = getFileExtension(filename);
        return extension.equals("txt") ||
               extension.equals("pdf") ||
               extension.equals("doc") ||
               extension.equals("docx");
    }

    /**
     * Sanitizes a filename by removing invalid characters.
     *
     * @param filename the filename to sanitize
     * @return sanitized filename
     */
    public static String sanitizeFilename(String filename) {
        // TODO: Implement filename sanitization
        if (filename == null) {
            return "";
        }

        // Remove invalid characters
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}