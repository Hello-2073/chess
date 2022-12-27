package core.model;

import core.model.chessman.Chess;

import java.awt.*;
import java.io.Serializable;

public class Step implements Serializable {
    private int num;                          // 第几步
    private boolean isBlack;
    private boolean isCastling;               // 王车易位
    private boolean isCastlingKingSide;       // 1. 王车短易位
    private boolean isCastlingQueenSide;      // 2. 王车长易位

    private Point passant;                    // 此位置产生了一个过路兵

    private Chess movedChess;       //
    private Chess newChess;         // 在兵的升变时使用
    private Point oldPosition;
    private Point newPosition;

    private boolean eating = false;
    private Chess eatenChess;       // 吃过路兵时这个位置比较特殊
    private Point eatenPosition;

    private boolean isLegal;

    public Step() {}

    public Step(int num, boolean isBlack, Chess movedChess, Point oldPostion, Point newPostion, ChessBoard chessBoard) {
        this.num = num;
        this.isBlack = movedChess.isBlack();
        this.movedChess = movedChess;
        this.oldPosition = oldPostion;
        this.newChess = movedChess;
        this.newPosition = newPostion;
        this.isLegal = movedChess.moveable(this, chessBoard);
    }

    // get 方法
    public int getNum() {return num;}
    public boolean isBlack() {return isBlack;}
    public Chess getMovedChess() {return isCastling ? null :this.movedChess;}
    public Point getOldPosition() {return isCastling ? null : this.oldPosition;}
    public Chess getNewChess() {return isCastling ? null : newChess;}
    public Point getNewPosition() {return isCastling ? null : this.newPosition;}
    public Chess getEatenChess() {return this.eatenChess;}
    public Point getEatenPosition() {return eatenPosition;}
    public Point getPassant() {return passant;}
    public boolean isCastling() {return isCastling;}
    public boolean isCastlingKingSide() {return isCastlingKingSide;}
    public boolean isCastlingQueenSide() {return isCastlingQueenSide;}
    public boolean isLegal() {return isLegal;}
    public boolean isEating() {return eating;}

    // set 方法
    public void setMovedChess(Chess movedChess) {this.movedChess = movedChess;}
    public void setOldPosition(Point oldPosition) {this.oldPosition =oldPosition;}
    public void setNewChess(Chess newChess) {this.newChess = newChess;}
    public void setNewPosition(Point newPosition) {this.newPosition = newPosition;}
    public void setEatenChess(Chess eatenChess) {this.eatenChess = eatenChess;}
    public void setEatenPosition(Point eatenPosition) {this.eatenPosition = eatenPosition;}
    public void setCastling(boolean isCastling) {this.isCastling = isCastling;}
    public void setCastlingKingSide(boolean isCastlingKingSide) {this.isCastlingKingSide = isCastlingKingSide;}
    public void setCastlingQueenSide(boolean isCastlingQueenSide) {this.isCastlingQueenSide = isCastlingQueenSide;}
    public void setLegal(boolean isLegal) {this.isLegal = isLegal;}
    public void setEating(boolean eating) {this.eating = eating;}
    public void setPassant(Point passant) {this.passant = passant;}

    @Override
    public String toString() {
        return "Step{" +
                "num=" + num +
                ", isBlack=" + isBlack +
                ", isCastling=" + isCastling +
                ", isCastlingKingSide=" + isCastlingKingSide +
                ", isCastlingQueenSide=" + isCastlingQueenSide +
                ", passant=" + passant +
                ", movedChess=" + movedChess +
                ", newChess=" + newChess +
                ", oldPosition=" + oldPosition +
                ", newPosition=" + newPosition +
                ", eating=" + eating +
                ", eatenChess=" + eatenChess +
                ", eatenPosition=" + eatenPosition +
                ", isLegal=" + isLegal +
                '}';
    }
}
