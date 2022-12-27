package core.model.chessman;

import core.model.ChessBoard;
import core.model.Step;

import java.awt.*;

public class Queen extends Chess{
    public Queen(boolean isBlack) {
        super(isBlack, "Queen");
    }
    @Override
    public boolean moveable(Step stp, ChessBoard cb){
        int x = stp.getOldPosition().x;
        int y = stp.getOldPosition().y;
        int nx = stp.getNewPosition().x;
        int ny = stp.getNewPosition().y;

        if(nx < 0 || nx > 7 || ny < 0 || ny > 7 || (nx == x && ny == y)) {
            return false;
        }
        else if(nx == x){
            if(ny > y) {
                for(int i = 1; y + i < ny; i++) {
                    if(cb.ChessAt(x, y + i) != null) {
                        return false;
                    }
                }
            }
            else {
                for(int i = 1; y - i < nx; i++) {
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
                for(int i = 1; x - i > nx; i++) {
                    if(cb.ChessAt(x - i, y) != null) {
                        return false;
                    }
                }
            }
        }
        else if(nx + ny == x + y) {
            if(nx < x) {
                for(int i = 1; x - i > nx; i++) {
                    if(cb.ChessAt(x - i, y + i) != null) {
                        return false;
                    }
                }
            }
            else {
                for(int i = 1; x + i < nx; i++) {
                    if(cb.ChessAt(x + i, y - i) != null) {
                        return false;
                    }
                }
            }
        }
        else if(nx - ny == x - y) {
            if(nx > x) {
                for(int i = 1; x + i < nx; i++) {
                    if (cb.ChessAt(x + i, y + i) != null) {
                        return false;
                    }
                }
            }
            else {
                for(int i = 1; x - i > nx; i++) {
                    if(cb.ChessAt(x - i,y - i) != null) {
                        return false;
                    }
                }
            }
        }
        else {
            return false;
        }
        if (cb.ChessAt(nx, ny) == null ) {
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
