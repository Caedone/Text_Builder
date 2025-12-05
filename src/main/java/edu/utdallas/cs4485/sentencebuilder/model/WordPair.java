/******************************************************************************
 * WordPair Model Class
 *
 * This class represents a word-to-word transition used in Markov chain text
 * generation. It stores the IDs of two consecutive words along with frequency
 * and probability data that describe how often one word follows another.
 *
 * Key functionality includes:
 * 1. Transition Data:
 *    - Tracks transition counts and calculated probabilities.
 *
 * 2. Database + UI Support:
 *    - Includes IDs, timestamps, and optional word text for display.
 *
 * Used by the Markov generator to build first-order transition models.
 *
 * Written by Bhaskar Atmakuri for CS4485.0W1, capstone project, starting October 2025.
 * NetID: BXA210025
 ******************************************************************************/

package edu.utdallas.cs4485.sentencebuilder.model;

import java.sql.Timestamp;

/**
 * Model class representing a word pair (transition) in the database.
 * Used for Markov chain generation.
 *
 * @author CS4485 Team
 * @version 1.0
 */
public class WordPair {

    private int pairId;
    private int firstWordId;
    private int secondWordId;
    private int transitionCount;
    private double transitionProbability;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Optional fields for displaying word text (not stored in DB)
    private String firstWordText;
    private String secondWordText;

    /**
     * Default constructor.
     */
    public WordPair() {
    }

    /**
     * Constructor with word IDs.
     *
     * @param firstWordId the ID of the first word
     * @param secondWordId the ID of the second word
     */
    public WordPair(int firstWordId, int secondWordId) {
        this.firstWordId = firstWordId;
        this.secondWordId = secondWordId;
        this.transitionCount = 1;
        this.transitionProbability = 0.0;
    }

    /**
     * Full constructor.
     *
     * @param pairId the unique identifier
     * @param firstWordId the ID of the first word
     * @param secondWordId the ID of the second word
     * @param transitionCount number of times this transition occurs
     * @param transitionProbability calculated probability
     */
    public WordPair(int pairId, int firstWordId, int secondWordId,
                    int transitionCount, double transitionProbability) {
        this.pairId = pairId;
        this.firstWordId = firstWordId;
        this.secondWordId = secondWordId;
        this.transitionCount = transitionCount;
        this.transitionProbability = transitionProbability;
    }

    // Getters and Setters

    public int getPairId() {
        return pairId;
    }

    public void setPairId(int pairId) {
        this.pairId = pairId;
    }

    public int getFirstWordId() {
        return firstWordId;
    }

    public void setFirstWordId(int firstWordId) {
        this.firstWordId = firstWordId;
    }

    public int getSecondWordId() {
        return secondWordId;
    }

    public void setSecondWordId(int secondWordId) {
        this.secondWordId = secondWordId;
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

    public String getFirstWordText() {
        return firstWordText;
    }

    public void setFirstWordText(String firstWordText) {
        this.firstWordText = firstWordText;
    }

    public String getSecondWordText() {
        return secondWordText;
    }

    public void setSecondWordText(String secondWordText) {
        this.secondWordText = secondWordText;
    }

    /**
     * Increments the transition count by one.
     */
    public void incrementTransitionCount() {
        this.transitionCount++;
    }

    @Override
    public String toString() {
        return "WordPair{" +
                "pairId=" + pairId +
                ", firstWordId=" + firstWordId +
                ", secondWordId=" + secondWordId +
                ", transitionCount=" + transitionCount +
                ", transitionProbability=" + transitionProbability +
                ", firstWordText='" + firstWordText + '\'' +
                ", secondWordText='" + secondWordText + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordPair wordPair = (WordPair) o;
        return firstWordId == wordPair.firstWordId && secondWordId == wordPair.secondWordId;
    }

    @Override
    public int hashCode() {
        return 31 * firstWordId + secondWordId;
    }
}