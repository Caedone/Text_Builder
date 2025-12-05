package edu.utdallas.cs4485.sentencebuilder.model;

/******************************************************************************
 * Generation Result Model
 *
 * This class is a simple data carrier used by the backend service layer and
 * the JavaFX UI to describe the result of a single text-generation request.
 * It bundles the generated text together with metadata about how it was
 * produced so the UI can display meaningful feedback to the user and the
 * system can log or analyze generation behavior later.
 *
 * The object tracks:
 *  - The full generated text
 *  - Which algorithm was used (e.g., first-order, second-order, N-gram)
 *  - The optional starting word supplied by the user
 *  - The requested / actual word count
 *  - How long the generation took in milliseconds
 *
 * By packaging all of this into one model instead of passing separate
 * parameters around, the service and controller code stays cleaner and it
 * is easier to extend the metadata in the future (for example, to add
 * temperature, random seed, or corpus information).
 *
 * Written by Johnathan Pedraza for CS4485.0W1, capstone project
 * "Sentence Builder / Babble", starting Oc 2025.
 * NetID: jxp220060
 ******************************************************************************/

/**
 * Represents the result of generating text from one of the algorithms.
 * Instances of this class are returned by the service layer and consumed
 * by the UI so that both the text and its metadata travel together.
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