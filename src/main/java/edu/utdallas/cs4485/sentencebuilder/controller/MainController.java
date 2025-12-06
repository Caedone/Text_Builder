package edu.utdallas.cs4485.sentencebuilder.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

/**
 *
 * Main controller for the Sentence Builder application. This class serves as the
 * central coordinator for the JavaFX user interface, managing the primary window
 * and navigation between different functional tabs.
 *
 * The controller handles the main TabPane that contains all feature tabs including
 * file import, text generation, and database viewing. It also manages global menu
 * actions such as application exit and help dialogs.
 *
 * This is the first controller loaded when the application starts and coordinates
 * the overall user experience across different application features.
 *
 * @author Johnathan Pedraza
 * @author Rahman-Danish, Rizvy
 */
public class MainController {

    @FXML
    private TabPane mainTabPane;

    /**
     * Initializes the controller. Called automatically by JavaFX after FXML
     * loading.
     */
    @FXML
    public void initialize() {
        // TODO: Initialize main controller
        // Set up menu items, event handlers, etc.
    }

    /**
     * Handles File > Exit menu action.
     */
    @FXML
    private void handleExit() {
        // TODO: Implement exit logic
        // Clean up resources and close application
        System.exit(0);
    }

    /**
     * Handles Help > About menu action.
     */
    @FXML
    private void handleAbout() {
        // TODO: Implement about dialog
        // Show application information
    }
}
