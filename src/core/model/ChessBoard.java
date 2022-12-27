package core.model;

import core.model.chessman.Chess;
import core.model.chessman.ChessFactory;

import java.util.Stack;

public class ChessBoard {
    private final Chess[][] board = new Chess[8][8];
    private Stack<Step> stepsBuffer = new Stack<Step>();
    private String winner = null;

    private static final String[][] stdBeginning =
            {       {"RookW",   "PawnW",    "", "", "", "",  "PawnB",    "RookB"     },
                    {"KnightW",  "PawnW",    "", "", "", "", "PawnB",    "KnightB"  },
                    {"BishopW",  "PawnW",    "", "", "", "", "PawnB",    "BishopB"  },
                    {"KingW",   "PawnW",    "", "", "", "",   "PawnB",    "KingB"   },
                    {"QueenW",    "PawnW",    "", "", "", "", "PawnB",    "QueenB"    },
                    {"BishopW",  "PawnW",    "", "", "", "", "PawnB",    "BishopB"  },
                    {"KnightW",  "PawnW",    "", "", "", "", "PawnB",    "KnightB"  },
                    {"RookW",    "PawnW",    "", "", "", "", "PawnB",    "RookB"    }   };

    public ChessBoard() {

    }

    public Step getLastStep() {
        return stepsBuffer.peek();
    }
    public boolean stepBufferEmpty() {
        return stepsBuffer.empty();
    }

    public void stdReset() {
        int i, j;
        this.winner = null;
        for(i = 0; i < 8; i++) {
            for(j = 0; j < 8; j++) {
                board[i][j] = ChessFactory.create(stdBeginning[i][j]);
            }
        }
    }

    public void StepAndRecord(Step s) {
        if (!s.isCastling()) {
            if(s.getMovedChess() != null &&s.getMovedChess().getName().equals("Pawn") && (s.getNewPosition().y == 0 || s.getNewPosition().y == 7)) {
                s.setNewChess(ChessFactory.create("Queen"+(s.getMovedChess().isBlack()?"B":"W")));
            }
            s.getMovedChess().setMoved();
            // 吃子
            if(s.isEating()) {
                if(s.getEatenChess().getName().equals("King")) {
                    this.winner = s.getEatenChess().isBlack()?"白方":"黑方";
                }
                board[s.getEatenPosition().x][s.getEatenPosition().y] = null;
            }
            // 移动（包含升变）
            board[s.getNewPosition().x][s.getNewPosition().y] = s.getNewChess();
            board[s.getNewPosition().x][s.getNewPosition().y].setMoved();
            board[s.getOldPosition().x][s.getOldPosition().y] = null;
        } else {
            if (s.isCastlingQueenSide()) {
                if (s.isBlack()) {
                    board[6][7] = board[3][7];
                    board[5][7] = board[7][7];
                    board[3][7] = null;
                    board[7][7] = null;
                    board[6][7].setMoved();
                    board[5][7].setMoved();
                } else {
                    board[6][0] = board[3][0];
                    board[5][0] = board[7][0];
                    board[3][0] = null;
                    board[7][0] = null;
                    board[6][0].setMoved();
                    board[5][0].setMoved();
                }
            } else {
                if (s.isBlack()) {
                    board[1][7] = board[3][7];
                    board[2][7] = board[0][7];
                    board[3][7] = null;
                    board[0][7] = null;
                    board[1][7].setMoved();
                    board[2][7].setMoved();
                } else {
                    board[1][0] = board[3][0];
                    board[2][0] = board[0][0];
                    board[3][0] = null;
                    board[0][0] = null;
                    board[1][0].setMoved();
                    board[2][0].setMoved();
                }
            }
        }

        stepsBuffer.push(s);

    }

    public void regret() {
        if(stepsBuffer.empty()) {
            return;
        }
        Step s = stepsBuffer.pop();
        if (!s.isCastling()) {
            // 恢复移动（包含升变）
            board[s.getOldPosition().x][s.getOldPosition().y] = s.getMovedChess();
            board[s.getOldPosition().x][s.getOldPosition().y].undo();
            board[s.getNewPosition().x][s.getNewPosition().y] = null;
            // 恢复吃子
            if(s.getEatenChess() != null){
                board[s.getEatenPosition().x][s.getEatenPosition().y] = s.getEatenChess();
            }
        } else {
            if (s.isCastlingQueenSide()) {
                if (s.isBlack()) {
                    board[3][7] = board[6][7];
                    board[7][7] = board[5][7];
                    board[6][7] = null;
                    board[5][7] = null;
                    board[3][7].undo();
                    board[7][7].undo();
                } else {
                    board[3][0] = board[6][0];
                    board[7][0] = board[5][0];
                    board[6][0] = null;
                    board[5][0] = null;
                    board[3][0].undo();
                    board[7][0].undo();
                }
            } else {
                if (s.isBlack()) {
                    board[3][7] = board[1][7];
                    board[0][7] = board[2][7];
                    board[1][7] = null;
                    board[2][7] = null;
                    board[3][7].undo();
                    board[0][7].undo();
                } else {
                    board[3][0] = board[1][0];
                    board[0][0] = board[2][0];
                    board[1][0] = null;
                    board[2][0] = null;
                    board[3][0].undo();
                    board[0][0].undo();
                }
            }
        }
    }

    public Chess ChessAt(int alpha, int digit) {
        return board[alpha][digit];
    }

    public String getWinner() {
        return winner;
    }
}
