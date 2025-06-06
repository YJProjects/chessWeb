package com.chess.project.movegen;

import com.chess.project.Bitboard;
import com.chess.project.Board;
import com.chess.project.enums.Color;

public class Queen {

    public Queen() {
    }

    public static Bitboard pseudoLegalMoves(Board board, int index, Color color) {
        Bitboard rookMoves = Rook.pseudoLegalMoves(board, index, color);
        Bitboard bishopMoves = Bishop.pseudoLegalMoves(board, index, color);
        return new Bitboard(rookMoves.bitboard | bishopMoves.bitboard);
    }

    public static Bitboard pseudoLegalMoves(Board board, Color color) {
        Bitboard rookMoves = Rook.pseudoLegalMoves(board, color);
        Bitboard bishopMoves = Bishop.pseudoLegalMoves(board, color);
        return new Bitboard(rookMoves.bitboard | bishopMoves.bitboard);
    }

    public static Bitboard moves(Board board, Bitboard queenBoard) {
        // Combine rook-style and bishop-style moves
        Bitboard rookMoves = Rook.moves(board, queenBoard);
        Bitboard bishopMoves = Bishop.moves(board, queenBoard);
        return new Bitboard(rookMoves.bitboard | bishopMoves.bitboard);
    }
}
