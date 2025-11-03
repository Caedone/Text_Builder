package edu.utdallas.cs4485.sentencebuilder.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

/**
 * Main controller for the application.
 * Manages the main window and tab navigation.
 *
 * @author CS4485 Team
 * @version 1.0
 */
public class MainController {

    @FXML
    private TabPane mainTabPane;

    /**
     * Initializes the controller.
     * Called automatically by JavaFX after FXML loading.
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