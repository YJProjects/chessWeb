package com.chess.project;
import org.springframework.stereotype.Service;


@Service
public class BoardService {
    private Board board;

    public void init() {
        board = new Board();
    }

    public Board getBoard() {
        return board;
    }
}
