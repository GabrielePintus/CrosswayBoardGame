package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.Game;
import org.boardgames.crossway.utils.GameSerializer;
import org.boardgames.crossway.utils.JsonUtils;
import org.boardgames.crossway.utils.Messages;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Controller dedicated to handling import and export operations for game files.
 * <p>
 * It delegates serialization logic to {@link GameSerializer} and coordinates
 * UI interactions such as file choosers and dialog messages.
 * </p>
 */
public class FileController {

    /** Supplies the current game instance for export operations. */
    private final Supplier<Game> gameSupplier;
    /** Callback used to replace the current game after an import. */
    private final Consumer<Game> gameUpdater;
    /** Handles UI related tasks such as dialogs and frame retrieval. */
    private final UiController uiController;
    /** Displays messages and errors to the user. */
    private final DialogHandler dialogHandler;

    /**
     * Creates a new {@code FileController} with the required dependencies.
     *
     * @param gameSupplier Supplies the current {@link Game} instance.
     * @param gameUpdater  Callback to update the application with a new game.
     * @param uiController Handles UI operations.
     * @param dialogHandler Displays dialogs for information and error messages.
     */
    public FileController(Supplier<Game> gameSupplier,
                          Consumer<Game> gameUpdater,
                          UiController uiController,
                          DialogHandler dialogHandler) {
        this.gameSupplier = gameSupplier;
        this.gameUpdater = gameUpdater;
        this.uiController = uiController;
        this.dialogHandler = dialogHandler;
    }

    /** Prompts the user to save the current game state to a JSON file. */
    public void handleExportRequest() {
        JFileChooser chooser = createJsonFileChooser(Messages.get("menu.file.export"));
        int choice = chooser.showSaveDialog(uiController.getFrame());
        if (choice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            executeGameExport(JsonUtils.ensureJsonExtension(selectedFile));
        }
    }

    /** Prompts the user to load a game state from a JSON file. */
    public void handleImportRequest() {
        JFileChooser chooser = createJsonFileChooser(Messages.get("menu.file.import"));
        int choice = chooser.showOpenDialog(uiController.getFrame());
        if (choice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            executeGameImport(selectedFile);
        }
    }

    /** Creates a file chooser configured for JSON files. */
    private JFileChooser createJsonFileChooser(String title) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                Messages.get("file.filter.json"), JsonUtils.JSON_EXT
        );
        chooser.setFileFilter(filter);
        return chooser;
    }

    /** Loads a game from the specified file and updates the application state. */
    private void executeGameImport(File targetFile) {
        try {
            Game imported = GameSerializer.load(targetFile);
            gameUpdater.accept(imported);
        } catch (Exception ex) {
            dialogHandler.showError(
                    Messages.get("error.import.title"),
                    Messages.format("error.import.message", ex.getMessage())
            );
        }
    }

    /** Saves the current game to the specified file. */
    private void executeGameExport(File targetFile) {
        try {
            GameSerializer.save(gameSupplier.get(), targetFile);
            dialogHandler.showInfo(
                    Messages.get("export.success.title"),
                    Messages.format("export.success.message", targetFile.getName())
            );
        } catch (Exception ex) {
            dialogHandler.showError(
                    Messages.get("error.export.title"),
                    Messages.format("error.export.message", ex.getMessage())
            );
        }
    }
}


