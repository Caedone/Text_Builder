/******************************************************************************
 * Input Validation Utility
 *
 * This utility class provides static methods for validating user input and
 * data throughout the Sentence Builder application. It centralizes validation
 * logic to ensure consistency and reduce code duplication.
 *
 * The class provides several categories of validation:
 *
 * 1. String Validation:
 *    - Checks for null and empty strings (isNotEmpty)
 *    - Validates string length within specified bounds (isValidLength)
 *    - Verifies strings contain only alphanumeric characters (isAlphanumeric)
 *    - Ensures strings contain only letters for valid words (isValidWord)
 *    These validations are critical for processing text input from files and
 *    user-entered search queries and start words.
 *
 * 2. Numeric Range Validation:
 *    - Validates integers are within acceptable ranges (isInRange for int)
 *    - Validates doubles are within acceptable ranges (isInRange for double)
 *    These are used to validate slider values for N-gram sizes and word counts,
 *    ensuring parameters stay within algorithmically sound ranges.
 *
 * Purpose and Design Rationale:
 * By centralizing validation logic, we achieve several benefits:
 * - Consistency: All parts of the application use the same validation rules
 * - Maintainability: Changing a validation rule only requires updating one location
 * - Testability: Validation logic can be unit tested independently
 * - Readability: Controller and service code is cleaner with validation extracted
 *
 * All methods are static because this is a stateless utility class. Each method
 * is designed to be pure (no side effects) and returns boolean results that are
 * easy to use in conditional logic.
 *
 * The validation methods are defensive, handling null inputs gracefully by
 * returning false rather than throwing NullPointerExceptions. This makes the
 * calling code simpler and more robust.
 *
 * Written by Caedon Ewing for CS4485.0W1, capstone project, starting October 2025.
 *    NetID: CSE220000
 ******************************************************************************/
package edu.utdallas.cs4485.sentencebuilder.util;

/**
 * Utility class for input validation.
 *
 * @author CS4485 Team
 * @version 1.0
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