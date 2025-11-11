/**
 *  Written by Manraj Singh for CS Project, starting Oct 28, 2025.
 *  NetID: mxs220007
 */
package edu.utdallas.cs4485.sentencebuilder.controller;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;

import edu.utdallas.cs4485.sentencebuilder.model.ImportedFile;
import edu.utdallas.cs4485.sentencebuilder.service.DatabaseService;
import edu.utdallas.cs4485.sentencebuilder.service.TextProcessingService;
import edu.utdallas.cs4485.sentencebuilder.util.FileUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

/**
 *
 * This controller will be the entry point for all imported data into the
 * system. It manages the user interface for selecting text files, initiating
 * the import process.
 *
 * Handles file selection, allowing user to pick .txt, .pdf, .doc, .docx files
 * from local system. When user imports file, the controller validates file
 * path, size and type It also validates user option of N-gram processing and
 * the N value from 2-5
 *
 * The text goes through a process of "cleaning" - removing symbols, normalize
 * spacing, etc. Each sentence is tokenized into words, and word relationships
 * are extracted
 *
 * Records of each word and the word next to it is recorded and stored for
 * building Markov Relationships
 *
 * @author Manraj Singh
 * @version 1.0
 */
public class FileImportController {

    @FXML
    private TextField filePathField;

    @FXML
    private Button browseButton;

    @FXML
    private Button deleteFileButton;

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
     * Creates a new controller instance. Creates a new connection to the
     * service layer for text processing, N-gram processing and database
     * operations.
     */
    public FileImportController() {
        this.textProcessingService = new TextProcessingService();
        this.databaseService = new DatabaseService();
        this.ngramService = new edu.utdallas.cs4485.sentencebuilder.service.NGramService();
    }

    /**
     * Prepares controller interface and configures UI elements. Fetches
     * previously imported files to display import history. - Even if file is
     * "deleted" from system Handles displaying the import history table and
     * configuring N-gram options. Prepares the controller interface and
     * configures UI elements
     */
    @FXML
    public void initialize() {
        // Configure table display
        setupTableColumns();

        // Control import button availability
        importButton.setDisable(true);
        filePathField.textProperty().addListener((obs, oldVal, newVal) -> {
            importButton.setDisable(newVal == null || newVal.trim().isEmpty());
        });

        // Configure N-gram value display
        if (ngramNValueSlider != null && ngramNValueLabel != null) {
            ngramNValueSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                ngramNValueLabel.setText(String.valueOf(newVal.intValue()));
            });
            ngramNValueSlider.setDisable(true);
        }

        // Link checkbox to slider activation
        if (processNGramsCheckBox != null && ngramNValueSlider != null) {
            processNGramsCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                ngramNValueSlider.setDisable(!newVal);
            });
        }

        // Set up delete button initial state
        if (deleteFileButton != null) {
            deleteFileButton.setDisable(true);
        }

        // Enable/disable delete button based on table selection
        if (fileHistoryTable != null && deleteFileButton != null) {
            fileHistoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                deleteFileButton.setDisable(newVal == null);
            });
        }

        // Refresh import history display
        refreshHistory();
    }

    /**
     * Handles orientation of import history table columns. Crucial for user
     * insight into previously imported files.
     */
    private void setupTableColumns() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        filenameColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getFilename()));

        wordCountColumn.setCellValueFactory(cellData
                -> new SimpleIntegerProperty(cellData.getValue().getWordCount()).asObject());

        statusColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));

        dateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getImportDate() != null) {
                return new SimpleStringProperty(dateFormat.format(cellData.getValue().getImportDate()));
            }
            return new SimpleStringProperty("");
        });

        /**
         * Shows file location without the need of validation check which
         * recduces UI update delays File accessibility is verified during the
         * import of the file - Will cause potential errors if local path
         * changes or files are moved TODO: Handle file accessibility if path
         * changes after import - Just display coherent error messsage for user
         */
        filePathColumn.setCellValueFactory(cellData -> {
            String filePath = cellData.getValue().getFilePath();
            if (filePath != null) {
                return new SimpleStringProperty(filePath);
            }
            return new SimpleStringProperty("");
        });
    }

    /**
     * Responds to file selection requests from the browse button.
     *
     * Presents a file chooser dialog allowing users to specify text documents
     * for training data extraction. The chosen file's contents will undergo
     * processing to identify word relationships, vocabulary statistics, and
     * optionally phrase patterns that establish the statistical basis for
     * Markov and N-gram text synthesis.
     *
     * File accessibility is verified immediately to give users quick feedback
     * before starting the potentially lengthy import procedure. This prevents
     * wasted time on processing attempts for inaccessible files.
     */
    @FXML
    private void handleBrowse() {
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

            // Check accessibility and inform user
            Path path = Path.of(absolutePath);
            if (FileUtils.isValidFile(path)) {
                statusLabel.setText("File selected: " + absolutePath + " (File is readable)");
            } else {
                statusLabel.setText("Warning: Selected file may not be readable - File path may be incorrect: " + absolutePath);
            }
        }
    }

    /**
     * Executes the training data processing workflow when import is requested.
     *
     * Handles the conversion of raw text files into structured training data
     *
     * Maps word relationships to help establish statistical foundations for
     * Markov Chain Generation
     *
     * Extracts text from local files and formats them for statistical analysis.
     * - Elimates non-printing characters - Normalizes whitespace - Tokenizes
     * text into sentences and words - Extracts word pairs and N-grams
     *
     * Background task management ensures UI responsiveness during potentially
     * lengthy operations
     *
     * All extracted data is transferred to the database for persistent storage
     * and future retrieval
     */
    @FXML
    private void handleImport() {
        String filePath = filePathField.getText().trim();
        if (filePath.isEmpty()) {
            statusLabel.setText("Please select a file to import");
            return;
        }

        // Prevent concurrent imports
        if (currentImportTask != null && currentImportTask.isRunning()) {
            statusLabel.setText("Import already in progress. Please wait...");
            return;
        }

        // Perform quick accessibility check on main thread
        Path path = Path.of(filePath);
        if (!FileUtils.isValidFile(path)) {
            statusLabel.setText("Error: File does not exist or cannot be read: " + filePath);
            importProgressBar.setProgress(0.0);
            return;
        }

        // Verify file size constraints
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

        // Confirm format support
        if (!FileUtils.isSupportedFormat(path.getFileName().toString())) {
            statusLabel.setText("Error: Unsupported file format. Supported formats: .txt, .pdf, .doc, .docx");
            importProgressBar.setProgress(0.0);
            return;
        }

        // Capture N-gram configuration before background processing
        final boolean processNGrams = processNGramsCheckBox != null && processNGramsCheckBox.isSelected();
        final int nValue = processNGrams && ngramNValueSlider != null ? (int) ngramNValueSlider.getValue() : 3;

        // Establish background processing task
        currentImportTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Importing file from: " + path.toAbsolutePath().toString());
                updateProgress(0.1, 1.0);

                // Extract and normalize content for algorithm training
                updateMessage("Reading file...");
                String text = textProcessingService.extractText(path);
                String cleanedText = textProcessingService.cleanText(text);
                updateProgress(0.2, 1.0);
                /**
                 * Splits text into sentences for alogorithms to generate text
                 * within the necessary boundaries
                 */
                updateMessage("Tokenizing text...");
                java.util.List<String> sentences = textProcessingService.tokenizeSentences(cleanedText);
                updateProgress(0.3, 1.0);

                /**
                 * Sentence analysis for Markov model training Extracts word
                 * pairs and transition frequencies to establish the statistical
                 * framework for Markov text generation
                 */
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
                        /**
                         * Record initiial word for Markov algorithm's reference
                         * Gives the generator a natural starting point for
                         * sentence generation
                         */
                        String firstWord = words.get(0).toLowerCase();
                        databaseService.incrementWordCount(firstWord, true, false);
                        totalWords++;

                        /**
                         * Record intermediate words and extract pairings Helps
                         * establish the transition probability matric for
                         * Markov Alrogithm Each pair (word1, word2) represents
                         * a possible state transition The generator uses
                         * weighted randomization based on occurrence counts to
                         * select next words
                         */
                        for (int i = 0; i < words.size() - 1; i++) {
                            String word1 = words.get(i).toLowerCase();
                            String word2 = words.get(i + 1).toLowerCase();

                            databaseService.incrementWordCount(word1, false, false);
                            databaseService.incrementWordPairCount(word1, word2);
                            totalWords++;
                        }
                        /**
                         * Record final word in sentence to close sequence
                         * Critical for coherent sentence generation
                         */
                        if (words.size() > 1) {
                            String lastWord = words.get(words.size() - 1).toLowerCase();
                            databaseService.incrementWordCount(lastWord, false, true);
                            totalWords++;
                        }

                        processedSentences++;
                        // Report progress based on sentence completion
                        if (sentenceCount > 0) {
                            double progress = 0.3 + (0.5 * processedSentences / sentenceCount);
                            updateProgress(progress, 1.0);
                            updateMessage(String.format("Processing sentences: %d/%d", processedSentences, sentenceCount));
                        }
                    }
                }

                updateProgress(0.8, 1.0);

                /**
                 * Updating n-value for N-gram process based on user selection
                 * Higher N values (4-5) preserve more context - not really
                 * tested on small files Lower N values (2-3) function with
                 * smaller datasets - results will be less coherent
                 */
                if (processNGrams) {
                    updateMessage("Processing N-grams (N=" + nValue + ")...");
                    ngramService.processAndStoreNGrams(cleanedText, nValue);
                    updateProgress(0.9, 1.0);
                }

                /**
                 * Creates ImportedFile object to represent the imported file
                 */
                updateMessage("Saving file record...");
                ImportedFile file = new ImportedFile(path.getFileName().toString(), path.toAbsolutePath().toString());
                file.setWordCount(totalWords);
                file.setStatus(ImportedFile.FileStatus.COMPLETED);
                databaseService.saveImportedFile(file);

                updateProgress(1.0, 1.0);

                // Confirm file remains accessible post-import
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

        // Connect UI elements to task state
        statusLabel.textProperty().bind(currentImportTask.messageProperty());
        importProgressBar.progressProperty().bind(currentImportTask.progressProperty());

        // Lock controls during processing
        importButton.setDisable(true);
        browseButton.setDisable(true);

        // Configure success handling
        currentImportTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                statusLabel.textProperty().unbind();
                importProgressBar.progressProperty().unbind();
                importButton.setDisable(false);
                browseButton.setDisable(false);

                // Update history display
                refreshHistory();
            });

            currentImportTask = null;
        });

        // Configure failure handling
        currentImportTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                statusLabel.textProperty().unbind();
                importProgressBar.progressProperty().unbind();
                importButton.setDisable(false);
                browseButton.setDisable(false);

                Throwable exception = currentImportTask.getException();
                String errorMessage = "Import failed: "
                        + (exception != null ? exception.getMessage() : "Unknown error");
                statusLabel.setText(errorMessage);
                importProgressBar.setProgress(0.0);
            });

            Throwable exception = currentImportTask.getException();
            if (exception != null) {
                exception.printStackTrace();
            }

            currentImportTask = null;
        });

        // Launch background processing
        Thread importThread = new Thread(currentImportTask);
        importThread.setDaemon(true);
        importThread.start();
    }

    /**
     * Handles the delete button click to remove a selected imported file
     * record. File is removed for the imported_files table only - training data
     * still utilizes the deleted file's data Index of the removed file is still
     * stored for the sake of maintaining data integrity. Confirmation dialog is
     * presented to user to prevent accidental deletion
     *
     * *** The database doesn't record from where the word pairings come from.
     */
    @FXML
    private void handleDeleteFile() {
        ImportedFile selectedFile = fileHistoryTable.getSelectionModel().getSelectedItem();
        if (selectedFile == null) {
            statusLabel.setText("Please select a file to delete");
            return;
        }

        // Show confirmation dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Imported File");
        confirmDialog.setHeaderText("Delete File Record");
        confirmDialog.setContentText(
                "Are you sure you want to delete the file record for:\n\n"
                + "Filename: " + selectedFile.getFilename() + "\n"
                + "Word Count: " + selectedFile.getWordCount() + "\n\n"
                + "Note: This will only remove the file record from the import history. "
                + "The training data (words, word pairs, N-grams) extracted from this file will "
                + "remain in the database since it may be shared with other imported files."
        );

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Delete the file record
                    databaseService.deleteImportedFile(selectedFile.getFileId());

                    // Refresh the history table
                    refreshHistory();

                    // Clear selection
                    fileHistoryTable.getSelectionModel().clearSelection();

                    statusLabel.setText("File record deleted successfully: " + selectedFile.getFilename());
                } catch (Exception e) {
                    statusLabel.setText("Error deleting file: " + e.getMessage());
                    e.printStackTrace();

                    // Show error dialog
                    Alert errorDialog = new Alert(Alert.AlertType.ERROR);
                    errorDialog.setTitle("Delete Error");
                    errorDialog.setHeaderText("Failed to delete file record");
                    errorDialog.setContentText("An error occurred while deleting the file record:\n" + e.getMessage());
                    errorDialog.showAndWait();
                }
            }
        });
    }

    /**
     * Updates the import history display with all processed training documents.
     *
     * Retrieves and presents all successfully imported files from storage,
     * giving users a complete record of documents that have contributed to the
     * statistical models. This historical view serves multiple purposes:
     *
     * When generation produces an incorherent output, the user can see which
     * files the training data came from.
     */
    private void refreshHistory() {
        try {
            fileHistoryTable.getItems().clear();
            fileHistoryTable.getItems().addAll(databaseService.getAllImportedFiles());

            // Update delete button state
            if (deleteFileButton != null) {
                deleteFileButton.setDisable(fileHistoryTable.getItems().isEmpty()
                        || fileHistoryTable.getSelectionModel().getSelectedItem() == null);
            }
        } catch (Exception e) {
            System.err.println("Failed to refresh history: " + e.getMessage());
            statusLabel.setText("Error refreshing file history: " + e.getMessage());
        }
    }
}
