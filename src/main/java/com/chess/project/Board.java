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

    public Integer enPassantIndex;
    public Boolean pieceCaptured;

    public Boolean canWhiteKingSideCastle = true;
    public Boolean canBlackKingSideCastle = true;
    public Boolean canWhiteQueenSideCastle = true;
    public Boolean canBlackQueenSideCastle = true;

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

    public int getKingIndex(Color color) {
        Bitboard kingBoard = new Bitboard(0L);
        if (color == Color.White){
            kingBoard = this.whiteKing;
        }
        else {
            kingBoard = this.blackKing;
        }
        for (int index = 0; index <= 63; index++) {
            if (kingBoard.containsBit(index)) {
                return index;
            }
        }

        return 0;
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
        String pieceType = this.getPieceType(from);

        pieceCaptured = false; //For Audio Purposes

        startSquareBitboard.flipBit(from);
        startSquareBitboard.flipBit(to);

        if (endSquareBitboard != null) {
            pieceCaptured = true;
            endSquareBitboard.flipBit(to);
        }

        //Promote Piece if its a pawn on opposite end of the board
        //Rules:
        //-> Piece should be Pawn_White and endPos(to) should be between 56 and 63 i.e. to > 56
        //-> Piece should be Pawn_Black and endPos(to) should be between 0 and 7
        if (pieceType == "Pawn_White" && (to >= 56)) {
            this.whitePawns.flipBit(to);
            this.whiteQueens.flipBit(to);
        }
        if (pieceType == "Pawn_Black" && (to <= 7)) {
            this.blackPawns.flipBit(to);
            this.blackQueens.flipBit(to);
        }

        //If move was enpassant we have to delete the enemy Pawn which will be 8 squares behind if white pawn is capturing and 8 squares ahead if black is capturing
        if  ((this.enPassantIndex != null) && (to == this.enPassantIndex) && (pieceType == "Pawn_White" || pieceType == "Pawn_Black")) {
            int row = Math.floorDiv(to, 8) + 1;
            if (row == 6) {
                pieceCaptured = true;
                this.blackPawns.flipBit(to - 8);
            }
            if (row == 3) {
                pieceCaptured = true;
                this.whitePawns.flipBit(to + 8);
            }
        }

        //Reset en passant index every turn since it lasts for a single chance.
        this.enPassantIndex = null;

        //check if en passant is enable
        // if piece type is a pawn and distance |(from - to)| is 16 (2 rows pushed) : en passant MIGHT be possible
        // if pawn color is white and has moves to 4th rank, en passant is enabled 8 squares behind it
        // if pawn color is black and has moves to 5th rnk, en passnt is enables 8 squares infront of it.

        long distance = Math.abs(from - to);
        if ((pieceType == "Pawn_White" || pieceType == "Pawn_Black") & (distance == 16)) {
            int row = Math.floorDiv(to, 8) + 1;
            if (pieceType == "Pawn_White" && row == 4) {
                this.enPassantIndex = to - 8;
            }
            if (pieceType == "Pawn_Black" && row == 5) {
                this.enPassantIndex = to + 8;
            }
        }

        //Check if move was a castle. If it is we have to move the rook accordingly.
        if (pieceType == "King_White") {
            if (from == 4 & to == 6) {
                this.whiteRooks.setBit(5);
                this.whiteRooks.flipBit(7);
            }
            if (from == 4 & to == 2) {
                this.whiteRooks.setBit(3);
                this.whiteRooks.flipBit(0);
            }
        }
        else if (pieceType == "King_Black") {
            if (from == 60 & to == 58) {
                this.blackRooks.setBit(59);
                this.blackRooks.flipBit(56);
            }
            if (from == 60 & to == 62) {
                this.blackRooks.setBit(61);
                this.blackRooks.flipBit(63);
            }
        }

        //check if king can castle
        if (pieceType == "King_White") {
            this.canWhiteKingSideCastle = false;
            this.canWhiteQueenSideCastle = false;
        }
        else if (from == 7 || to == 7) {
            this.canWhiteKingSideCastle = false; //if from == 7 than kingside rook has moved and to == 7 means kingside rook has been captured
        }
        else if (from == 0 || to == 0) {
            this.canWhiteQueenSideCastle = false; //if from == 0 than queenside rook has moved and to == 0 means queenside rook has been captured
        }
        else if (pieceType == "King_Black") {
            this.canBlackKingSideCastle = false;
            this.canBlackQueenSideCastle = false;
        }
        else if (from == 56 || to == 56) {
            this.canBlackQueenSideCastle = false; //if from == 56 than queenside rook has moved and to == 56 means queenside rook has been captured
        }
        else if (from == 63 || to == 63) {
            this.canBlackKingSideCastle = false; //if from == 63 than queenside rook has moved and to == 63 means queenside rook has been captured
        }

        //Swap Player Color
        if (this.playerColor == Color.White) {this.playerColor = Color.Black;}
        else if (this.playerColor == Color.Black) {this.playerColor = Color.White;}
    }
}