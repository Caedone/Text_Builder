package edu.utdallas.cs4485.sentencebuilder.model;

import java.sql.Timestamp;

/**
 * Model class representing a word in the database.
 * Stores word text and frequency statistics.
 *
 * @author CS4485 Team
 * @version 1.0
 */
public class Word {

    private int wordId;
    private String wordText;
    private int totalCount;
    private int sentenceStartCount;
    private int sentenceEndCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    /**
     * Default constructor.
     */
    public Word() {
    }

    /**
     * Constructor with word text.
     *
     * @param wordText the text of the word
     */
    public Word(String wordText) {
        this.wordText = wordText;
        this.totalCount = 0;
        this.sentenceStartCount = 0;
        this.sentenceEndCount = 0;
    }

    /**
     * Full constructor.
     *
     * @param wordId the unique identifier
     * @param wordText the text of the word
     * @param totalCount total occurrences
     * @param sentenceStartCount times word starts a sentence
     * @param sentenceEndCount times word ends a sentence
     */
    public Word(int wordId, String wordText, int totalCount,
                int sentenceStartCount, int sentenceEndCount) {
        this.wordId = wordId;
        this.wordText = wordText;
        this.totalCount = totalCount;
        this.sentenceStartCount = sentenceStartCount;
        this.sentenceEndCount = sentenceEndCount;
    }

    // Getters and Setters

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public String getWordText() {
        return wordText;
    }

    public void setWordText(String wordText) {
        this.wordText = wordText;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getSentenceStartCount() {
        return sentenceStartCount;
    }

    public void setSentenceStartCount(int sentenceStartCount) {
        this.sentenceStartCount = sentenceStartCount;
    }

    public int getSentenceEndCount() {
        return sentenceEndCount;
    }

    public void setSentenceEndCount(int sentenceEndCount) {
        this.sentenceEndCount = sentenceEndCount;
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

    /**
     * Increments the total count by one.
     */
    public void incrementTotalCount() {
        this.totalCount++;
    }

    /**
     * Increments the sentence start count by one.
     */
    public void incrementSentenceStartCount() {
        this.sentenceStartCount++;
    }

    /**
     * Increments the sentence end count by one.
     */
    public void incrementSentenceEndCount() {
        this.sentenceEndCount++;
    }

    @Override
    public String toString() {
        return "Word{" +
                "wordId=" + wordId +
                ", wordText='" + wordText + '\'' +
                ", totalCount=" + totalCount +
                ", sentenceStartCount=" + sentenceStartCount +
                ", sentenceEndCount=" + sentenceEndCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return wordText != null && wordText.equals(word.wordText);
    }

    @Override
    public int hashCode() {
        return wordText != null ? wordText.hashCode() : 0;
    }
}