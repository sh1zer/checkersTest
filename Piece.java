public class Piece {
    private int player;
    private boolean isKing;
    
    public Piece(int player, boolean isKing) {
        this.player = player;
        this.isKing = isKing;
    }
    
    public int getPlayer() { return player; }
    public boolean isKing() { return isKing; }
    public void makeKing() { isKing = true; }
}