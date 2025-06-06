package com.chess.project.movegen;

import com.chess.project.Bitboard;
import com.chess.project.Board;
import com.chess.project.enums.Color;

public class Pawn {
    public Pawn() {}

    /*
     * Generates all pawn moves (attacks and pushes) for a given index
     */
    public static Bitboard pseudoLegalMoves(Board board, int index, Color color) {
        Bitboard pawnBoard = new Bitboard(0L);
        
        pawnBoard.setBit(index);

        Bitboard enemyPieces = new Bitboard(0L);
        if (color == Color.White) {
            enemyPieces = board.getBlackPieces();
        } else if (color == Color.Black) {
            enemyPieces = board.getWhitePieces();
        }

        Bitboard pushes = Pawn.pushes(board, pawnBoard, color);
        Bitboard attacks = Pawn.attacks(board, pawnBoard, color);

        attacks.bitboard &= enemyPieces.bitboard;
        return new Bitboard(pushes.bitboard | attacks.bitboard);

    }

    /*
     * Generated all pawn moves (attacks and pushes) for all pawns on the board of a given color
     */
    public static Bitboard pseudoLegalMoves(Board board, Color color) {
        Bitboard pawnBoard = new Bitboard(0L);
        Bitboard enemyPieces = new Bitboard(0L);


        if (color == Color.White) {
            pawnBoard = board.whitePawns;
            enemyPieces = board.getBlackPieces();
        } else if (color == Color.Black) {
            pawnBoard = board.blackPawns;
            enemyPieces = board.getWhitePieces();
        }

        Bitboard pushes = Pawn.pushes(board, pawnBoard, color);
        Bitboard attacks = Pawn.attacks(board, pawnBoard, color);

        attacks.bitboard &= enemyPieces.bitboard;
        return new Bitboard(pushes.bitboard | attacks.bitboard);
    }

    public static Bitboard pushes(Board board, Bitboard pawnBoard, Color color) {
        long singlePushes = 0L;
        long doublePushes = 0L;
        long empty = board.getEmpty().bitboard;

        if (color == Color.White) {
            singlePushes = (pawnBoard.bitboard << 8) & empty;
            doublePushes = (singlePushes << 8) & empty;
        } else if (color == Color.Black) {
            singlePushes = (pawnBoard.bitboard >> 8) & empty;
            doublePushes = (singlePushes >> 8) & empty;
        }
        Bitboard allTargets = new Bitboard(singlePushes | doublePushes);
        return allTargets;
    }

    public static Bitboard attacks(Board board, Bitboard pawnBoard, Color color) {
        long eastAttacks = 0L;
        long westAttacks = 0L;

        if (color == Color.White) {
            eastAttacks = (pawnBoard.bitboard << 9);
            westAttacks = (pawnBoard.bitboard  << 7);
        } else if (color == Color.Black) {
            eastAttacks = (pawnBoard.bitboard >> 7);
            westAttacks = (pawnBoard.bitboard  >> 9);
        }
        Bitboard allTargets = new Bitboard(eastAttacks | westAttacks);
        return allTargets;
    }
}
