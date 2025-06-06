package com.chess.project.movegen;

import com.chess.project.Bitboard;
import com.chess.project.Board;
import com.chess.project.enums.Color;

public class Queen {
    public Queen() {}

    public static Bitboard pseudoLegalMoves(Board board, int index, Color color) {
        Bitboard queenBoard = new Bitboard(0L);
        queenBoard.setBit(index);
        Bitboard enemyPieces = new Bitboard(0L);
        Bitboard empty = board.getEmpty();

        if (color == Color.White) {
            enemyPieces = board.getBlackPieces();
        } else {
            enemyPieces = board.getWhitePieces();
        }

        Bitboard moves = moves(board, queenBoard);
        moves.bitboard &= (empty.bitboard | enemyPieces.bitboard);

        return moves;
    }

    public static Bitboard pseudoLegalMoves(Board board, Color color) {
        Bitboard queenBoard = new Bitboard(0L);
        Bitboard enemyPieces = new Bitboard(0L);
        Bitboard empty = board.getEmpty();

        if (color == Color.White) {
            queenBoard = board.whiteQueens;
            enemyPieces = board.getBlackPieces();
        } else {
            queenBoard = board.blackQueens;
            enemyPieces = board.getWhitePieces();
        }

        Bitboard moves = moves(board, queenBoard);
        moves.bitboard &= (empty.bitboard | enemyPieces.bitboard);

        return moves;
    }

    public static Bitboard moves(Board board, Bitboard queenBoard) {
        Bitboard moves = new Bitboard(0L);
        Bitboard pieces = new Bitboard(board.getWhitePieces().bitboard | board.getBlackPieces().bitboard);

        for (int square = 0; square < 64; square++) {
            if (!queenBoard.containsBit(square)) continue;

            int rank = square / 8;
            int file = square % 8;

            // Rook-like directions (Straight)

            // Up
            for (int r = rank + 1; r < 8; r++) {
                int index = r * 8 + file;
                moves.setBit(index);
                if (pieces.containsBit(index)) break;
            }

            // Down
            for (int r = rank - 1; r >= 0; r--) {
                int index = r * 8 + file;
                moves.setBit(index);
                if (pieces.containsBit(index)) break;
            }

            // Right
            for (int f = file + 1; f < 8; f++) {
                int index = rank * 8 + f;
                moves.setBit(index);
                if (pieces.containsBit(index)) break;
            }

            // Left
            for (int f = file - 1; f >= 0; f--) {
                int index = rank * 8 + f;
                moves.setBit(index);
                if (pieces.containsBit(index)) break;
            }

            // Bishop-like directions (Diagonals)

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
