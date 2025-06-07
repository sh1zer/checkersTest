import javax.swing.*;
import java.awt.Point;

public class GameController {
    private boolean isPlayer1Turn = true;
    private JLabel statusLabel;
    private JLabel timerLabel;
    private javax.swing.Timer moveTimer;
    private long moveStartTime;
    private boolean gameEnded = false;
    
    private boolean inMultiJump = false;
    private Point multiJumpPiece = null;
    private CheckersBoard board;
    
    public GameController(JLabel statusLabel, JLabel timerLabel) {
        this.statusLabel = statusLabel;
        this.timerLabel = timerLabel;
        startMoveTimer();
    }
    
    public void setBoard(CheckersBoard board) {
        this.board = board;
        board.updateMovablePieces(getCurrentPlayer());
    }
    
    public int getCurrentPlayer() {
        return isPlayer1Turn ? 1 : 2;
    }
    
    public boolean isInMultiJump() {
        return inMultiJump;
    }
    
    public Point getMultiJumpPiece() {
        return multiJumpPiece;
    }
    
    public boolean isGameEnded() {
        return gameEnded;
    }
    
    public void handleMove(Point from, Point to, boolean wasCapture) {
        if (gameEnded) return;
        
        if (wasCapture && board.pieceHasCaptures(to.x, to.y)) {
            inMultiJump = true;
            multiJumpPiece = to;
            statusLabel.setText("Player " + getCurrentPlayer() + " must continue jumping!");
            board.updateMovablePieces(getCurrentPlayer());
        } else {
            endTurn();
            checkForVictory();
        }
    }
    
    private void checkForVictory() {
        if (board.hasWon(getCurrentPlayer())) {
            gameEnded = true;
            moveTimer.stop();
            showVictoryScreen(getCurrentPlayer());
        } else if (board.hasWon(getOpponent())) {
            gameEnded = true;
            moveTimer.stop();
            showVictoryScreen(getOpponent());
        }
    }
    
    private int getOpponent() {
        return isPlayer1Turn ? 2 : 1;
    }
    
    private void showVictoryScreen(int winner) {
        String winnerName = "Player " + winner;
        int player1Count = board.getPieceCount(Constants.PLAYER_1);
        int player2Count = board.getPieceCount(Constants.PLAYER_2);
        
        String message = String.format(
            "%s Wins!\n\n" +
            "Final Score:\n" +
            "Player 1: %d pieces\n" +
            "Player 2: %d pieces\n\n" +
            "Play again?",
            winnerName, player1Count, player2Count
        );
        
        int choice = JOptionPane.showConfirmDialog(
            null,
            message,
            "Game Over",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }
    
    private void restartGame() {
        gameEnded = false;
        inMultiJump = false;
        multiJumpPiece = null;
        isPlayer1Turn = true;
        
        board.resetBoard();
        statusLabel.setText("Player 1's Turn");
        board.updateMovablePieces(getCurrentPlayer());
        startMoveTimer();
    }
    
    public void endTurn() {
        inMultiJump = false;
        multiJumpPiece = null;
        isPlayer1Turn = !isPlayer1Turn;
        statusLabel.setText("Player " + getCurrentPlayer() + "'s Turn");
        
        long moveTime = (System.currentTimeMillis() - moveStartTime) / 1000;
        System.out.println("Move took: " + moveTime + " seconds");
        
        board.updateMovablePieces(getCurrentPlayer());
        startMoveTimer();
    }
    
    private void startMoveTimer() {
        if (moveTimer != null) {
            moveTimer.stop();
        }
        
        moveStartTime = System.currentTimeMillis();
        moveTimer = new javax.swing.Timer(100, timerEvent -> {
            long elapsed = (System.currentTimeMillis() - moveStartTime) / 1000;
            timerLabel.setText("Time: " + elapsed + "s");
        });
        moveTimer.start();
    }
}