package com.chess.project;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.chess.project.movegen.MoveGenerator;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")

public class Controller {

    @Autowired
    private BoardService boardService;

    @PostMapping("/init")
    public String[] init(){
        boardService.init();
        Board board = boardService.getBoard();
        return board.pieceArray();
    }

    @PostMapping("/moves")
    public Map<String, Object> moves(@RequestBody Map<String, String> data){
        long startTime = System.nanoTime();
        Board board = boardService.getBoard();

        int index = Integer.parseInt(data.get("index"));
        Bitboard moves = MoveGenerator.generatePieceMoves(board, index);

        long endTime = System.nanoTime();
        double durationInMs = (endTime - startTime) / 1_000_000.0;  

        ArrayList<Integer> moveList = moves.toIndexes();
        
        Map<String, Object> returnData = new HashMap<>();
        Map<Object, Object> debugData = new HashMap<>();

        returnData.put("moves" , moveList);

        debugData.put("playerColor", board.playerColor);
        debugData.put("moveGenerationTime", durationInMs);
        returnData.put("DebugData" , debugData);
        
        return returnData;
    }

    @PostMapping("/updateBoard")
    public Map<String, Object> updateBoard(@RequestBody Map<String, String> data){

        Board board = boardService.getBoard();

        int from = Integer.parseInt(data.get("from"));
        int to = Integer.parseInt(data.get("to"));

        board.makeMove(from, to);

        Map<String, Object> returnData = new HashMap<>();

        returnData.put("Board" , board.pieceArray());
        returnData.put("pieceCaptured" , board.pieceCaptured);

        

        return returnData;
    }

}
