package com.chess.project.movegen;

import com.chess.project.Bitboard;
import com.chess.project.Board;

public class BitboardUtils {

    public static Bitboard drawLine(Bitboard a, Bitboard b) {
        int squareA = Long.numberOfTrailingZeros(a.bitboard);
        int squareB = Long.numberOfTrailingZeros(b.bitboard);

        int rankA = squareA / 8;
        int fileA = squareA % 8;
        int rankB = squareB / 8;
        int fileB = squareB % 8;

        Bitboard line = new Bitboard(0L);

        int rankDiff = rankB - rankA;
        int fileDiff = fileB - fileA;

        // Determine direction of line
        int rankStep = Integer.compare(rankDiff, 0); // -1, 0, or 1
        int fileStep = Integer.compare(fileDiff, 0); // -1, 0, or 1

        // Only draw line if direction is straight or diagonal
        if (Math.abs(rankDiff) == Math.abs(fileDiff) || rankA == rankB || fileA == fileB) {
            int r = rankA;
            int f = fileA;

            while (r != rankB || f != fileB) {
                int index = r * 8 + f;
                line.setBit(index);
                r += rankStep;
                f += fileStep;
            }

            // Set the final square
            line.setBit(squareB);
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
