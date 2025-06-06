package com.chess.project.movegen;

import com.chess.project.Bitboard;
import com.chess.project.Board;
import com.chess.project.enums.Color;

public class Knight {
    public Knight() {}


    public static Bitboard pseudoLegalMoves(Board board, int index, Color color) {
        Bitboard knightBoard = new Bitboard(0L);
        knightBoard.setBit(index);
        Bitboard enemyPieces = new Bitboard(0L);
        Bitboard empty = board.getEmpty();

        if (color == Color.White) {
            enemyPieces = board.getBlackPieces();
        }
        else {
            enemyPieces = board.getWhitePieces();

        }

        Bitboard moves = Knight.moves(board, knightBoard);
        moves.bitboard &= (empty.bitboard | enemyPieces.bitboard);

        return moves;
        
    }

    public static Bitboard pseudoLegalMoves(Board board, Color color) {

        Bitboard knightBoard = new Bitboard(0L);
        Bitboard enemyPieces = new Bitboard(0L);
        Bitboard empty = board.getEmpty();

        if (color == Color.White) {
            knightBoard = board.whiteKnights;
            enemyPieces = board.getBlackPieces();
        }
        else {
            knightBoard = board.blackKnights;
            enemyPieces = board.getWhitePieces();

        }

        Bitboard moves = Knight.moves(board, knightBoard);
        moves.bitboard &= (empty.bitboard | enemyPieces.bitboard);

        return moves;
    }

    public static Bitboard moves(Board board, Bitboard knightBoard) {
        long notAFile   = 0xFEFEFEFEFEFEFEFEL; // Clear File A (leftmost column)
        long notABFile  = 0xFCFCFCFCFCFCFCFCL; // Clear Files A and B (first two columns)
        long notHFile = 0x7F7F7F7F7F7F7F7FL;
        long notGHFile  = 0x3F3F3F3F3F3F3F3FL; // Clear Files G and H (last two columns)

        long noNoEa =  (knightBoard.bitboard << 17) & notAFile ;
        long noEaEa =  (knightBoard.bitboard << 10) & notABFile;
        long soEaEa =  (knightBoard.bitboard >>  6) & notABFile;
        long soSoEa =  (knightBoard.bitboard >> 15) & notAFile ;
        long noNoWe =  (knightBoard.bitboard << 15) & notHFile ;
        long noWeWe =  (knightBoard.bitboard <<  6) & notGHFile;
        long soWeWe =  (knightBoard.bitboard >> 10) & notGHFile;
        long soSoWe =  (knightBoard.bitboard >> 17) & notHFile ;

        return new Bitboard(noNoEa | noEaEa | soEaEa | soSoEa | noNoWe | noWeWe | soWeWe | soSoWe);
    }

}
