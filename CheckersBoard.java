import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class CheckersBoard extends JPanel {
    
    private BoardLogic boardLogic;
    private BoardRenderer boardRenderer;
    private GameController gameController;
    
    private Point selectedSquare = null;
    private List<Point> validMoves = new ArrayList<>();
    private List<Point> movablePieces = new ArrayList<>();
    
    public CheckersBoard(GameController controller) {
        this.gameController = controller;
        this.boardLogic = new BoardLogic();
        this.boardRenderer = new BoardRenderer();
        setupPanel();
    }
    
    private void setupPanel() {
        int boardSize = boardLogic.getBoardSize();
        setPreferredSize(new Dimension(boardSize * Constants.CELL_SIZE, boardSize * Constants.CELL_SIZE));
        addMouseListener(new BoardClickListener());
    }
    
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        boardRenderer.drawBoard(graphics, boardLogic, selectedSquare, 
                               validMoves, movablePieces);
    }
    
    public void updateMovablePieces(int currentPlayer) {
        movablePieces = boardLogic.getMovablePieces(currentPlayer, 
                                                   gameController.isInMultiJump(), 
                                                   gameController.getMultiJumpPiece());
        repaint();
    }
    
    public boolean pieceHasCaptures(int row, int col) {
        return boardLogic.pieceHasCaptures(row, col);
    }
    
    public boolean hasWon(int player) {
        return boardLogic.hasWon(player);
    }
    
    public int getPieceCount(int player) {
        return boardLogic.getPieceCount(player);
    }
    
    public void resetBoard() {
        boardLogic.resetBoard();
        clearSelection();
        repaint();
    }
    
    public void clearSelection() {
        selectedSquare = null;
        validMoves.clear();
    }
    
    public void setGameController(GameController controller) {
        this.gameController = controller;
    }
    
    private class BoardClickListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            if (gameController.isGameEnded()) return; 
            
            int col = mouseEvent.getX() / Constants.CELL_SIZE;
            int row = mouseEvent.getY() / Constants.CELL_SIZE;
            
            int boardSize = boardLogic.getBoardSize();
            if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) return;
            
            Point clickedSquare = new Point(row, col);
            
            if (selectedSquare == null) {
                Piece piece = boardLogic.getPiece(row, col);
                if (piece != null && piece.getPlayer() == gameController.getCurrentPlayer()) {
                    if (gameController.isInMultiJump()) {
                        Point requiredPiece = gameController.getMultiJumpPiece();
                        if (requiredPiece.equals(clickedSquare)) {
                            selectedSquare = clickedSquare;
                            validMoves = boardLogic.getValidMoves(row, col);
                        }
                    } else {
                        selectedSquare = clickedSquare;
                        validMoves = boardLogic.getValidMoves(row, col);
                    }
                }
            } else {
                if (validMoves.contains(clickedSquare)) {
                    boolean wasCapture = boardLogic.makeMove(selectedSquare, clickedSquare);
                    gameController.handleMove(selectedSquare, clickedSquare, wasCapture);
                }
                clearSelection();
            }
            
            repaint();
        }
    }
}