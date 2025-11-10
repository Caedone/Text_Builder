package edu.utdallas.cs4485.sentencebuilder.controller;

import edu.utdallas.cs4485.sentencebuilder.model.ImportedFile;
import edu.utdallas.cs4485.sentencebuilder.service.TextProcessingService;
import edu.utdallas.cs4485.sentencebuilder.service.DatabaseService;
import edu.utdallas.cs4485.sentencebuilder.util.FileUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
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
    private TableColumn<ImportedFile, String> filePathColumn;

    @FXML
    private CheckBox processNGramsCheckBox;

    @FXML
    private Slider ngramNValueSlider;

    @FXML
    private Label ngramNValueLabel;

    private TextProcessingService textProcessingService;
    private DatabaseService databaseService;
    private edu.utdallas.cs4485.sentencebuilder.service.NGramService ngramService;
    private Task<Void> currentImportTask;

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

        filePathColumn.setCellValueFactory(cellData -> {
            String filePath = cellData.getValue().getFilePath();
            if (filePath != null) {
                // Display file path - avoid checking validity here to prevent UI blocking
                // File validity is checked during import, and users can verify manually if needed
                return new SimpleStringProperty(filePath);
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
            String absolutePath = selectedFile.getAbsolutePath();
            filePathField.setText(absolutePath);
            
            // Validate and show file info
            Path path = Path.of(absolutePath);
            if (FileUtils.isValidFile(path)) {
                statusLabel.setText("File selected: " + absolutePath + " (File is readable)");
            } else {
                statusLabel.setText("Warning: Selected file may not be readable: " + absolutePath);
            }
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

        // Check if an import is already in progress
        if (currentImportTask != null && currentImportTask.isRunning()) {
            statusLabel.setText("Import already in progress. Please wait...");
            return;
        }

        // Validate file before processing (quick check on UI thread)
        Path path = Path.of(filePath);
        if (!FileUtils.isValidFile(path)) {
            statusLabel.setText("Error: File does not exist or cannot be read: " + filePath);
            importProgressBar.setProgress(0.0);
            return;
        }

        // Check file size
        try {
            if (!FileUtils.isAcceptableSize(path)) {
                statusLabel.setText("Error: File size is too large or invalid");
                importProgressBar.setProgress(0.0);
                return;
            }
        } catch (java.io.IOException e) {
            statusLabel.setText("Error: Cannot determine file size: " + e.getMessage());
            importProgressBar.setProgress(0.0);
            return;
        }

        // Check file format
        if (!FileUtils.isSupportedFormat(path.getFileName().toString())) {
            statusLabel.setText("Error: Unsupported file format. Supported formats: .txt, .pdf, .doc, .docx");
            importProgressBar.setProgress(0.0);
            return;
        }

        // Get N-gram settings before starting background task
        final boolean processNGrams = processNGramsCheckBox != null && processNGramsCheckBox.isSelected();
        final int nValue = processNGrams && ngramNValueSlider != null ? (int) ngramNValueSlider.getValue() : 3;

        // Create background task for file import
        currentImportTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Importing file from: " + path.toAbsolutePath().toString());
                updateProgress(0.1, 1.0);

                // Read and process file
                updateMessage("Reading file...");
                String text = textProcessingService.extractText(path);
                String cleanedText = textProcessingService.cleanText(text);
                updateProgress(0.2, 1.0);

                // Tokenize into sentences
                updateMessage("Tokenizing text...");
                java.util.List<String> sentences = textProcessingService.tokenizeSentences(cleanedText);
                updateProgress(0.3, 1.0);

                // Process each sentence and build word database
                updateMessage("Processing words and word pairs...");
                int totalWords = 0;
                int sentenceCount = sentences.size();
                int processedSentences = 0;

                if (sentenceCount == 0) {
                    updateMessage("Warning: No sentences found in file");
                    updateProgress(0.8, 1.0);
                } else {
                    for (String sentence : sentences) {
                        java.util.List<String> words = textProcessingService.tokenizeWords(sentence);
                        if (words.isEmpty()) {
                            processedSentences++;
                            continue;
                        }

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

                        processedSentences++;
                        // Update progress based on sentence processing
                        if (sentenceCount > 0) {
                            double progress = 0.3 + (0.5 * processedSentences / sentenceCount);
                            updateProgress(progress, 1.0);
                            updateMessage(String.format("Processing sentences: %d/%d", processedSentences, sentenceCount));
                        }
                    }
                }

                updateProgress(0.8, 1.0);

                // Process N-grams if checkbox is selected
                if (processNGrams) {
                    updateMessage("Processing N-grams (N=" + nValue + ")...");
                    ngramService.processAndStoreNGrams(cleanedText, nValue);
                    updateProgress(0.9, 1.0);
                }

                // Create and save file record
                updateMessage("Saving file record...");
                ImportedFile file = new ImportedFile(path.getFileName().toString(), path.toAbsolutePath().toString());
                file.setWordCount(totalWords);
                file.setStatus(ImportedFile.FileStatus.COMPLETED);
                databaseService.saveImportedFile(file);

                updateProgress(1.0, 1.0);

                // Verify file is still accessible after import
                boolean fileAccessible = FileUtils.isValidFile(path);
                String message = String.format("File imported successfully: %s - %d words processed", 
                        path.getFileName().toString(), totalWords);
                if (processNGrams) {
                    message += String.format(" (N-grams N=%d)", nValue);
                }
                if (!fileAccessible) {
                    message += " [WARNING: File location not accessible]";
                }
                updateMessage(message);

                return null;
            }
        };

        // Bind UI to task properties
        statusLabel.textProperty().bind(currentImportTask.messageProperty());
        importProgressBar.progressProperty().bind(currentImportTask.progressProperty());

        // Disable import button during import
        importButton.setDisable(true);
        browseButton.setDisable(true);

        // Handle task completion
        currentImportTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                statusLabel.textProperty().unbind();
                importProgressBar.progressProperty().unbind();
                importButton.setDisable(false);
                browseButton.setDisable(false);
                
                // Refresh history table
                refreshHistory();
            });
            
            currentImportTask = null;
        });

        // Handle task failure
        currentImportTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                statusLabel.textProperty().unbind();
                importProgressBar.progressProperty().unbind();
                importButton.setDisable(false);
                browseButton.setDisable(false);
                
                Throwable exception = currentImportTask.getException();
                String errorMessage = "Import failed: " + 
                        (exception != null ? exception.getMessage() : "Unknown error");
                statusLabel.setText(errorMessage);
                importProgressBar.setProgress(0.0);
            });
            
            Throwable exception = currentImportTask.getException();
            if (exception != null) {
                exception.printStackTrace();
            }
            
            currentImportTask = null;
        });

        // Start the task in a background thread
        Thread importThread = new Thread(currentImportTask);
        importThread.setDaemon(true);
        importThread.start();
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