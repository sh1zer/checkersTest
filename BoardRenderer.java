import java.awt.*;
import java.awt.Point;
import java.util.List;

public class BoardRenderer {
    
    public void drawBoard(Graphics graphics, BoardLogic boardLogic, 
                         Point selectedSquare, List<Point> validMoves, 
                         List<Point> movablePieces) {
        int boardSize = boardLogic.getBoardSize();
        
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                int xPos = col * Constants.CELL_SIZE;
                int yPos = row * Constants.CELL_SIZE;
                
                drawBoardSquare(graphics, row, col, xPos, yPos);
                drawHighlights(graphics, row, col, xPos, yPos, selectedSquare, validMoves);
                drawPiece(graphics, boardLogic.getPiece(row, col), row, col, 
                         xPos, yPos, movablePieces);
            }
        }
    }
    
    private void drawBoardSquare(Graphics graphics, int row, int col, 
                                int xPos, int yPos) {
        if ((row + col) % 2 == 0) {
            graphics.setColor(Color.LIGHT_GRAY);
        } else {
            graphics.setColor(Color.DARK_GRAY);
        }
        graphics.fillRect(xPos, yPos, Constants.CELL_SIZE, Constants.CELL_SIZE);
    }
    
    private void drawHighlights(Graphics graphics, int row, int col, 
                               int xPos, int yPos, Point selectedSquare, 
                               List<Point> validMoves) {
        // Highlight selected square
        if (selectedSquare != null && selectedSquare.x == row && 
            selectedSquare.y == col) {
            graphics.setColor(Color.YELLOW);
            graphics.drawRect(xPos + 2, yPos + 2, Constants.CELL_SIZE - 4, Constants.CELL_SIZE - 4);
        }
        
        // Highlight valid moves
        if (validMoves.contains(new Point(row, col))) {
            graphics.setColor(Color.GREEN);
            graphics.fillOval(xPos + Constants.CELL_SIZE/2 - 5, yPos + Constants.CELL_SIZE/2 - 5, 
                            10, 10);
        }
    }
    
    private void drawPiece(Graphics graphics, Piece piece, int row, int col,
                          int xPos, int yPos, List<Point> movablePieces) {
        if (piece == null) return;
        
        // Draw piece outline for movable pieces
        if (movablePieces.contains(new Point(row, col))) {
            graphics.setColor(new Color(0, 150, 0, 180));
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval(xPos + 8, yPos + 8, Constants.CELL_SIZE - 16, Constants.CELL_SIZE - 16);
            g2d.setStroke(new BasicStroke(1));
        }
        
        if (piece.getPlayer() == Constants.PLAYER_1) {
            graphics.setColor(Color.RED);
        } else {
            graphics.setColor(Color.BLACK);
        }
        graphics.fillOval(xPos + 10, yPos + 10, Constants.CELL_SIZE - 20, Constants.CELL_SIZE - 20);
        
        if (piece.isKing()) {
            graphics.setColor(Color.YELLOW);
            graphics.fillOval(xPos + 25, yPos + 25, Constants.CELL_SIZE - 50, Constants.CELL_SIZE - 50);
        }
    }
}