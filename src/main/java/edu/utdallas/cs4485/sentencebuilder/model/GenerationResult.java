package edu.utdallas.cs4485.sentencebuilder.model;

/**
 *
 * Data transfer object encapsulating the result of a text generation request
 * along with metadata about how the text was produced.
 *
 * This class bundles the generated text with contextual information including
 * the algorithm used (first-order Markov, second-order Markov, or N-gram),
 * optional starting word supplied by the user, actual word count produced,
 * and generation duration in milliseconds.
 *
 * By packaging all generation metadata into a single model, the service and
 * controller code remains clean and extensible. The JavaFX UI uses this to
 * display meaningful feedback to users, and the system can log or analyze
 * generation behavior. Future extensions might include temperature, random
 * seed, or corpus information.
 *
 * @author Johnathan Pedraza
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