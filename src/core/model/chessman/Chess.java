package core.model.chessman;

import core.model.ChessBoard;
import core.model.Step;

import java.io.File;
import java.io.Serializable;

public abstract class Chess implements Serializable {
    String name;
    String img_path;
    boolean isBlack;
    boolean moved;

    int movedTimes = 0;

    public Chess(boolean isBlack, String name) {
        // 初始化信息
        this.name = name;
        this.img_path = "pic"+File.separator+"Piece"+File.separator+name+(isBlack?"B":"W")+".png";
        this.isBlack = isBlack;
        this.moved = false;
    }

    // get
    public String getName() {
        return this.name;
    }

    public String getImg_path() {
        return img_path;
    }

    public boolean isBlack() {
        return isBlack;
    }
    // set
    public void setMoved() {
        this.movedTimes++;
        this.moved = true;
    }

    public void undo() {
        if(this.moved){
            this.movedTimes--;
            this.moved = movedTimes == 0;
        }
    }

    //
    public abstract boolean moveable(Step stp, ChessBoard cb);

    @Override
    public String toString() {
        return "Chess{" +
                "name='" + name + '\'' +
                ", img_path='" + img_path + '\'' +
                ", isBlack=" + isBlack +
                ", moved=" + moved +
                ", movedTimes=" + movedTimes +
                '}';
    }
}
