package edu.utdallas.cs4485.sentencebuilder.controller;

import edu.utdallas.cs4485.sentencebuilder.model.ImportedFile;
import edu.utdallas.cs4485.sentencebuilder.service.TextProcessingService;
import edu.utdallas.cs4485.sentencebuilder.service.DatabaseService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;

/**
 * Controller for the file import tab.
 * Handles file selection and import operations.
 *
 * @author CS4485 Team
 * @version 1.0
 */
public class FileImportController {

    @FXML
    private TextField filePathField;

    @FXML
    private Button browseButton;

    @FXML
    private Button importButton;

    @FXML
    private ProgressBar importProgressBar;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<ImportedFile> fileHistoryTable;

    @FXML
    private TableColumn<ImportedFile, String> filenameColumn;

    @FXML
    private TableColumn<ImportedFile, Integer> wordCountColumn;

    @FXML
    private TableColumn<ImportedFile, String> statusColumn;

    @FXML
    private TableColumn<ImportedFile, String> dateColumn;

    @FXML
    private CheckBox processNGramsCheckBox;

    @FXML
    private Slider ngramNValueSlider;

    @FXML
    private Label ngramNValueLabel;

    private TextProcessingService textProcessingService;
    private DatabaseService databaseService;
    private edu.utdallas.cs4485.sentencebuilder.service.NGramService ngramService;

    /**
     * Constructor.
     */
    public FileImportController() {
        this.textProcessingService = new TextProcessingService();
        this.databaseService = new DatabaseService();
        this.ngramService = new edu.utdallas.cs4485.sentencebuilder.service.NGramService();
    }

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // TODO: Initialize file import controller
        // Set up table columns
        setupTableColumns();

        // Load import history
        importButton.setDisable(true);
        filePathField.textProperty().addListener((obs, oldVal, newVal) -> {
            importButton.setDisable(newVal == null || newVal.trim().isEmpty());
        });

        // Set up N-gram N-value slider
        if (ngramNValueSlider != null && ngramNValueLabel != null) {
            ngramNValueSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                ngramNValueLabel.setText(String.valueOf(newVal.intValue()));
            });
            ngramNValueSlider.setDisable(true);
        }

        // Set up checkbox listener for N-gram processing
        if (processNGramsCheckBox != null && ngramNValueSlider != null) {
            processNGramsCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                ngramNValueSlider.setDisable(!newVal);
            });
        }

        // Load initial history
        refreshHistory();
    }

    /**
     * Sets up the column bindings for the file history table.
     */
    private void setupTableColumns() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        filenameColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getFilename()));

        wordCountColumn.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getWordCount()).asObject());

        statusColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getStatus().toString()));

        dateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getImportDate() != null) {
                return new SimpleStringProperty(dateFormat.format(cellData.getValue().getImportDate()));
            }
            return new SimpleStringProperty("");
        });
    }

    /**
     * Handles the browse button click.
     */
    @FXML
    private void handleBrowse() {
        // TODO: Implement file selection dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Text File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Word Documents", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(browseButton.getScene().getWindow());
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Handles the import button click.
     */
    @FXML
    private void handleImport() {
        String filePath = filePathField.getText().trim();
        if (filePath.isEmpty()) {
            statusLabel.setText("Please select a file to import");
            return;
        }

        try {
            statusLabel.setText("Importing file...");
            importProgressBar.setProgress(0.1);

            // Read and process file
            Path path = Path.of(filePath);
            String text = textProcessingService.extractText(path);
            String cleanedText = textProcessingService.cleanText(text);

            importProgressBar.setProgress(0.3);

            // Tokenize into sentences
            java.util.List<String> sentences = textProcessingService.tokenizeSentences(cleanedText);

            importProgressBar.setProgress(0.4);

            // Process each sentence and build word database
            int totalWords = 0;
            for (String sentence : sentences) {
                java.util.List<String> words = textProcessingService.tokenizeWords(sentence);
                if (words.isEmpty()) continue;

                // Process first word (sentence start)
                String firstWord = words.get(0).toLowerCase();
                databaseService.incrementWordCount(firstWord, true, false);
                totalWords++;

                // Process middle words and create word pairs
                for (int i = 0; i < words.size() - 1; i++) {
                    String word1 = words.get(i).toLowerCase();
                    String word2 = words.get(i + 1).toLowerCase();

                    databaseService.incrementWordCount(word1, false, false);
                    databaseService.incrementWordPairCount(word1, word2);
                    totalWords++;
                }

                // Process last word (sentence end)
                if (words.size() > 1) {
                    String lastWord = words.get(words.size() - 1).toLowerCase();
                    databaseService.incrementWordCount(lastWord, false, true);
                    totalWords++;
                }
            }

            importProgressBar.setProgress(0.8);

            // Process N-grams if checkbox is selected
            if (processNGramsCheckBox != null && processNGramsCheckBox.isSelected()) {
                int nValue = ngramNValueSlider != null ? (int) ngramNValueSlider.getValue() : 3;
                statusLabel.setText("Processing N-grams (N=" + nValue + ")...");
                ngramService.processAndStoreNGrams(cleanedText, nValue);
                importProgressBar.setProgress(0.9);
            }

            // Create and save file record
            ImportedFile file = new ImportedFile(path.getFileName().toString(), filePath);
            file.setWordCount(totalWords);
            file.setStatus(ImportedFile.FileStatus.COMPLETED);
            file = databaseService.saveImportedFile(file);

            String message = "File imported successfully - " + totalWords + " words processed";
            if (processNGramsCheckBox != null && processNGramsCheckBox.isSelected()) {
                int nValue = ngramNValueSlider != null ? (int) ngramNValueSlider.getValue() : 3;
                message += " (N-grams N=" + nValue + " processed)";
            }
            statusLabel.setText(message);
            importProgressBar.setProgress(1.0);

            // Refresh history table
            refreshHistory();

        } catch (Exception e) {
            statusLabel.setText("Import failed: " + e.getMessage());
            importProgressBar.setProgress(0.0);
            e.printStackTrace();
        }
    }

    /**
     * Refreshes the import history table.
     */
    private void refreshHistory() {
        // TODO: Implement history refresh
        try {
            fileHistoryTable.getItems().clear();
            fileHistoryTable.getItems().addAll(databaseService.getAllImportedFiles());
        } catch (Exception e) {
            System.err.println("Failed to refresh history: " + e.getMessage());
        }
    }
}