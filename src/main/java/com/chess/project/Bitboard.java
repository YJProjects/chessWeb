package com.chess.project;
import java.util.ArrayList;

public class Bitboard {
    public long bitboard;

    public Bitboard(long bitboard) {
        this.bitboard = bitboard;
    }

    public boolean containsBit(int index) {
        return ((this.bitboard & (1L << index)) != 0); 
    }

    public void setBit(int index) {
        this.bitboard |= (1L << index);
    }

    public void flipBit(int index) {
        this.bitboard ^= (1L << index);
    }

    public ArrayList<Integer> toIndexes() {
        ArrayList<Integer> Indexes = new ArrayList<Integer>();
        for (int index = 0; index <= 63; index++){
            if (this.containsBit(index)) {Indexes.add(index);}
        }

        return Indexes;
    }

    public int countBits() {
        int count = 0;
        long bitboard = this.bitboard;
        while (bitboard != 0) {
            bitboard &= (bitboard - 1); // Clears the least significant 1 bit
            count++;
        }

        return count;
    }

    public void printBoard() {
        for (int rank = 7; rank >= 0; rank--) {
            System.out.print((rank + 1) + "  "); // Print rank number
            for (int file = 0; file < 8; file++) {
                int square = rank * 8 + file;
                long mask = 1L << square;
                if ((this.bitboard & mask) != 0) {
                    System.out.print("1 ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        System.out.println("\n   a b c d e f g h"); // Print file letters
    }

    public String getPieceFromBoardWithoutColor(Board board) {
        if (isSubset(this.bitboard, board.whitePawns.bitboard) || isSubset(this.bitboard, board.blackPawns.bitboard)) {return "Pawn";}
        if (isSubset(this.bitboard, board.whiteRooks.bitboard) || isSubset(this.bitboard, board.blackRooks.bitboard)) {return "Rook";}
        if (isSubset(this.bitboard, board.whiteBishops.bitboard) || isSubset(this.bitboard, board.blackBishops.bitboard)) {return "Bishop";}
        if (isSubset(this.bitboard, board.whiteQueens.bitboard) || isSubset(this.bitboard, board.blackQueens.bitboard)) {return "Queen";}
        if (isSubset(this.bitboard, board.whiteKnights.bitboard) || isSubset(this.bitboard, board.blackKnights.bitboard)) {return "Knight";}
        if (isSubset(this.bitboard, board.whiteKing.bitboard) || isSubset(this.bitboard, board.blackKing.bitboard)) {return "King";}

        return null;
    }

    public static boolean isSubset(long a, long b) { //is A a subset of B
        return (a & b) == a;
    }   
}
