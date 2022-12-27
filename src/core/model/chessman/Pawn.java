package core.model.chessman;

import core.model.ChessBoard;
import core.model.Step;

import java.awt.*;

public class Pawn extends Chess{
    public Pawn(boolean isBlack) {
        super(isBlack, "Pawn");
    }

    public boolean moveable(Step stp, ChessBoard cb) {
        int x = stp.getOldPosition().x;
        int y = stp.getOldPosition().y;
        int nx = stp.getNewPosition().x;
        int ny = stp.getNewPosition().y;
        Point passant = null;
        if(!cb.stepBufferEmpty()) {
            passant = cb.getLastStep().getPassant();
        }
        if(nx < 0 || nx > 7 || ny < 0 || ny > 7 || (nx == x && ny == y)) {
            return false;
        }
        else if(this.isBlack) {
            System.out.println("Judging Black Pawn"+"x:"+x+" y:"+y+" nx:"+nx+" ny"+ny);
            if(nx == x && ny == y - 1 && cb.ChessAt(nx, ny) == null) {
                // 兵前进
                stp.setEatenPosition(null);
                stp.setEatenChess(null);
                return true;
            }
            else if((nx == x - 1 || nx == x + 1) && ny == y - 1 && cb.ChessAt(nx, ny) != null
                    && cb.ChessAt(nx, ny).isBlack != isBlack) {
                // 兵斜吃子
                stp.setEating(true);
                stp.setEatenPosition(new Point(nx, ny));
                stp.setEatenChess(cb.ChessAt(nx, ny));
                return true;
            }
            else if(passant != null && nx == passant.x && ny == passant.y - 1
                    && (x == nx - 1 || x == nx + 1) && y == ny + 1) {
                stp.setEating(true);
                stp.setEatenChess(cb.ChessAt(passant.x, passant.y));
                stp.setEatenPosition(passant);
                return true;
            }
            else if(!moved && nx == x && ny == y - 2 && cb.ChessAt(nx, ny) == null
                    && cb.ChessAt(x, y - 1) == null) {
                stp.setPassant(new Point(nx, ny));
                stp.setEatenPosition(null);
                stp.setEatenChess(null);
                return true;
            }
        }
        else{
            if(nx == x && ny == y + 1 && cb.ChessAt(nx, ny) == null) {
                stp.setEatenPosition(null);
                stp.setEatenChess(null);
                return true;
            }
            else if((nx == x - 1 || nx == x + 1) && ny == y + 1 && cb.ChessAt(nx, ny) != null
                    && cb.ChessAt(nx, ny).isBlack != isBlack()) {
                // 兵斜吃子
                stp.setEating(true);
                stp.setEatenPosition(new Point(nx, ny));
                stp.setEatenChess(cb.ChessAt(nx, ny));
                return true;
            }
            else if(passant != null && nx == passant.x && ny == passant.y + 1
                    && (x == nx - 1 || x == nx + 1) && y == ny - 1) {
                // 吃过路兵
                stp.setEating(true);
                stp.setEatenChess(cb.ChessAt(passant.x, passant.y));
                stp.setEatenPosition(passant);
                return true;
            }
            else if(!moved && nx == x && ny == y + 2 && cb.ChessAt(nx, ny) == null
                    && cb.ChessAt(x, y + 1) == null){
                // 兵的冲锋
                stp.setPassant(new Point(nx, ny));
                stp.setEatenPosition(null);
                stp.setEatenChess(null);
                return true;
            }
        }
        return false;
    }
}
