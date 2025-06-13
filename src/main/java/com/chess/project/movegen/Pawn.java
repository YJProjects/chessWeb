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
        Bitboard legalAttacks = new Bitboard(attacks.bitboard & enemyPieces.bitboard);

        //Add enPassant
        //Rules : If EnPassant is possible pawn attack square contains enPassantSquare we conclude enPassant is a legalMove
        //We also filter the row for which enpassant is possible. 
        
        Bitboard enPassantBoard = new Bitboard(0L);
        long notRank_3 = ~0x0000000000FF0000L;
        long notRank_6 = ~0x0000FF0000000000L;

        if (board.enPassantIndex != null) {
            enPassantBoard.setBit(board.enPassantIndex);

            //Filter Rows
            if (color == Color.White) {enPassantBoard.bitboard &= notRank_3;}
            else if (color == Color.Black) {enPassantBoard.bitboard &= notRank_6;}

            //Check if attack coincides with correct color
            if ((enPassantBoard.bitboard & attacks.bitboard) > 0){
                legalAttacks.bitboard |= enPassantBoard.bitboard;
            }
        }
        
        return new Bitboard(pushes.bitboard | legalAttacks.bitboard);
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
        Bitboard legalAttacks = new Bitboard(attacks.bitboard & enemyPieces.bitboard);

        //Add enPassant
        //Rules : If EnPassant is possible pawn attack square contains enPassantSquare we conclude enPassant is a legalMove
        
        Bitboard enPassantBoard = new Bitboard(0L);
        if (board.enPassantIndex != null) {
            enPassantBoard.setBit(board.enPassantIndex);
            if ((enPassantBoard.bitboard & legalAttacks.bitboard) > 0){
                legalAttacks.bitboard |= enPassantBoard.bitboard;
            }
        }
        return new Bitboard(pushes.bitboard | legalAttacks.bitboard);
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

        long notAFile   = 0xFEFEFEFEFEFEFEFEL; // Clear File A (leftmost column)
        long notHFile = 0x7F7F7F7F7F7F7F7FL;

        if (color == Color.White) {
            eastAttacks = (pawnBoard.bitboard << 9) & notAFile;
            westAttacks = (pawnBoard.bitboard  << 7) & notHFile;
        } else if (color == Color.Black) {
            eastAttacks = (pawnBoard.bitboard >> 7) & notAFile;
            westAttacks = (pawnBoard.bitboard  >> 9) & notHFile ;
        }
        Bitboard allTargets = new Bitboard(eastAttacks | westAttacks);

        return allTargets;
    }
}
