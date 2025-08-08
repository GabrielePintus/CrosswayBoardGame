// CrosswayController.java
package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.BoardSize;
import org.boardgames.crossway.model.Game;
import org.boardgames.crossway.model.Point;
import org.boardgames.crossway.model.Stone;
import org.boardgames.crossway.ui.BoardView;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Controller: handles user input and updates model and view.
 */
public class CrosswayController {
    private final Game game;
    private final BoardView view;
    private Stone currentPlayer = Stone.BLACK;

    public CrosswayController(int boardSize) {
        // Initialize model and view
        this.game = new Game(new BoardSize(boardSize));
        this.view = new BoardView(game.getBoard());
        initView();
        bindEvents();
    }
    public CrosswayController(BoardSize boardSize) {
        // Initialize model and view
        this.game = new Game(boardSize);
        this.view = new BoardView(game.getBoard());
        initView();
        bindEvents();
    }
    public CrosswayController() {
        this.game = new Game(BoardSize.REGULAR);
        this.view = new BoardView(game.getBoard());
        initView();
        bindEvents();
    }

    /**
     * Sets up and displays the main window.
     */
    private void initView() {
        JFrame frame = new JFrame("Crossway");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /** Initializes the view and binds events.
     *  This method is called after the view is created.
     */
    public void init() {
        initView();
        bindEvents();
    }


    /**
     * Attaches mouse listener to the board view for handling clicks.
     */
    private void bindEvents() {
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = getPointFromMouse(e);
                makeMove(p);
                boolean win = game.hasWon(currentPlayer);
                if (win) {
                    // Popup that tells the user who won
                    // Two buttons: "New Game" and "Exit"
                    String message = currentPlayer + " wins!";
                    int option = JOptionPane.showOptionDialog(view, message, "Game Over",
                            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                            null, new String[]{"New Game", "Exit"}, "New Game");
                    if (option == JOptionPane.YES_OPTION) {
                        // Start a new game
                        game.reset();
                        view.repaint();
                        currentPlayer = Stone.BLACK;
                    } else {
                        // Exit the application
                        System.exit(0);
                    }
                }else{
                    currentPlayer = currentPlayer.opposite();
                }
            }
        });
    }

    private void makeMove(Point p) {
        try {
            game.placeStone(p, currentPlayer);
            view.repaint();
        } catch (IllegalArgumentException ex) {
            // Click outside bounds or invalid move: ignore
            alert(ex.getMessage());
        }
    }


    private Point getPointFromMouse(MouseEvent e) {
        int cellSize = view.getCellSize();
        int col = e.getX() / cellSize;
        int row = e.getY() / cellSize;
        return new Point(col, row);
    }

    private void alert(String message) {
        JOptionPane.showMessageDialog(view, message, "Alert", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CrosswayController(11));
    }
}
