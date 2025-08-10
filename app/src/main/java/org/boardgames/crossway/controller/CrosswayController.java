package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.*;
import org.boardgames.crossway.ui.BoardView;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Controller for Crossway. Manages user input, menu actions, and updates
 * the Game model and BoardView, ensuring board resizing works correctly.
 */
public class CrosswayController {
    private Game game;
    private BoardView view;
    private JFrame frame;
    private Stone currentPlayer = Stone.BLACK;  // BLACK starts by default
    private int boardSize;

    /**
     * Constructs controller with a custom board size.
     * @param boardSize board dimension (NxN)
     */
    public CrosswayController(int boardSize) {
        this.boardSize = boardSize;
        initializeGame();
        initView();
        bindEvents();
    }

    /**
     * Constructs controller using a predefined BoardSize.
     */
    public CrosswayController(BoardSize size) {
        this(size.size());
    }

    /**
     * Constructs controller with standard 19x19 board.
     */
    public CrosswayController() {
        this(BoardSize.REGULAR);
    }

    /**
     * Initializes the Game model and BoardView based on boardSize.
     */
    private void initializeGame() {
        game = new Game(new BoardSize(boardSize));
        view = new BoardView(game.getBoard());
    }

    /**
     * Builds and displays the main JFrame, assigning frame field.
     */
    private void initView() {
        frame = new JFrame("Crossway");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);          // ← Disable manual resizing
        frame.setJMenuBar(createMenuBar());
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Creates the menu bar with New Game, Restart, and Exit.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Left-aligned "Game" menu
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(e -> promptNewGame());
        JMenuItem restart = new JMenuItem("Restart");
        restart.addActionListener(e -> restartGame());
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));

        gameMenu.add(newGame);
        gameMenu.add(restart);
        gameMenu.add(exit);
        menuBar.add(gameMenu);

        // Push everything after this to the right
        menuBar.add(Box.createHorizontalGlue());

        // Right-aligned "Undo" button
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> undoMove());
        menuBar.add(undoButton);

        return menuBar;
    }

    private void undoMove() {
        try {
            game.undoLastMove();
            currentPlayer = currentPlayer.opposite();
            view.repaint();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    "No moves to undo.",
                    "Undo",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }


    /**
     * Attaches mouse listener to BoardView for placing stones.
     */
    private void bindEvents() {
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = toBoardPoint(e);
                try {
                    game.makeMove(new Move(p, currentPlayer));
                    view.repaint();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(
                            frame,
                            ex.getMessage(),
                            "Invalid Move",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                if (game.hasWon(currentPlayer)) {
                    int choice = JOptionPane.showOptionDialog(
                            frame,
                            currentPlayer + " wins!",
                            "Game Over",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            new String[]{"New Game", "Restart", "Exit"},
                            "Restart"
                    );
                    switch (choice) {
                        case 0 -> promptNewGame();  // New Game
                        case 1 -> restartGame();     // Restart
                        case 2, -1 -> System.exit(0); // Exit or close dialog
                    }
                } else {
                    currentPlayer = currentPlayer.opposite();
                }
            }
        });
    }

    /**
     * Converts MouseEvent coordinates to board Point.
     */
    private Point toBoardPoint(MouseEvent e) {
        int cell = view.getCellSize();
        return new Point(e.getX() / cell, e.getY() / cell);
    }

    /**
     * Prompts user to select a board size and starts a new game.
     */
    private void promptNewGame() {
        String[] options = {"Tiny (5x5)", "Small (9x9)", "Regular (19x19)", "Large (25x25)"};
        int sel = JOptionPane.showOptionDialog(
                frame,
                "Select board size:",
                "New Game",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]
        );
        if (sel < 0 || sel >= options.length) return;
        switch (sel) {
            case 0 -> boardSize = 5;
            case 1 -> boardSize = BoardSize.SMALL.size();
            case 2 -> boardSize = BoardSize.REGULAR.size();
            case 3 -> boardSize = BoardSize.LARGE.size();
        }
        restartGame();
    }

    /**
     * Restarts the game while retaining the current board size.
     */
    /**
     * Restarts the game while retaining the current board size.
     * Resizes the window to fit the new board view.
     */
    private void restartGame() {
        initializeGame();
        frame.getContentPane().removeAll();
        frame.add(view);
        frame.pack();  // Adjust window size to new board dimensions
        bindEvents();
        frame.revalidate();
        frame.repaint();
        currentPlayer = Stone.BLACK;
    }
}
