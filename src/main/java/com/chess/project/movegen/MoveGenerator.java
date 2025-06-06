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

        Bitboard kingBoard = new Bitboard(0);
        kingBoard.setBit(board.getKingIndex(color));
        moves = MoveGenerator.filterPseudoLegalMoves(board, moves, color, board.getKingIndex(color), board.getPieceType(index));

        return moves;
    }

    public static Bitboard filterPseudoLegalMoves(Board board, Bitboard moves, Color color, int kingIndex, String pieceType) {

        //Check Evasions;
        Bitboard attackers = new Bitboard(0L);
        Bitboard kingBoard = new Bitboard(0L);
        kingBoard.setBit(kingIndex);

        if (color == Color.White) {
            attackers.bitboard |= Knight.pseudoLegalMoves(board, kingIndex, color).bitboard & board.blackKnights.bitboard;
            attackers.bitboard |= Rook.pseudoLegalMoves(board, kingIndex, color).bitboard & board.blackRooks.bitboard;
            attackers.bitboard |= Queen.pseudoLegalMoves(board, kingIndex, color).bitboard & board.blackQueens.bitboard;
            attackers.bitboard |= Bishop.pseudoLegalMoves(board, kingIndex, color).bitboard & board.blackBishops.bitboard;
            attackers.bitboard |= Pawn.attacks(board, kingBoard, color).bitboard & board.blackPawns.bitboard;
        }
        else {
            attackers.bitboard |= Knight.pseudoLegalMoves(board, kingIndex, color).bitboard & board.whiteKnights.bitboard;
            attackers.bitboard |= Rook.pseudoLegalMoves(board, kingIndex, color).bitboard & board.whiteRooks.bitboard;
            attackers.bitboard |= Queen.pseudoLegalMoves(board, kingIndex, color).bitboard & board.whiteQueens.bitboard;
            attackers.bitboard |= Bishop.pseudoLegalMoves(board, kingIndex, color).bitboard & board.whiteBishops.bitboard;
            attackers.bitboard |= Pawn.attacks(board, kingBoard, color).bitboard & board.whitePawns.bitboard;
        }

        int attackersCount = attackers.countBits();
        if (pieceType == "King_White" || pieceType == "King_Black") {return moves;}
        
        if (attackersCount > 1) {
            {return new Bitboard(0L);}
        }
        if(attackersCount == 1) {
            if (!BitboardUtils.isSingleSlidingPiece(attackers, board)) {
                    moves.bitboard &= attackers.bitboard; //If its a non sliding piece we cannot block it so only option is to evade it or move away
                }
            else {
                moves.bitboard &= BitboardUtils.drawLine(kingBoard, attackers).bitboard;
            }
            return moves;

        }
        return moves;
    }
}