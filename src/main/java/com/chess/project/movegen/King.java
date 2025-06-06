package com.chess.project.movegen;

import com.chess.project.Bitboard;
import com.chess.project.Board;
import com.chess.project.enums.Color;

public class King {
    public King() {}

    public static Bitboard pseudoLegalMoves(Board board, int index, Color color) {
        Bitboard kingBoard = new Bitboard(0L);
        kingBoard.setBit(index);
        Color enemyColor = color == Color.White? Color.Black : Color.White;
        Bitboard enemyPieces = new Bitboard(0L);
        Bitboard empty = board.getEmpty();

        if (color == Color.White) {
            enemyPieces = board.getBlackPieces();
        }
        else {
            enemyPieces = board.getWhitePieces();

        }

        Bitboard moves = King.moves(board, kingBoard);
        moves.bitboard &= (empty.bitboard | enemyPieces.bitboard);

        Bitboard taboo = King.tabooSquares(board, kingBoard, enemyColor);

        moves.bitboard &= ~(taboo.bitboard);
        return moves;
    }

    public static Bitboard pseudoLegalMoves(Board board, Color color) {
        Bitboard kingBoard = new Bitboard(0L);
        Bitboard enemyPieces = new Bitboard(0L);
        Bitboard empty = board.getEmpty();

        if (color == Color.White) {
            enemyPieces = board.getBlackPieces();
            kingBoard =  board.whiteKing;
        }
        else {
            enemyPieces = board.getWhitePieces();
            kingBoard =  board.blackKing;
        }

        Bitboard moves = King.moves(board, kingBoard);
        moves.bitboard &= (empty.bitboard | enemyPieces.bitboard);

        return moves;
        
    }

    public static Bitboard moves(Board board, Bitboard kingBoard) {
        long notAFile   = 0xFEFEFEFEFEFEFEFEL; 
        long notHFile = 0x7F7F7F7F7F7F7F7FL;
        long notRank_1 = 0xFFFFFFFFFFFFFF00L; 
        long notRank_8 = 0x00FFFFFFFFFFFFFFL;


        long north =  (kingBoard.bitboard << 8) & notRank_1;
        long northEast =  (kingBoard.bitboard << 9) & notAFile & notRank_1;
        long east =  (kingBoard.bitboard <<  1) & notAFile;
        long northWest =  (kingBoard.bitboard << 7)& notRank_1 & notHFile;
        long south =  (kingBoard.bitboard >> 8) & notRank_8; 
        long southEast =  (kingBoard.bitboard >>  7) & notAFile & notRank_8;
        long west =  (kingBoard.bitboard >> 1) & notHFile;
        long southWest =  (kingBoard.bitboard >> 9) & notHFile & notRank_8;

        return new Bitboard(north | northEast | east | northWest | south | southEast | west | southWest);
    }

    public static Bitboard tabooSquares(Board board, Bitboard kingBoard, Color enemyColor){ //All squares enemy pieces are attacking
        Bitboard taboo = new Bitboard(0L);

        Bitboard enemyPawnBoard = enemyColor == Color.White? board.whitePawns : board.blackPawns;
        Bitboard friendlyKingBoard = enemyColor == Color.White? board.blackKing : board.whiteKing;

        Bitboard kingBoardCopy = new Bitboard(friendlyKingBoard.bitboard);
        friendlyKingBoard = new Bitboard(0L);

        Bitboard enemyBishopBoard, enemyRookBoard, enemyQueenBoard, enemyKnightBoard, enemyKingBoard;

        if (enemyColor == Color.White) {
            enemyPawnBoard = board.whitePawns;
            friendlyKingBoard = board.blackKing;
            kingBoardCopy = new Bitboard(friendlyKingBoard.bitboard);
            board.blackKing.bitboard = 0L;

            enemyBishopBoard = board.whiteBishops;
            enemyRookBoard = board.whiteRooks;
            enemyQueenBoard = board.whiteQueens;
            enemyKnightBoard = board.whiteKnights;
            enemyKingBoard = board.whiteKing;

        }
        else {
            enemyPawnBoard = board.blackPawns;
            friendlyKingBoard = board.whiteKing;
            kingBoardCopy = new Bitboard(friendlyKingBoard.bitboard);
            board.whiteKing.bitboard = 0L;

            enemyBishopBoard = board.blackBishops;
            enemyRookBoard = board.blackRooks;
            enemyQueenBoard = board.blackQueens;
            enemyKnightBoard = board.blackKnights;
            enemyKingBoard = board.blackKing;
        }
        
        Bitboard bishopMoves = Bishop.moves(board, enemyBishopBoard);
        Bitboard rookMoves = Rook.moves(board, enemyRookBoard);
        Bitboard queenMoves = Queen.moves(board, enemyQueenBoard);
        Bitboard knightMoves = Knight.moves(board, enemyKnightBoard);
        Bitboard kingMoves = King.moves(board, enemyKingBoard);
        Bitboard pawnAttacks = Pawn.attacks(board, enemyPawnBoard, enemyColor);

        taboo.bitboard = pawnAttacks.bitboard | bishopMoves.bitboard | rookMoves.bitboard | queenMoves.bitboard | knightMoves.bitboard | kingMoves.bitboard;

        if (enemyColor == Color.White) {board.blackKing = kingBoardCopy;}
        else {board.whiteKing = kingBoardCopy;}
        return taboo;
    }

}
