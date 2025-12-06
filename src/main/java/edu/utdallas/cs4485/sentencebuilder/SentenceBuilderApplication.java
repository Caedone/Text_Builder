package edu.utdallas.cs4485.sentencebuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *
 * Main application entry point for Sentence Builder, a JavaFX-based text generation
 * application using Markov chain and N-gram algorithms.
 *
 * Extends JavaFX Application to bootstrap the user interface, loading the main FXML
 * view and establishing the primary stage with configured dimensions and styling.
 * Initializes the application window with title, minimum size constraints, and CSS
 * stylesheet for consistent visual presentation.
 *
 * Manages application lifecycle including startup (loading FXML and resources) and
 * shutdown (cleaning up database connections and resources). Serves as the launch
 * point for the entire application, coordinating between the JavaFX framework and
 * the custom controllers, services, and algorithms that implement text generation
 * functionality.
 *
 * @author Caedon Ewing
 */
public class SentenceBuilderApplication extends Application {

    private static final String MAIN_VIEW_FXML = "/fxml/main-view.fxml";
    private static final String APP_TITLE = "Sentence Builder - Markov Chain Text Generator";
    private static final int DEFAULT_WIDTH = 1024;
    private static final int DEFAULT_HEIGHT = 768;

    /**
     * Starts the JavaFX application.
     *
     * @param stage the primary stage for this application
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource(MAIN_VIEW_FXML)
            );

            Scene scene = new Scene(fxmlLoader.load(), DEFAULT_WIDTH, DEFAULT_HEIGHT);

            // Load CSS stylesheet if available
            String cssPath = getClass().getResource("/css/application.css").toExternalForm();
            scene.getStylesheets().add(cssPath);

            stage.setTitle(APP_TITLE);
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called when the application is stopped.
     * Cleanup resources here (database connections, etc.)
     */
    @Override
    public void stop() throws Exception {
        // TODO: Clean up database connections and other resources
        super.stop();
    }

    /**
     * Main entry point for the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}