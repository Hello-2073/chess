package core.model.chessman;

import core.model.ChessBoard;
import core.model.Step;

import java.awt.*;

public class King extends Chess{
    public King(boolean isBlack) {
        super(isBlack, "King");
    }

    @Override
    public boolean moveable(Step stp, ChessBoard cb) {
        int x = stp.getOldPosition().x;
        int y = stp.getOldPosition().y;
        int nx = stp.getNewPosition().x;
        int ny = stp.getNewPosition().y;
        if(nx < 0 || nx > 7 || ny < 0 || ny > 7 || (nx == x && ny == y)) {
            return false;
        }
        else if(x - 1 <= nx && nx <= x + 1 && y - 1 <= ny && ny <= y + 1 ) {
            if(cb.ChessAt(nx, ny) == null) {
                stp.setEating(false);
                stp.setEatenPosition(null);
                stp.setEatenChess(null);
                return true;
            }
            else if(cb.ChessAt(nx, ny).isBlack() ^ isBlack) {
                stp.setEatenPosition(new Point(nx, ny));
                stp.setEatenChess(cb.ChessAt(nx, ny));
                return true;
            }
            return false;
        }
        else if(!moved){
            if(this.isBlack() && nx == 6 && ny == 7) {
                if(cb.ChessAt(5, 7) == null
                        && cb.ChessAt(5, 7) == null
                        && cb.ChessAt(6, 7) == null) {
                    stp.setCastling(true);
                    stp.setCastlingKingSide(false);
                    stp.setCastlingQueenSide(true);
                    return true;
                }
                else {
                    return false;
                }
            }
            else if(this.isBlack() && nx == 1 && ny == 7){
                if(cb.ChessAt(1, 7) == null
                        && cb.ChessAt(2, 7) == null) {
                    stp.setCastling(true);
                    stp.setCastlingKingSide(true);
                    stp.setCastlingQueenSide(false);
                    return true;
                }
                else {
                    return false;
                }
            }
            else if(!this.isBlack() && nx == 6 && ny == 0) {
                if(cb.ChessAt(5, 0) == null
                        && cb.ChessAt(6, 0) == null
                        && cb.ChessAt(4, 0) == null) {
                    stp.setCastling(true);
                    stp.setCastlingKingSide(false);
                    stp.setCastlingQueenSide(true);
                    return true;
                }
                else {
                    return false;
                }
            }
            else if(!this.isBlack() && nx == 1 && ny == 0) {
                if(cb.ChessAt(1, 0) == null
                        && cb.ChessAt(2, 0) == null) {
                    stp.setCastling(true);
                    stp.setCastlingKingSide(true);
                    stp.setCastlingQueenSide(false);
                    return true;
                }
                else {
                    return false;
                }
            }
            return false;
        }
        return false;
    }
}
