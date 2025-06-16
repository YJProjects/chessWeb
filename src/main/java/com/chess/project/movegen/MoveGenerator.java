package com.chess.project.movegen;

import com.chess.project.Board;
import com.chess.project.Bitboard;
import com.chess.project.enums.Color;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {
    public static Bitboard generatePieceMoves(Board board, int index) {
        Bitboard moves = new Bitboard(0L);

        Bitboard blackPieces = board.getBlackPieces();
        Color color = Color.White;
        if (blackPieces.containsBit(index)) {color = Color.Black;}

        if (color != board.playerColor) {return moves;}

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
        moves = MoveGenerator.filterPseudoLegalMoves(board, moves, color, board.getKingIndex(color), board.getPieceType(index), index);

        return moves;
    }

    public static Bitboard filterPseudoLegalMoves(Board board, Bitboard moves, Color color, int kingIndex, String pieceType, int pieceIndex) {

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
            int attackerIndex = attackers.getSetBitIndexes().get(0);
            if (!BitboardUtils.isSingleSlidingPiece(attackers, board)) {
                    moves.bitboard &= attackers.bitboard; //If its a non sliding piece we cannot block it so only option is to evade it or move away
                }
            else {
                int kingRow = Math.floorDiv(kingIndex, 8);
                int kingColumn = kingIndex % 8;

                int attackerRow = Math.floorDiv(attackerIndex, 8);
                int attackerColumn = attackerIndex % 8;

                if (attackerRow == kingRow || attackerColumn == kingColumn) {
                    moves.bitboard &= BitboardUtils.drawLine(kingIndex, attackerIndex).bitboard;
                }
                else {
                    moves.bitboard &= BitboardUtils.drawDiagonal(kingIndex, attackerIndex).bitboard;
                }
            }

        }

        Bitboard enemyRooks  = new Bitboard(0L);
        Bitboard enemyBishops  = new Bitboard(0L);
        Bitboard enemyQueens  = new Bitboard(0L);

        //Filter for pinned pieces
        if (color == Color.White) {
            enemyRooks = board.blackRooks;
            enemyBishops = board.blackBishops;
            enemyQueens = board.blackQueens;
        }
        else {
            enemyRooks = board.whiteRooks;
            enemyBishops = board.whiteBishops;
            enemyQueens = board.whiteQueens;
        }

        List<Integer> enemyRooksIndexes = enemyRooks.getSetBitIndexes();
        List<Integer> enemyBishopsIndexes = enemyBishops.getSetBitIndexes();
        List<Integer> enemyQueensIndexes = enemyQueens.getSetBitIndexes();


        List<Integer> enemyLineAttackers = new ArrayList<>();
        enemyLineAttackers.addAll(enemyRooksIndexes);
        enemyLineAttackers.addAll(enemyQueensIndexes);


        List<Integer> enemyDiagonalAttackers = new ArrayList<>();
        enemyDiagonalAttackers.addAll(enemyBishopsIndexes);
        enemyDiagonalAttackers.addAll(enemyQueensIndexes);
        
        Bitboard kingLineMoves = Rook.moves(board, kingBoard);
        Bitboard kingDiagonalMoves = Bishop.moves(board, kingBoard);

        for (int index : enemyLineAttackers) {         
            Bitboard pieceBoard = new Bitboard(0L);
            pieceBoard.setBit(index);
            Bitboard pieceLineMoves = Rook.moves(board, pieceBoard);

            Bitboard pinnedPiece = new Bitboard(pieceLineMoves.bitboard & kingLineMoves.bitboard);
            if (pinnedPiece.bitboard <= 0) {continue;} //no pinned piece

            int pinnedPieceIndex = pinnedPiece.getSetBitIndexes().get(0);
            if (pinnedPieceIndex == pieceIndex) {
                Bitboard ray = BitboardUtils.drawLine(kingIndex, index);
                moves.bitboard &= ray.bitboard;
            }
        }

        for (int index : enemyDiagonalAttackers) {
            Bitboard pieceBoard = new Bitboard(0L);
            pieceBoard.setBit(index);
            Bitboard pieceDiagonalMoves = Bishop.moves(board, pieceBoard);

            Bitboard pinnedPiece = new Bitboard(pieceDiagonalMoves.bitboard & kingDiagonalMoves.bitboard);
            if (pinnedPiece.bitboard <= 0) {continue;} //no pinned piece

            int pinnedPieceIndex = pinnedPiece.getSetBitIndexes().get(0);
            if (pinnedPieceIndex == pieceIndex) {
                Bitboard ray = BitboardUtils.drawDiagonal(kingIndex, index);
                moves.bitboard &= ray.bitboard;
            }
        }
        
        return moves;   
    }
    
    public static List<int[]> createMoveList(Board board, Color color) { // A nested list containing list from at index 0 and to and index 1
        List<Integer> PawnIndexes;
        List<Integer> KnightIndexes;
        List<Integer> QueenIndexes;
        List<Integer> KingIndex;
        List<Integer> RookIndexes;
        List<Integer> BishopIndexes;

        if (color == Color.White) {
            PawnIndexes = board.whitePawns.getSetBitIndexes();
            RookIndexes = board.whiteRooks.getSetBitIndexes();
            BishopIndexes = board.whiteBishops.getSetBitIndexes();
            KnightIndexes = board.whiteKnights.getSetBitIndexes();
            QueenIndexes = board.whiteQueens.getSetBitIndexes();
            KingIndex = board.whiteKing.getSetBitIndexes();
        }
        else {
            PawnIndexes = board.blackPawns.getSetBitIndexes();
            RookIndexes = board.blackRooks.getSetBitIndexes();
            BishopIndexes = board.blackBishops.getSetBitIndexes();
            KnightIndexes = board.blackKnights.getSetBitIndexes();
            QueenIndexes = board.blackQueens.getSetBitIndexes();
            KingIndex = board.blackKing.getSetBitIndexes();
        }

        List<int[]> moveList = new ArrayList<>();

        
        for (int index : PawnIndexes) {
            int from = index;
            Bitboard moves = Pawn.pseudoLegalMoves(board, index, color);
            List<Integer> pieceMoveList = moves.getSetBitIndexes();
            for (int move: pieceMoveList) {
                int[] moveCombination = {from, move};
                moveList.add(moveCombination);
            }
        }
        for (int index : RookIndexes) {
            int from = index;
            Bitboard moves = Rook.pseudoLegalMoves(board, index, color);
            List<Integer> pieceMoveList = moves.getSetBitIndexes();
            for (int move: pieceMoveList) {
                int[] moveCombination = {from, move};
                moveList.add(moveCombination);
            }
        }
        for (int index : KnightIndexes) {
            int from = index;
            Bitboard moves = Knight.pseudoLegalMoves(board, index, color);
            List<Integer> pieceMoveList = moves.getSetBitIndexes();
            for (int move: pieceMoveList) {
                int[] moveCombination = {from, move};
                moveList.add(moveCombination);
            }
        }
        for (int index : BishopIndexes) {
            int from = index;
            Bitboard moves = Bishop.pseudoLegalMoves(board, index, color);
            List<Integer> pieceMoveList = moves.getSetBitIndexes();
            for (int move: pieceMoveList) {
                int[] moveCombination = {from, move};
                moveList.add(moveCombination);
            }
        }
        for (int index : QueenIndexes) {
            int from = index;
            Bitboard moves = Queen.pseudoLegalMoves(board, index, color);
            List<Integer> pieceMoveList = moves.getSetBitIndexes();
            for (int move: pieceMoveList) {
                int[] moveCombination = {from, move};
                moveList.add(moveCombination);
            }
        }
        for (int index : KingIndex) {
            int from = index;
            Bitboard moves = King.pseudoLegalMoves(board, index, color);
            List<Integer> pieceMoveList = moves.getSetBitIndexes();
            for (int move: pieceMoveList) {
                int[] moveCombination = {from, move};
                moveList.add(moveCombination);
            }
        }

        return moveList;
    }
}