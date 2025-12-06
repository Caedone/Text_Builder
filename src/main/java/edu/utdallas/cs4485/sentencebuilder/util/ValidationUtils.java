package edu.utdallas.cs4485.sentencebuilder.util;

/**
 *
 * Static utility class centralizing validation logic for user input and data
 * throughout the application to ensure consistency and reduce code duplication.
 *
 * Provides string validation (null/empty checks, length bounds, alphanumeric and
 * letter-only verification) critical for processing file text input, search queries,
 * and start words. Also offers numeric range validation for integers and doubles,
 * used to validate slider values for N-gram sizes and word counts within
 * algorithmically sound ranges.
 *
 * Centralizing validation achieves multiple benefits: consistent rules across all
 * components, easy maintainability with single-location updates, independent unit
 * testing, and cleaner controller/service code. All methods are stateless, pure
 * functions returning booleans for simple conditional usage.
 *
 * Validation methods handle null inputs gracefully by returning false rather than
 * throwing exceptions, making calling code simpler and more robust.
 *
 * @author Caedon Ewing
 */
public class ValidationUtils {

    /**
     * Validates if a string is not null and not empty.
     *
     * @param str the string to validate
     * @return true if valid
     */
    public static boolean isNotEmpty(String str) {
        // TODO: Implement string validation
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Validates if a string is within length limits.
     *
     * @param str the string to validate
     * @param minLength minimum length
     * @param maxLength maximum length
     * @return true if valid
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        // TODO: Implement length validation
        if (str == null) {
            return false;
        }
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validates if an integer is within a range.
     *
     * @param value the value to validate
     * @param min minimum value (inclusive)
     * @param max maximum value (inclusive)
     * @return true if valid
     */
    public static boolean isInRange(int value, int min, int max) {
        // TODO: Implement range validation
        return value >= min && value <= max;
    }

    /**
     * Validates if a double is within a range.
     *
     * @param value the value to validate
     * @param min minimum value (inclusive)
     * @param max maximum value (inclusive)
     * @return true if valid
     */
    public static boolean isInRange(double value, double min, double max) {
        // TODO: Implement range validation
        return value >= min && value <= max;
    }

    /**
     * Validates if a string contains only alphanumeric characters.
     *
     * @param str the string to validate
     * @return true if alphanumeric
     */
    public static boolean isAlphanumeric(String str) {
        // TODO: Implement alphanumeric validation
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("[a-zA-Z0-9]+");
    }

    /**
     * Validates if a string is a valid word (letters only).
     *
     * @param str the string to validate
     * @return true if valid word
     */
    public static boolean isValidWord(String str) {
        // TODO: Implement word validation
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("[a-zA-Z]+");
    }
}