package com.chess.project;
import org.springframework.stereotype.Service;


@Service
public class BoardService {
    private Board board;
    public Boolean boardInitialized = false;

    public void init() {
        board = new Board();
        boardInitialized = true;
    }

    public Board getBoard() {
        if (!boardInitialized) {this.init();} //Board is initialized when frontend page is reloaded. if backend page is reloaded the board is lost so we re initalize it.

        return board;
    }
}
