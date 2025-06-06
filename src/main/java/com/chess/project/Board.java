package com.chess.project;
import com.chess.project.enums.Color;

public class Board {
    // Bitboards for each piece
    public Bitboard whitePawns;
    public Bitboard whiteKnights;
    public Bitboard whiteBishops;
    public Bitboard whiteRooks;
    public Bitboard whiteQueens;
    public Bitboard whiteKing;

    public Bitboard blackPawns;
    public Bitboard blackKnights;
    public Bitboard blackBishops;
    public Bitboard blackRooks;
    public Bitboard blackQueens;
    public Bitboard blackKing;

    public Color playerColor;

    public Board() {
        // Initialize standard chess position or custom as needed
        this.whitePawns = new Bitboard(0x000000000000FF00L);
        this.whiteRooks = new Bitboard(0x0000000000000081L);
        this.whiteKnights = new Bitboard(0x0000000000000042L);
        this.whiteBishops = new Bitboard(0x0000000000000024L);
        this.whiteQueens = new Bitboard(0x0000000000000008L);
        this.whiteKing = new Bitboard(0x0000000000000010L);

        this.blackPawns = new Bitboard(0x00FF000000000000L);
        this.blackRooks = new Bitboard(0x8100000000000000L);
        this.blackKnights = new Bitboard(0x4200000000000000L);
        this.blackBishops = new Bitboard(0x2400000000000000L);
        this.blackQueens = new Bitboard(0x0800000000000000L);
        this.blackKing = new Bitboard(0x1000000000000000L);

        this.playerColor = Color.White;
    }

    public Bitboard getOccupied() {
        return new Bitboard(whitePawns.bitboard | whiteKnights.bitboard | whiteBishops.bitboard | whiteRooks.bitboard | whiteQueens.bitboard | whiteKing.bitboard |
               blackPawns.bitboard | blackKnights.bitboard | blackBishops.bitboard | blackRooks.bitboard | blackQueens.bitboard | blackKing.bitboard);
    }

    public Bitboard getWhitePieces() {
        return new Bitboard(whitePawns.bitboard | whiteKnights.bitboard | whiteBishops.bitboard | whiteRooks.bitboard | whiteQueens.bitboard | whiteKing.bitboard);
    }

    public Bitboard getBlackPieces() {
        return new Bitboard(blackPawns.bitboard | blackKnights.bitboard | blackBishops.bitboard | blackRooks.bitboard | blackQueens.bitboard | blackKing.bitboard);
    }

    public Bitboard getEmpty() {
        Bitboard board = this.getOccupied();
        board.bitboard = ~board.bitboard;

        return board;
    }

    public String getPieceType(int index) {
        if (this.whitePawns.containsBit(index)) {return "Pawn_White";}
        else if (this.whiteRooks.containsBit(index)) {return "Rook_White";}
        else if (this.whiteKnights.containsBit(index)) {return "Knight_White";}
        else if (this.whiteBishops.containsBit(index)) {return "Bishop_White";}
        else if (this.whiteQueens.containsBit(index)) {return "Queen_White";}
        else if (this.whiteKing.containsBit(index)) {return "King_White";}

        else if (this.blackPawns.containsBit(index)) {return "Pawn_Black";}
        else if (this.blackRooks.containsBit(index)) {return "Rook_Black";}
        else if (this.blackKnights.containsBit(index)) {return "Knight_Black";}
        else if (this.blackBishops.containsBit(index)) {return "Bishop_Black";}
        else if (this.blackQueens.containsBit(index)) {return "Queen_Black";}
        else if (this.blackKing.containsBit(index)) {return "King_Black";}

        else {return "Empty";}

    }

    public Bitboard getBoardType(int index) {
        if (this.whitePawns.containsBit(index)) {return this.whitePawns;}
        else if (this.whiteRooks.containsBit(index)) {return this.whiteRooks;}
        else if (this.whiteKnights.containsBit(index)) {return this.whiteKnights;}
        else if (this.whiteBishops.containsBit(index)) {return this.whiteBishops;}
        else if (this.whiteQueens.containsBit(index)) {return this.whiteQueens;}
        else if (this.whiteKing.containsBit(index)) {return this.whiteKing;}

        else if (this.blackPawns.containsBit(index)) {return this.blackPawns;}
        else if (this.blackRooks.containsBit(index)) {return this.blackRooks;}
        else if (this.blackKnights.containsBit(index)) {return this.blackKnights;}
        else if (this.blackBishops.containsBit(index)) {return this.blackBishops;}
        else if (this.blackQueens.containsBit(index)) {return this.blackQueens;}
        else if (this.blackKing.containsBit(index)) {return this.blackKing;}

        else {return null;}

    }



    public String[] pieceArray(){
        String[] pieces = new String[64];

        for (int index = 0; index <= 63; index++) {
            String pieceType = this.getPieceType(index);
            pieces[index] = pieceType;
        }

        return pieces;
    }

    public void makeMove(int from, int to) {
        Bitboard startSquareBitboard = this.getBoardType(from);
        Bitboard endSquareBitboard = this.getBoardType(to);

        startSquareBitboard.flipBit(from);
        startSquareBitboard.flipBit(to);

        if (endSquareBitboard != null) {
            endSquareBitboard.flipBit(to);
        }

        //Swap Player Color
        if (this.playerColor == Color.White) {this.playerColor = Color.Black;}
        else if (this.playerColor == Color.Black) {this.playerColor = Color.White;}

    }
}