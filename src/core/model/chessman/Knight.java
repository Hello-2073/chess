package core.model.chessman;

import core.model.ChessBoard;
import core.model.Step;

import java.awt.*;

public class Knight extends Chess{
    public Knight(boolean isBlack) {
        super(isBlack, "Knight");
    }

    @Override
    public boolean moveable(Step stp, ChessBoard cb){
        int x = stp.getOldPosition().x;
        int y = stp.getOldPosition().y;
        int nx = stp.getNewPosition().x;
        int ny = stp.getNewPosition().y;
        if(nx < 0 || nx > 7 || ny < 0 || ny > 7) {
            return false;
        }
        else if(  (nx == x - 1 && ny == y + 2) ||
                  (nx == x + 1 && ny == y + 2) ||
                  (nx == x + 2 && ny == y + 1) ||
                  (nx == x + 2 && ny == y - 1) ||
                  (nx == x + 1 && ny == y - 2) ||
                  (nx == x - 1 && ny == y - 2) ||
                  (nx == x - 2 && ny == y - 1) ||
                  (nx == x - 2 && ny == y + 1) ){
            if(cb.ChessAt(nx, ny) == null) {
                stp.setEatenPosition(null);
                stp.setEatenChess(null);
                return true;
            }
            else if(cb.ChessAt(nx, ny).isBlack ^ isBlack) {
                stp.setEating(true);
                stp.setEatenPosition(new Point(nx, ny));
                stp.setEatenChess(cb.ChessAt(nx, ny));
                return true;
            }
        }
        return false;
    }
}
