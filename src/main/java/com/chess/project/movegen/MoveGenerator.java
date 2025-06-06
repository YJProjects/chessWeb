package com.chess.project.movegen;

import com.chess.project.Board;
import com.chess.project.Bitboard;
import com.chess.project.enums.Color;

public class MoveGenerator {
    public static Bitboard generatePieceMoves(Board board, int index) {
        Bitboard moves = new Bitboard(0L);

        Bitboard blackPieces = board.getBlackPieces();
        Color color = Color.White;
        if (blackPieces.containsBit(index)) {color = Color.Black;}

        //if (color != board.playerColor) {return moves;}

        if (board.whitePawns.containsBit(index) || board.blackPawns.containsBit(index)) {
            moves = Pawn.pseudoLegalMoves(board, index, color);
        }
        else if (board.whiteKnights.containsBit(index) || board.blackKnights.containsBit(index)) {
            moves = Knight.pseudoLegalMoves(board, index, color);
        }
        else if (board.whiteRooks.containsBit(index) || board.blackRooks.containsBit(index)) {
            moves = Rook.pseudoLegalMoves(board, index, color);
        }
        else if (board.whiteBishops.containsBit(index) || board.blackBishops.containsBit(index)) {
            moves = Bishop.pseudoLegalMoves(board, index, color);
        }
        else if (board.whiteQueens.containsBit(index) || board.blackQueens.containsBit(index)) {
            moves = Queen.pseudoLegalMoves(board, index, color);
        }
        else if (board.whiteKing.containsBit(index) || board.blackKing.containsBit(index)) {
            moves = King.pseudoLegalMoves(board, index, color);
        }

        return moves;
    }
}