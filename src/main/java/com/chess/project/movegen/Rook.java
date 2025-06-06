package com.chess.project.movegen;

import com.chess.project.Bitboard;
import com.chess.project.Board;
import com.chess.project.enums.Color;

public class Rook {
    public Rook() {}


    public static Bitboard pseudoLegalMoves(Board board, int index, Color color) {

        Bitboard rookBoard = new Bitboard(0L);
        rookBoard.setBit(index);
        Bitboard enemyPieces = new Bitboard(0L);
        Bitboard empty = board.getEmpty();

        if (color == Color.White) {
            enemyPieces = board.getBlackPieces();
        }
        else {
            enemyPieces = board.getWhitePieces();

        }

        Bitboard moves = moves(board, rookBoard);
        moves.bitboard &= (empty.bitboard | enemyPieces.bitboard);

        return moves;
    }
    

   public static Bitboard pseudoLegalMoves(Board board, Color color) {

        Bitboard rookBoard = new Bitboard(0L);
        Bitboard enemyPieces = new Bitboard(0L);
        Bitboard empty = board.getEmpty();

        if (color == Color.White) {
            rookBoard = board.whiteRooks;
            enemyPieces = board.getBlackPieces();
        }
        else {
            rookBoard = board.blackRooks;
            enemyPieces = board.getWhitePieces();

        }

        Bitboard moves = moves(board, rookBoard);
        moves.bitboard &= (empty.bitboard | enemyPieces.bitboard);

        return moves;
    }


    public static Bitboard moves(Board board, Bitboard rookBoard) {
    Bitboard moves = new Bitboard(0L);
    Bitboard pieces = new Bitboard(board.getWhitePieces().bitboard | board.getBlackPieces().bitboard);

    for (int square = 0; square < 64; square++) {
        if (!rookBoard.containsBit(square)) continue;

        int rank = square / 8;
        int file = square % 8;

        // Up (rank + 1)
        for (int r = rank + 1; r < 8; r++) {
            int index = r * 8 + file;
            moves.setBit(index);
            if (pieces.containsBit(index)) break;
        }

        // Down (rank - 1)
        for (int r = rank - 1; r >= 0; r--) {
            int index = r * 8 + file;
            moves.setBit(index);
            if (pieces.containsBit(index)) break;
        }

        // Right (file + 1)
        for (int f = file + 1; f < 8; f++) {
            int index = rank * 8 + f;
            moves.setBit(index);
            if (pieces.containsBit(index)) break;
        }

        // Left (file - 1)
        for (int f = file - 1; f >= 0; f--) {
            int index = rank * 8 + f;
            moves.setBit(index);
            if (pieces.containsBit(index)) break;
        }
    }

    return moves;
}

}
