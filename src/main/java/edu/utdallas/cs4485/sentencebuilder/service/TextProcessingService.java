package edu.utdallas.cs4485.sentencebuilder.service;

import edu.utdallas.cs4485.sentencebuilder.algorithm.TextTokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Service class for text processing operations.
 * Handles file reading, tokenization, and text normalization.
 *
 * @author CS4485 Team
 * @version 1.0
 */
public class TextProcessingService {

    /**
     * Reads text from a file.
     *
     * @param filePath path to the file
     * @return file contents as string
     * @throws IOException if file cannot be read
     */
    public String readTextFromFile(Path filePath) throws IOException {
        // TODO: Implement file reading logic with error handling
        return Files.readString(filePath);
    }

    /**
     * Tokenizes text into words.
     *
     * @param text the text to tokenize
     * @return list of words
     */
    public List<String> tokenizeWords(String text) {
        // TODO: Implement word tokenization
        return TextTokenizer.tokenizeWords(text);
    }

    /**
     * Tokenizes text into sentences.
     *
     * @param text the text to tokenize
     * @return list of sentences
     */
    public List<String> tokenizeSentences(String text) {
        // TODO: Implement sentence tokenization
        return TextTokenizer.tokenizeSentences(text);
    }

    /**
     * Normalizes text (removes extra whitespace, converts to lowercase).
     *
     * @param text the text to normalize
     * @return normalized text
     */
    public String normalizeText(String text) {
        // TODO: Implement text normalization
        return TextTokenizer.normalizeText(text);
    }

    /**
     * Counts words in text.
     *
     * @param text the text to analyze
     * @return word count
     */
    public int countWords(String text) {
        // TODO: Implement word counting
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        List<String> words = TextTokenizer.tokenizeWords(text);
        return (int) words.stream()
                .filter(TextTokenizer::isValidWord)
                .count();
    }

    /**
     * Validates file format.
     *
     * @param filename the filename to check
     * @return true if supported format
     */
    public boolean isSupportedFileFormat(String filename) {
        // TODO: Implement format validation
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }

        String lowerName = filename.toLowerCase();
        return lowerName.endsWith(".txt") ||
               lowerName.endsWith(".doc") ||
               lowerName.endsWith(".docx") ||
               lowerName.endsWith(".pdf");
    }

    /**
     * Extracts plain text from various file formats.
     *
     * @param filePath path to the file
     * @return extracted text
     * @throws IOException if file cannot be processed
     */
    public String extractText(Path filePath) throws IOException {
        // TODO: Implement text extraction for different formats
        // For now, just handle plain text
        String filename = filePath.getFileName().toString().toLowerCase();

        if (filename.endsWith(".txt")) {
            return readTextFromFile(filePath);
        } else if (filename.endsWith(".pdf")) {
            // TODO: Implement PDF text extraction
            throw new UnsupportedOperationException("PDF extraction not yet implemented");
        } else if (filename.endsWith(".doc") || filename.endsWith(".docx")) {
            // TODO: Implement Word document text extraction
            throw new UnsupportedOperationException("Word document extraction not yet implemented");
        } else {
            throw new IOException("Unsupported file format: " + filename);
        }
    }

    /**
     * Cleans text by removing non-printable characters and normalizing whitespace.
     *
     * @param text the text to clean
     * @return cleaned text
     */
    public String cleanText(String text) {
        // TODO: Implement text cleaning
        if (text == null) {
            return "";
        }

        // Remove non-printable characters
        text = text.replaceAll("\\p{C}", " ");

        // Normalize whitespace
        text = text.replaceAll("\\s+", " ");

        return text.trim();
    }
}