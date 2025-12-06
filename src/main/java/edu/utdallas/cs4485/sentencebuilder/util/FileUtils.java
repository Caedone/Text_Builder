package edu.utdallas.cs4485.sentencebuilder.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * Static utility class providing file operations and validation for text file
 * import functionality throughout the application.
 *
 * Offers three main categories of functionality: file validation (existence, readability,
 * regular file checks, and size limits with 50 MB maximum to prevent memory issues),
 * format detection (extension extraction and supported format verification for txt, pdf,
 * doc, docx files), and filename sanitization (removing dangerous characters and
 * preventing path traversal attacks).
 *
 * All methods are stateless and static, designed as pure utility functions with no
 * instance state. Primarily used by FileImportController during the file selection
 * process to ensure only valid, safe, and compatible files are processed for text
 * extraction and analysis.
 *
 * Enforces security and performance constraints to protect against malicious input
 * and resource exhaustion while maintaining a clean, safe database and UI.
 *
 * @author Caedon Ewing
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