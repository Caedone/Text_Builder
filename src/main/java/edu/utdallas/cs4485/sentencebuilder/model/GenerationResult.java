package edu.utdallas.cs4485.sentencebuilder.model;

/**
 * Model class representing the result of text generation.
 * Contains the generated text and metadata about the generation process.
 *
 * @author CS4485 Team
 * @version 1.0
 */
public class GenerationResult {

    private String generatedText;
    private String algorithm;
    private String startWord;
    private int wordCount;
    private long durationMs;

    /**
     * Default constructor.
     */
    public GenerationResult() {
    }

    /**
     * Full constructor.
     *
     * @param generatedText the generated text
     * @param algorithm the algorithm used (first-order or second-order)
     * @param startWord the starting word
     * @param wordCount number of words generated
     * @param durationMs generation time in milliseconds
     */
    public GenerationResult(String generatedText, String algorithm,
                           String startWord, int wordCount, long durationMs) {
        this.generatedText = generatedText;
        this.algorithm = algorithm;
        this.startWord = startWord;
        this.wordCount = wordCount;
        this.durationMs = durationMs;
    }

    // Getters and Setters

    public String getGeneratedText() {
        return generatedText;
    }

    public void setGeneratedText(String generatedText) {
        this.generatedText = generatedText;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getStartWord() {
        return startWord;
    }

    public void setStartWord(String startWord) {
        this.startWord = startWord;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    /**
     * Gets the duration in seconds.
     *
     * @return duration in seconds
     */
    public double getDurationSeconds() {
        return durationMs / 1000.0;
    }

    @Override
    public String toString() {
        return "GenerationResult{" +
                "algorithm='" + algorithm + '\'' +
                ", startWord='" + startWord + '\'' +
                ", wordCount=" + wordCount +
                ", durationMs=" + durationMs +
                ", generatedText='" +
                (generatedText != null && generatedText.length() > 50
                    ? generatedText.substring(0, 50) + "..."
                    : generatedText) + '\'' +
                '}';
    }
}