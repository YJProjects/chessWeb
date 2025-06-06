package com.chess.project.movegen;

import com.chess.project.Bitboard;
import com.chess.project.Board;
import com.chess.project.enums.Color;

public class Bishop {
    public Bishop() {}

    public static Bitboard pseudoLegalMoves(Board board, int index, Color color) {
        Bitboard bishopBoard = new Bitboard(0L);
        bishopBoard.setBit(index);
        Bitboard enemyPieces = (color == Color.White) ? board.getBlackPieces() : board.getWhitePieces();
        Bitboard empty = board.getEmpty();

        Bitboard moves = Bishop.moves(board, bishopBoard);
        moves.bitboard &= (empty.bitboard | enemyPieces.bitboard);

        return moves;
    }

    public static Bitboard pseudoLegalMoves(Board board, Color color) {
        Bitboard bishopBoard = (color == Color.White) ? board.whiteBishops : board.blackBishops;
        Bitboard enemyPieces = (color == Color.White) ? board.getBlackPieces() : board.getWhitePieces();
        Bitboard empty = board.getEmpty();

        Bitboard moves = Bishop.moves(board, bishopBoard);
        moves.bitboard &= (empty.bitboard | enemyPieces.bitboard);

        return moves;
    }

    public static Bitboard moves(Board board, Bitboard bishopBoard) {
        Bitboard moves = new Bitboard(0L);
        Bitboard pieces = new Bitboard(board.getWhitePieces().bitboard | board.getBlackPieces().bitboard);

        for (int square = 0; square < 64; square++) {
            if (!bishopBoard.containsBit(square)) continue;

            int rank = square / 8;
            int file = square % 8;

            // Top-Right
            for (int r = rank + 1, f = file + 1; r < 8 && f < 8; r++, f++) {
                int index = r * 8 + f;
                moves.setBit(index);
                if (pieces.containsBit(index)) break;
            }

            // Top-Left
            for (int r = rank + 1, f = file - 1; r < 8 && f >= 0; r++, f--) {
                int index = r * 8 + f;
                moves.setBit(index);
                if (pieces.containsBit(index)) break;
            }

            // Bottom-Right
            for (int r = rank - 1, f = file + 1; r >= 0 && f < 8; r--, f++) {
                int index = r * 8 + f;
                moves.setBit(index);
                if (pieces.containsBit(index)) break;
            }

            // Bottom-Left
            for (int r = rank - 1, f = file - 1; r >= 0 && f >= 0; r--, f--) {
                int index = r * 8 + f;
                moves.setBit(index);
                if (pieces.containsBit(index)) break;
            }
        }

        return moves;
    }
}
