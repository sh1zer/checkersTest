import javax.swing.*;
import java.awt.*;

public class CheckersGame extends JFrame {
    
    public CheckersGame() {
        setupGUI();
    }
    
    /*private void showGameModeDialog() {
        String[] options = {"Local Game", "Host Network Game", "Join Network Game"};
        int choice = JOptionPane.showOptionDialog(null, 
            "Select game mode:", "Checkers", 
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
            null, options, options[0]);
            
        switch (choice) {
            case 1: startNetworkHost(); break;
            case 2: joinNetworkGame(); break;
            default: // Local game - no changes needed
        }
    }*/
    
    private void setupGUI() {
        setTitle("Checkers Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Bottom fo screen text
        JLabel statusLabel = new JLabel("Player 1's Turn", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel timerLabel = new JLabel("Time: 0s", JLabel.CENTER);
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Game initialization
        CheckersBoard boardPanel = new CheckersBoard(null);
        GameController gameController = new GameController(statusLabel, timerLabel);
        
        boardPanel.setGameController(gameController);
        
        gameController.setBoard(boardPanel);
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.add(statusLabel);
        infoPanel.add(timerLabel);
        
        add(boardPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CheckersGame().setVisible(true);
        });
    }
}