package core.model.chessman;

// 简单工厂

public class ChessFactory {
    private ChessFactory(){};

    public static Chess create(String name) {
        if(name.equals("KingW")) {
            return new King(false);
        }
        else if(name.equals("KingB")) {
            return new King(true);
        }
        else if(name.equals("QueenW")) {
            return new Queen(false);
        }
        else if(name.equals("QueenB")) {
            return new Queen(true);
        }
        else if(name.equals("BishopW")) {
            return new Bishop(false);
        }
        else if(name.equals("BishopB")) {
            return new Bishop(true);
        }
        else if(name.equals("KnightW")) {
            return new Knight(false);
        }
        else if(name.equals("KnightB")) {
            return new Knight(true);
        }
        else if(name.equals("RookW")) {
            return new Rook(false);
        }
        else if(name.equals("RookB")) {
            return new Rook(true);
        }
        else if(name.equals("PawnW")) {
            return new Pawn(false);
        }
        else if(name.equals("PawnB")) {
            return new Pawn(true);
        }
        else {
            return null;
        }
    }
}
