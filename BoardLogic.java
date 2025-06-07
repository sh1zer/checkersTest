import java.awt.Point;
import java.util.List;
import java.util.ArrayList;

public class BoardLogic {
    private Piece[][] board = new Piece[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
    
    public BoardLogic() {
        initializeBoard();
    }
    
    private void initializeBoard() {
        // Place player 1 pieces (top, red)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                if ((row + col) % 2 == 1) {
                    board[row][col] = new Piece(Constants.PLAYER_1, false);
                }
            }
        }
        
        // Place player 2 pieces (bottom, black)
        for (int row = 5; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                if ((row + col) % 2 == 1) {
                    board[row][col] = new Piece(Constants.PLAYER_2, false);
                }
            }
        }
    }
    
    public Piece getPiece(int row, int col) {
        if (!isValidSquare(row, col)) return null;
        return board[row][col];
    }
    
    public boolean hasAvailableCaptures(int player) {
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getPlayer() == player) {
                    if (!getCaptureMoves(row, col).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    // Check if a specific piece has captures
    public boolean pieceHasCaptures(int row, int col) {
        return !getCaptureMoves(row, col).isEmpty();
    }
    
    // Check if a move is a capture
    public boolean isCapture(Point from, Point to) {
        return Math.abs(to.x - from.x) == 2;
    }
    
    private List<Point> getMoves(int row, int col, boolean capturesOnly) {
        List<Point> moves = new ArrayList<>();
        Piece piece = board[row][col];
        if (piece == null) return moves;
        
        int[][] directions = getDirections(piece);
        
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            
            if (!isValidSquare(newRow, newCol)) continue;
            
            if (board[newRow][newCol] == null && !capturesOnly) {
                // Regular move
                moves.add(new Point(newRow, newCol));
            } else if (board[newRow][newCol] != null && 
                       board[newRow][newCol].getPlayer() != piece.getPlayer()) {
                // Check for capture
                int jumpRow = newRow + dir[0];
                int jumpCol = newCol + dir[1];
                if (isValidSquare(jumpRow, jumpCol) && board[jumpRow][jumpCol] == null) {
                    moves.add(new Point(jumpRow, jumpCol));
                }
            }
        }
        
        return moves;
    }
    
    public List<Point> getCaptureMoves(int row, int col) {
        return getMoves(row, col, true);
    }
    
    public List<Point> getRegularMoves(int row, int col) {
        return getMoves(row, col, false);
    }
    
    public List<Point> getValidMoves(int row, int col) {
        Piece piece = board[row][col];
        if (piece == null) return new ArrayList<>();
        
        boolean capturesAvailable = hasAvailableCaptures(piece.getPlayer());
        
        if (capturesAvailable) {
            return getCaptureMoves(row, col);
        } else {
            return getRegularMoves(row, col);
        }
    }
    
    // Updated getMovablePieces to handle multi-jump
    public List<Point> getMovablePieces(int player, boolean inMultiJump, Point multiJumpPiece) {
        List<Point> movable = new ArrayList<>();
        
        if (inMultiJump && multiJumpPiece != null) {
            // Only the jumping piece can move
            if (!getCaptureMoves(multiJumpPiece.x, multiJumpPiece.y).isEmpty()) {
                movable.add(multiJumpPiece);
            }
            return movable;
        }
        
        // Normal movable pieces logic
        boolean capturesAvailable = hasAvailableCaptures(player);
        
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getPlayer() == player) {
                    List<Point> moves;
                    if (capturesAvailable) {
                        moves = getCaptureMoves(row, col);
                    } else {
                        moves = getRegularMoves(row, col);
                    }
                    
                    if (!moves.isEmpty()) {
                        movable.add(new Point(row, col));
                    }
                }
            }
        }
        return movable;
    }
    
    // Keep original method for backward compatibility
    public List<Point> getMovablePieces(int player) {
        return getMovablePieces(player, false, null);
    }
    
    // Updated makeMove to return if it was a capture
    public boolean makeMove(Point from, Point to) {
        Piece piece = board[from.x][from.y];
        board[to.x][to.y] = piece;
        board[from.x][from.y] = null;
        
        boolean wasCapture = false;
        
        // Handle captures
        if (Math.abs(to.x - from.x) == 2) {
            int capturedRow = (from.x + to.x) / 2;
            int capturedCol = (from.y + to.y) / 2;
            board[capturedRow][capturedCol] = null;
            wasCapture = true;
        }
        
        // Check for king promotion
        if ((piece.getPlayer() == Constants.PLAYER_1 && to.x == Constants.BOARD_SIZE - 1) ||
            (piece.getPlayer() == Constants.PLAYER_2 && to.x == 0)) {
            piece.makeKing();
        }
        
        return wasCapture;
    }
    
    private int[][] getDirections(Piece piece) {
        if (piece.isKing()) return Constants.KING_DIRECTIONS;
        return piece.getPlayer() == Constants.PLAYER_1 ? 
               Constants.PLAYER_1_DIRECTIONS : Constants.PLAYER_2_DIRECTIONS;
    }
    
    private boolean isValidSquare(int row, int col) {
        return row >= 0 && row < Constants.BOARD_SIZE && col >= 0 && col < Constants.BOARD_SIZE;
    }
    
    public int getBoardSize() {
        return Constants.BOARD_SIZE;
    }

    public boolean hasWon(int player) {
        int opponent = (player == Constants.PLAYER_1) ? Constants.PLAYER_2 : Constants.PLAYER_1;
        return !hasPieces(opponent) || !hasLegalMoves(opponent);
    }
    public void resetBoard() {
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                board[row][col] = null;
            }
        }
        
        // Reinitialize
        initializeBoard();
    }

    private boolean hasPieces(int player) {
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getPlayer() == player) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasLegalMoves(int player) {
        return !getMovablePieces(player).isEmpty();
    }

    public int getPieceCount(int player) {
        int count = 0;
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getPlayer() == player) {
                    count++;
                }
            }
        }
        return count;
    }
}