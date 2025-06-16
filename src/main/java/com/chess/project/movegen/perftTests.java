package com.chess.project.movegen;
import java.util.Arrays;
import java.util.List;

import com.chess.project.Bitboard;
import com.chess.project.Board;
import com.chess.project.enums.Color;

public class perftTests {

    public static void perftTestOne(Board board, int depth) {
        System.out.println(Arrays.toString(perftTests.getPerftData(board, 4, 4)));
    }

    private static int[] getPerftData(Board board, int startDepth, int depth) {
        List<int[]> moveList = MoveGenerator.createMoveList(board, board.playerColor);

        int numberOfMoves = moveList.size();
        Integer nodes = 0;
        Integer captures = 0;

        if (depth == 0) {
            if (board.pieceCaptured == true) {return new int[]{1, 1};}
            else {return new int[]{1, 0};}
        }
        
        for (int moveIndex = 0; moveIndex < numberOfMoves; moveIndex++) {
            Board newBoard = perftTests.copyBoard(board);
            int[] moveData = moveList.get(moveIndex);
            newBoard.makeMove(moveData[0], moveData[1]);
            
            int[] data = getPerftData(newBoard, startDepth, depth - 1);
            nodes += data[0];
            captures += data[1];

            if (depth == startDepth) {
                System.out.println("" + perftTests.indexToSquare(moveData[0]) + perftTests.indexToSquare(moveData[1]) + " : " + data[0]);;
            }
        }

        

        return new int[]{nodes, captures};
    }


    public static Board copyBoard(Board board) {
        Board boardCopy = new Board();
        
        boardCopy.whitePawns = new Bitboard(board.whitePawns.bitboard);
        boardCopy.whiteKnights = new Bitboard(board.whiteKnights.bitboard);
        boardCopy.whiteBishops = new Bitboard(board.whiteBishops.bitboard);
        boardCopy.whiteRooks = new Bitboard(board.whiteRooks.bitboard);
        boardCopy.whiteQueens = new Bitboard(board.whiteQueens.bitboard);
        boardCopy.whiteKing = new Bitboard(board.whiteKing.bitboard);

        boardCopy.blackPawns = new Bitboard(board.blackPawns.bitboard);
        boardCopy.blackKnights = new Bitboard(board.blackKnights.bitboard);
        boardCopy.blackBishops = new Bitboard(board.blackBishops.bitboard);
        boardCopy.blackRooks = new Bitboard(board.blackRooks.bitboard);
        boardCopy.blackQueens = new Bitboard(board.blackQueens.bitboard);
        boardCopy.blackKing = new Bitboard(board.blackKing.bitboard);

        boardCopy.playerColor = board.playerColor == Color.White? Color.White : Color.Black;

        boardCopy.enPassantIndex = board.enPassantIndex;
        boardCopy.pieceCaptured = board.pieceCaptured;

        boardCopy.canWhiteKingSideCastle = board.canWhiteKingSideCastle;
        boardCopy.canBlackKingSideCastle = board.canBlackKingSideCastle;
        boardCopy.canWhiteQueenSideCastle = board.canWhiteQueenSideCastle;
        boardCopy.canBlackQueenSideCastle = board.canBlackQueenSideCastle;

        return boardCopy;
    }

    public static String indexToSquare(int index) {
        char[] columns = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        char[] rows = {'1', '2', '3', '4', '5', '6', '7', '8'};

        int columnIndex = index % 8;
        int rowIndex = Math.floorDiv(index, 8);

        String square = "" + columns[columnIndex] + rows[rowIndex];
        return square;

    }
}
