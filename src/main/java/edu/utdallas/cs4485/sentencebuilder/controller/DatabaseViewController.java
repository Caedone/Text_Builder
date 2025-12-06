package edu.utdallas.cs4485.sentencebuilder.controller;

import edu.utdallas.cs4485.sentencebuilder.model.Word;
import edu.utdallas.cs4485.sentencebuilder.model.WordPair;
import edu.utdallas.cs4485.sentencebuilder.service.DatabaseService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 *
 * Controller for the database visualization interface. This class manages the display
 * and interaction with stored word statistics and relationship data in tabular format.
 *
 * Provides two main views: a Words table showing individual word statistics including
 * total occurrence count, start-of-sentence count, and end-of-sentence count; and a
 * Word Pairs table showing word transition relationships with occurrence counts and
 * calculated probabilities for Markov chain analysis.
 *
 * Users can search and filter the displayed data, refresh the tables to see updated
 * information, and examine the underlying statistical model that drives text generation.
 * This view helps users understand how the system learned from their imported texts.
 *
 * @author Bhaskar Atmakuri
 */
public class DatabaseViewController {

    @FXML
    private TabPane databaseTabPane;

    @FXML
    private TableView<Word> wordsTable;

    @FXML
    private TableColumn<Word, String> wordTextColumn;

    @FXML
    private TableColumn<Word, Integer> totalCountColumn;

    @FXML
    private TableColumn<Word, Integer> startCountColumn;

    @FXML
    private TableColumn<Word, Integer> endCountColumn;

    @FXML
    private TableView<WordPair> wordPairsTable;

    @FXML
    private TableColumn<WordPair, String> firstWordColumn;

    @FXML
    private TableColumn<WordPair, String> secondWordColumn;

    @FXML
    private TableColumn<WordPair, Integer> transitionCountColumn;

    @FXML
    private TableColumn<WordPair, Double> probabilityColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Button refreshButton;

    @FXML
    private Label statusLabel;

    private DatabaseService databaseService;

    /**
     * Constructor.
     */
    public DatabaseViewController() {
        this.databaseService = new DatabaseService();
    }

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // TODO: Initialize database view controller
        // Set up table columns
        setupWordTableColumns();
        setupWordPairTableColumns();

        // Load initial data
        refreshData();
    }

    /**
     * Sets up the column bindings for the words table.
     */
    private void setupWordTableColumns() {
        wordTextColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getWordText()));

        totalCountColumn.setCellValueFactory(cellData
                -> new SimpleIntegerProperty(cellData.getValue().getTotalCount()).asObject());

        startCountColumn.setCellValueFactory(cellData
                -> new SimpleIntegerProperty(cellData.getValue().getSentenceStartCount()).asObject());

        endCountColumn.setCellValueFactory(cellData
                -> new SimpleIntegerProperty(cellData.getValue().getSentenceEndCount()).asObject());
    }

    /**
     * Sets up the column bindings for the word pairs table.
     */
    private void setupWordPairTableColumns() {
        firstWordColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getFirstWordText()));

        secondWordColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getSecondWordText()));

        transitionCountColumn.setCellValueFactory(cellData
                -> new SimpleIntegerProperty(cellData.getValue().getTransitionCount()).asObject());

        probabilityColumn.setCellValueFactory(cellData
                -> new SimpleDoubleProperty(cellData.getValue().getTransitionProbability()).asObject());
    }

    /**
     * Handles the refresh button click.
     */
    @FXML
    private void handleRefresh() {
        // TODO: Implement refresh logic
        refreshData();
    }

    /**
     * Refreshes data in both tables.
     */
    private void refreshData() {
        try {
            statusLabel.setText("Loading data...");

            // Load words
            wordsTable.getItems().clear();
            wordsTable.getItems().addAll(databaseService.getAllWords());

            // Load word pairs
            wordPairsTable.getItems().clear();
            wordPairsTable.getItems().addAll(databaseService.getAllWordPairs());

            statusLabel.setText(String.format("Loaded %d words and %d word pairs",
                    wordsTable.getItems().size(),
                    wordPairsTable.getItems().size()));

        } catch (Exception e) {
            statusLabel.setText("Error loading data: " + e.getMessage());
        }
    }

    /**
     * Handles search field text changes.
     */
    @FXML
    private void handleSearch() {
        // TODO: Implement search/filter logic
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            refreshData();
            return;
        }

        // Filter tables based on search text
        // This is a simple example - implement more sophisticated filtering as needed
    }

    /**
     * Handles recalculate probabilities button click.
     */
    @FXML
    private void handleRecalculateProbabilities() {
        // TODO: Implement probability recalculation
        try {
            statusLabel.setText("Recalculating probabilities...");
            databaseService.recalculateProbabilities();
            refreshData();
            statusLabel.setText("Probabilities recalculated successfully");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }
}
