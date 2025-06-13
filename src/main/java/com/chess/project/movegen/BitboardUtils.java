package com.chess.project.movegen;

import com.chess.project.Bitboard;
import com.chess.project.Board;

public class BitboardUtils {

    /**
     * Draw Line from a to b
     */
    public static Bitboard drawLine(int a, int b) {
        
        int direction = (a < b)? 1 : -1;
        int increment = (a - b > 7 ||b - a > 7)? 8 : 1;
        
        int stepValue = direction * increment;

        Bitboard line = new Bitboard(0L);

        while (a != b) {
            a += stepValue;
            line.setBit(a);
        }
        
        return line;
    }

    /**
    * Draw Line from a to b
    */
    public static Bitboard drawDiagonal(int a, int b) {
        
        int startColumn = a % 8;
        int endColumn = b % 8;

        int startRow = Math.floorDiv(a, 8);
        int endRow = Math.floorDiv(b, 8);

        int rowIncrement = (int)Math.signum(endRow - startRow);
        int columnIncrement = (int)Math.signum(endColumn - startColumn);

        Bitboard line = new Bitboard(0L);

        while (a != b) {
            startColumn += columnIncrement;
            startRow += rowIncrement;
            a = (startRow * 8) + startColumn;
            line.setBit(a);
        }
        
        return line;
    }



    public static boolean isSingleSlidingPiece(Bitboard bb, Board board) {
        if (bb.bitboard == 0L || (bb.bitboard & (bb.bitboard - 1)) != 0) {
            // Not a single bit set (either 0 or multiple bits)
            return false;
        }

        int index = Long.numberOfTrailingZeros(bb.bitboard);
        String pieceType = board.getPieceType(index);

        return pieceType.contains("Rook") || pieceType.contains("Bishop") || pieceType.contains("Queen");
    }
    
}
