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

}
