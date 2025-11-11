/**
 *  Written by Manraj Singh for CS Project, starting Oct 28, 2025.
 *  NetID: mxs220007
 */
package edu.utdallas.cs4485.sentencebuilder.controller;

import edu.utdallas.cs4485.sentencebuilder.model.GenerationResult;
import edu.utdallas.cs4485.sentencebuilder.service.MarkovChainService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/**
 * Controller for the text generation tab. Handles user input for text
 * generation parameters.
 *
 * @author CS4485 Team
 * @version 1.0
 */
public class TextGeneratorController {

    @FXML
    private TextField startWordField;

    @FXML
    private Slider wordCountSlider;

    @FXML
    private Label wordCountLabel;

    @FXML
    private RadioButton firstOrderRadio;

    @FXML
    private RadioButton secondOrderRadio;

    @FXML
    private RadioButton ngramRadio;

    @FXML
    private ToggleGroup algorithmGroup;

    @FXML
    private Slider nValueSlider;

    @FXML
    private Label nValueLabel;

    @FXML
    private Button generateButton;

    @FXML
    private TextArea outputArea;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField autocompleteField;

    @FXML
    private ListView<String> suggestionsList;

    @FXML
    private Label autocompleteStatusLabel;

    private MarkovChainService markovChainService;
    private edu.utdallas.cs4485.sentencebuilder.service.NGramService ngramService;
    private edu.utdallas.cs4485.sentencebuilder.service.AutoCompleteService autoCompleteService;

    /**
     * Constructor.
     */
    public TextGeneratorController() {
        this.markovChainService = new MarkovChainService();
        this.ngramService = new edu.utdallas.cs4485.sentencebuilder.service.NGramService();
        this.autoCompleteService = new edu.utdallas.cs4485.sentencebuilder.service.AutoCompleteService();
    }

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // TODO: Initialize text generator controller
        // Set up slider listener
        wordCountSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            wordCountLabel.setText(String.valueOf(newVal.intValue()));
        });

        // Set up N-value slider listener
        if (nValueSlider != null) {
            nValueSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                nValueLabel.setText(String.valueOf(newVal.intValue()));
            });
            nValueSlider.setDisable(true); // Disabled by default
        }

        // Set up algorithm radio button listeners
        if (algorithmGroup != null) {
            algorithmGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
                if (ngramRadio != null && nValueSlider != null) {
                    nValueSlider.setDisable(newVal != ngramRadio);
                }
            });
        }

        // Set default algorithm
        secondOrderRadio.setSelected(true);

        // Set up autocomplete field listener
        if (autocompleteField != null) {
            autocompleteField.textProperty().addListener((obs, oldVal, newVal) -> {
                updateAutocompleteSuggestions(newVal);
            });
        }

        // Set up double-click on suggestions list
        if (suggestionsList != null) {
            suggestionsList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    handleAppendSuggestion();
                }
            });
        }
    }

    /**
     * Updates autocomplete suggestions based on the current text.
     */
    private void updateAutocompleteSuggestions(String text) {
        if (text == null || text.trim().isEmpty()) {
            suggestionsList.getItems().clear();
            autocompleteStatusLabel.setText("");
            return;
        }

        try {
            boolean useNGram = ngramRadio != null && ngramRadio.isSelected();
            int n = 2; // Default to bigram

            if (useNGram && nValueSlider != null) {
                n = (int) nValueSlider.getValue();
            } else if (secondOrderRadio.isSelected()) {
                n = 2;
            } else {
                n = 1;
            }

            java.util.List<String> suggestions = autoCompleteService.getSuggestions(text, n, 10);

            suggestionsList.getItems().clear();
            suggestionsList.getItems().addAll(suggestions);

            if (suggestions.isEmpty()) {
                autocompleteStatusLabel.setText("No suggestions found");
            } else {
                autocompleteStatusLabel.setText(suggestions.size() + " suggestions");
            }

        } catch (Exception e) {
            autocompleteStatusLabel.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Handles the generate button click.
     */
    @FXML
    private void handleGenerate() {
        // TODO: Implement text generation
        try {
            String startWord = startWordField.getText().trim();
            int maxWords = (int) wordCountSlider.getValue();
            boolean useSecondOrder = secondOrderRadio.isSelected();
            boolean useNGram = ngramRadio != null && ngramRadio.isSelected();

            statusLabel.setText("Generating text...");

            GenerationResult result;

            if (useNGram) {
                // Use N-gram generation
                int nValue = nValueSlider != null ? (int) nValueSlider.getValue() : 3;
                result = ngramService.generateText(
                        startWord.isEmpty() ? null : startWord,
                        maxWords,
                        nValue
                );
            } else {
                // Use Markov chain generation
                result = markovChainService.generateText(
                        startWord.isEmpty() ? null : startWord,
                        maxWords,
                        useSecondOrder
                );
            }

            outputArea.setText(result.getGeneratedText());
            statusLabel.setText(String.format("Generated %d words in %.2f seconds using %s",
                    result.getWordCount(),
                    result.getDurationSeconds(),
                    result.getAlgorithm()));

        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            outputArea.setText("Generation failed. Please ensure training data has been imported.");
            e.printStackTrace();
        }
    }

    /**
     * Handles the clear button click.
     */
    @FXML
    private void handleClear() {
        // TODO: Implement clear logic
        startWordField.clear();
        outputArea.clear();
        statusLabel.setText("");
    }

    /**
     * Handles the copy button click.
     */
    @FXML
    private void handleCopy() {
        // TODO: Implement copy to clipboard
        String text = outputArea.getText();
        if (!text.isEmpty()) {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(text);
            clipboard.setContent(content);
            statusLabel.setText("Text copied to clipboard");
        }
    }

    /**
     * Handles appending the selected suggestion to the autocomplete field.
     */
    @FXML
    private void handleAppendSuggestion() {
        String selected = suggestionsList.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isEmpty()) {
            String currentText = autocompleteField.getText();
            if (currentText.isEmpty()) {
                autocompleteField.setText(selected);
            } else {
                autocompleteField.setText(currentText + " " + selected);
            }
            autocompleteStatusLabel.setText("Appended: " + selected);
        } else {
            autocompleteStatusLabel.setText("Please select a suggestion first");
        }
    }

    /**
     * Handles clearing the autocomplete field.
     */
    @FXML
    private void handleClearAutocomplete() {
        autocompleteField.clear();
        suggestionsList.getItems().clear();
        autocompleteStatusLabel.setText("Cleared");
    }
}
