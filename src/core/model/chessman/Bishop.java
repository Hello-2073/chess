package core.model.chessman;

import core.model.ChessBoard;
import core.model.Step;

public class Bishop extends Chess{
    public Bishop(boolean isBlack) {
        super(isBlack, "Bishop");
    }

    @Override
    public boolean moveable(Step stp, ChessBoard cb) {
        int x = stp.getOldPosition().x;
        int y = stp.getOldPosition().y;
        int nx = stp.getNewPosition().x;
        int ny = stp.getNewPosition().y;
        if(nx < 0 || nx > 7 || ny < 0 || ny > 7 || (x == nx && y == ny)) {
            return false;
        }
        else if(nx + ny == x + y) {
            if(nx > x) {
                for(int i = 1; x + i < nx; i++) {
                    if(cb.ChessAt(x + i, y - i) != null) {
                        return false;
                    }
                }
            }
            else if(nx < x) {
                for(int i = 1; x - i > nx; i++) {
                    if(cb.ChessAt(x - i, y + i) != null) {
                        return false;
                    }
                }
            }
        }
        else if(nx - ny == x - y) {
            if(nx > x) {
                for(int i = 1; x + i < nx; i++) {
                    if(cb.ChessAt(x + i, y + i) != null) {
                        return false;
                    }
                }
            }
            else if(nx < x) {
                for(int i = 1; x - i > nx; i++) {
                    if(cb.ChessAt(x - i, y - i) != null) {
                        return false;
                    }
                }
            }
        }
        else {
            return false;
        }
        if(cb.ChessAt(nx, ny) == null) {
            stp.setEatenChess(null);
            stp.setEatenPosition(null);
            return true;
        }
        else if(cb.ChessAt(nx, ny).isBlack() ^ this.isBlack) {
            stp.setEating(true);
            stp.setEatenChess(cb.ChessAt(nx, ny));
            stp.setEatenPosition(stp.getNewPosition());
            return true;
        }
        return false;
    }
}
