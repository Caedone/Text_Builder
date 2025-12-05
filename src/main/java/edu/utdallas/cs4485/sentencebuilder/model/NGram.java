/******************************************************************************
 * N-Gram Model Class
 *
 * This class represents an N-gram used in the Sentence Builder application for
 * probabilistic text generation. It stores the N-gram sequence, its component
 * words, and statistical data needed for modeling transitions in N-gram and
 * Markov-based algorithms.
 *
 * Key functionality includes:
 * 1. N-Gram Structure:
 *    - Stores the N value, full sequence text, and tokenized word array.
 *
 * 2. Transition Data:
 *    - Tracks the next word ID, transition count, and computed probability.
 *
 * 3. Database + UI Support:
 *    - Includes IDs, timestamps, and optional display fields, with automatic
 *      synchronization between ngramText and its word array.
 *
 * The class is used by the text-generation engine and database layer to build
 * conditional word prediction models.
 *
 * Written by Bhaskar Atmakuri for CS4485.0W1, capstone project, starting October 2025.
 * NetID: BXA210025
 ******************************************************************************/

package edu.utdallas.cs4485.sentencebuilder.model;

import java.sql.Timestamp;

/**
 * Model class representing an N-gram (sequence of N words) in the database.
 * Used for N-gram based text generation.
 *
 * @author CS4485 Team
 * @version 1.0
 */
public class NGram {

    private int ngramId;
    private int n; // The N in N-gram (2 for bigram, 3 for trigram, etc.)
    private String ngramText; // The complete N-gram sequence
    private String[] words; // Individual words in the N-gram
    private int nextWordId; // ID of the word that follows this N-gram
    private int transitionCount;
    private double transitionProbability;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Optional fields for displaying text (not stored in DB)
    private String nextWordText;

    /**
     * Default constructor.
     */
    public NGram() {
    }

    /**
     * Constructor with N-gram text and next word ID.
     *
     * @param n the N value (number of words in sequence)
     * @param ngramText the N-gram sequence
     * @param nextWordId the ID of the next word
     */
    public NGram(int n, String ngramText, int nextWordId) {
        this.n = n;
        this.ngramText = ngramText;
        this.nextWordId = nextWordId;
        this.transitionCount = 1;
        this.transitionProbability = 0.0;
        this.words = ngramText.split(" ");
    }

    /**
     * Full constructor.
     *
     * @param ngramId the unique identifier
     * @param n the N value
     * @param ngramText the N-gram sequence
     * @param nextWordId the ID of the next word
     * @param transitionCount number of times this transition occurs
     * @param transitionProbability calculated probability
     */
    public NGram(int ngramId, int n, String ngramText, int nextWordId,
                 int transitionCount, double transitionProbability) {
        this.ngramId = ngramId;
        this.n = n;
        this.ngramText = ngramText;
        this.nextWordId = nextWordId;
        this.transitionCount = transitionCount;
        this.transitionProbability = transitionProbability;
        this.words = ngramText.split(" ");
    }

    // Getters and Setters

    public int getNgramId() {
        return ngramId;
    }

    public void setNgramId(int ngramId) {
        this.ngramId = ngramId;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public String getNgramText() {
        return ngramText;
    }

    public void setNgramText(String ngramText) {
        this.ngramText = ngramText;
        this.words = ngramText != null ? ngramText.split(" ") : new String[0];
    }

    public String[] getWords() {
        return words;
    }

    public void setWords(String[] words) {
        this.words = words;
        this.ngramText = String.join(" ", words);
    }

    public int getNextWordId() {
        return nextWordId;
    }

    public void setNextWordId(int nextWordId) {
        this.nextWordId = nextWordId;
    }

    public int getTransitionCount() {
        return transitionCount;
    }

    public void setTransitionCount(int transitionCount) {
        this.transitionCount = transitionCount;
    }

    public double getTransitionProbability() {
        return transitionProbability;
    }

    public void setTransitionProbability(double transitionProbability) {
        this.transitionProbability = transitionProbability;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNextWordText() {
        return nextWordText;
    }

    public void setNextWordText(String nextWordText) {
        this.nextWordText = nextWordText;
    }

    /**
     * Increments the transition count by one.
     */
    public void incrementTransitionCount() {
        this.transitionCount++;
    }

    /**
     * Gets the last word from the N-gram sequence.
     *
     * @return the last word
     */
    public String getLastWord() {
        if (words != null && words.length > 0) {
            return words[words.length - 1];
        }
        return "";
    }

    /**
     * Gets the first word from the N-gram sequence.
     *
     * @return the first word
     */
    public String getFirstWord() {
        if (words != null && words.length > 0) {
            return words[0];
        }
        return "";
    }

    @Override
    public String toString() {
        return "NGram{" +
                "ngramId=" + ngramId +
                ", n=" + n +
                ", ngramText='" + ngramText + '\'' +
                ", nextWordId=" + nextWordId +
                ", transitionCount=" + transitionCount +
                ", transitionProbability=" + transitionProbability +
                ", nextWordText='" + nextWordText + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NGram nGram = (NGram) o;
        return n == nGram.n &&
               nextWordId == nGram.nextWordId &&
               ngramText.equals(nGram.ngramText);
    }

    @Override
    public int hashCode() {
        int result = n;
        result = 31 * result + ngramText.hashCode();
        result = 31 * result + nextWordId;
        return result;
    }
}