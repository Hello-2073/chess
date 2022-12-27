package core.model.chessman;

import core.model.ChessBoard;
import core.model.Step;
import java.awt.*;

public class Rook extends Chess{
    public static final String NAME = "Rook";

    public Rook(boolean isBlack) {
        super(isBlack, "Rook");
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
        else if(nx == x) {
            if(ny > y) {
                for(int i = 1; y + i < ny; i++) {
                    if(cb.ChessAt(x, y + i) != null) {
                        return false;
                    }
                }
            }
            else {
                for(int i = 1; y - i > ny; i++) {
                    if(cb.ChessAt(x, y - i) != null) {
                        return false;
                    }
                }
            }
        }
        else if(ny == y) {
            if(nx > x) {
                for(int i = 1; x + i < nx; i++) {
                    if(cb.ChessAt(x + i, y) != null) {
                        return false;
                    }
                }
            }
            else {
                for(int i = 1; x - i < nx; i++) {
                    if(cb.ChessAt(x - i, y) != null) {
                        return false;
                    }
                }
            }
        }
        else  {
            return false;
        }

        if (cb.ChessAt(nx, ny) == null) {
            stp.setEatenChess(null);
            stp.setEatenPosition(null);
            return true;
        }
        else if(cb.ChessAt(nx, ny).isBlack ^ this.isBlack) {
            stp.setEating(true);
            stp.setEatenChess(cb.ChessAt(nx, ny));
            stp.setEatenPosition(new Point(nx, ny));
            return true;
        }
        return false;
    }
}
