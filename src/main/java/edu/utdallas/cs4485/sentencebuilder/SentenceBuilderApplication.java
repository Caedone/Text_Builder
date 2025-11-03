package edu.utdallas.cs4485.sentencebuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class for Sentence Builder.
 * Uses JavaFX for the user interface and implements Markov chain text generation.
 *
 * @author CS4485 Team
 * @version 1.0
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