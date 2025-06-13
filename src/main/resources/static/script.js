prevSquare = null
let currentBoard = null
//const currentURL = "https://chessweb-98le.onrender.com/"
const currentURL = "http://localhost:8080/"

let playerColor = "White";
let moveGenerationTime = 0;

function createBoard() {

    const columnName = {
        0 : 'A',
        1 : 'B',
        2 : 'C',
        3 : 'D',
        4 : 'E',
        5 : 'F',
        6 : 'G',
        7 : 'H'
    };

    let chessboard = document.getElementById("chessboard");

    for (let row = 7; row >= 0; row--) {
        const newRow = document.createElement('div');
        newRow.setAttribute('row', row);

        for (let column = 0; column <= 7; column++) {
            //Create all the attributes for a square
            const index = (row * 8) + column;
            const squareNotation = columnName[column] + String(row + 1);
            const newSquare = document.createElement('div');
            const color = ((column + row)%2 == 1)? 'white' : 'black';

            //Add all the created attributes to the square
            newSquare.setAttribute('index', index);
            newSquare.setAttribute('squareNotation', squareNotation);
            newSquare.setAttribute('column', column);
            newSquare.setAttribute('highlighted', 'False')
            newSquare.setAttribute('color', color);
            newSquare.setAttribute('dragZone', true)

            newRow.appendChild(newSquare);//Add the new square to the row
        }
        chessboard.appendChild(newRow); // Add the row to the board
    }
}

createBoard()

function resetBoard() {
    for (let index = 0; index <= 63; index ++){
        const square = document.querySelector(`[index="${index}"]`);
        if (square.children.length > 0) {
            const child = square.children[0]
            square.removeChild(child)
        }

        
    }
}

function updateDebugData() {
    const moveGenerationTimeSpan = document.getElementById("moveGenerationTime")
    moveGenerationTimeSpan.textContent =  moveGenerationTime + "ms"
    
    const playerColorSpan = document.getElementById("playerColor")
    playerColorSpan.textContent = playerColor
}

function setBoard(board) {
    currentBoard = board
    resetBoard()
    removeAllEventListenersFromBoard()
    for (let index = 0; index <= 63; index ++){
        const piece = board[index]

        const pieceStyleDropDown = document.getElementById('pieceStyleDropDown')
        const pieceStylePath = pieceStyleDropDown.value

        if (piece != "Empty") {
            const square = document.querySelector(`[index="${index}"]`);
            const img = document.createElement('img');
            img.src = `../static/pieceImages/${pieceStylePath}/${piece}.png`;
            img.draggable = true
            img.classList.add("pieceImage");
            square.appendChild(img);
        }
    }

    addEventListernsToAllSquares();
    
}

function addDragStartEventListener(square) {
    square.addEventListener('dragstart', (event) => {
        pieceSelected(square); //On hovering on element the piece is marked as selected
    })
}

function addDragOverEventListener(square) {
    square.addEventListener('dragover', (event) => {
        event.preventDefault();
    })
}

function addDropEventListener(square) {
    square.addEventListener('drop', (event) => {
        dropPiece(square);
    })
}

function addEventListernsToAllSquares() {
    for (let index = 0; index <= 63; index++) {
        const square = document.querySelector(`[index="${index}"]`)
        if (square.children.length > 0) {
            addDragStartEventListener(square);
        }
        addDragOverEventListener(square);
        addDropEventListener(square);
    }
}

function removeAllEventListenersFromBoard() {
    for (let index = 0; index <= 63; index++) {
        const square = document.querySelector(`[index="${index}"]`)
        const clone = square.cloneNode(true);
        square.parentNode.replaceChild(clone, square);
    }
    
}

function initGame() {
    const start = performance.now();

    fetch(currentURL + "api/init", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify()
    })
    .then((response) => response.json())
    .then((data) => {
        const end = performance.now(); 
        console.log(`API call time to create board: ${end - start} milliseconds`);
        console.log(data)
        setBoard(data)})
    .catch((error) => console.error("Error:", error));

    const end = performance.now();
    
    
}

initGame()


async function pieceSelected(square) {
    const start = performance.now();
        const squareIndex = square.getAttribute('index');
        fetch(currentURL + "api/moves", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({'index' : squareIndex})
        })
        .then((response) => response.json())
        .then((data) => {
            const end = performance.now(); 
            console.log("legal moves", data);

            
            if(data['moves']) {data['moves'].forEach((index) => highlightSquare(index))}

            moveGenerationTime = data['DebugData']['moveGenerationTime']
            playerColor = data['DebugData']['playerColor']

            updateDebugData();
            
        })
        .catch((error) => console.error("Error:", error));

    prevSquare = square
}

function playAudio(pieceCaptured) {
    let captureAudio = new Audio('../static/Sounds/capture.mp3')
    let moveSelfAudio = new Audio('../static/Sounds/move-self.mp3')
    
    if (pieceCaptured) {
        captureAudio.play()
    }
    else {
        moveSelfAudio.play()
    }
}

async function dropPiece(square) {
    let isSquareHighlighted = square.getAttribute('highlighted') == "True"? true : false

    if (!prevSquare) {playAudio(false);}

    if(prevSquare && isSquareHighlighted){
        let start = performance.now();
        let prevSquareIndex = prevSquare.getAttribute('index')
        let newSquareIndex  = square.getAttribute('index')
        fetch(currentURL + "api/updateBoard", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({'from' : prevSquareIndex, 'to' : newSquareIndex})
        })
        .then((response) => response.json())
        .then((data) => {
            let end = performance.now(); 
            console.log(`API call time get new board: ${end - start} milliseconds`);
            console.log(data)

            setBoard(data['Board']);

            const isPieceCaptured = data['pieceCaptured']
            let checkMateDiv = document.getElementById("checkmate")

            playAudio(isPieceCaptured);
            //if (data['isCheckMate']) {checkMateDiv.textContent = "CHECKMATE, YOU WIN"}
        })
        .catch((error) => console.error("Error:", error));

        start = performance.now();

       /*fetch(currentURL + "api/AIMove", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
        })
        .then((response) => response.json())
        .then((data) => {
            end = performance.now(); 
            console.log(`API call time get new board: ${end - start} milliseconds`);
            console.log(data)

            setBoard(data['Board']);
            checkMateDiv = document.getElementById("checkmate")
            if (data['isCheckMate']) {checkMateDiv.textContent = "CHECKMATE, AI WINS"}
        })
        .catch((error) => console.error("Error:", error));*/
    }

    
    removeAllHighlights()
}

function highlightSquare(index){
    let square = document.querySelector(`[index="${index}"]`)
    square.setAttribute('highlighted', 'True')
}

function removeAllHighlights() {
    const squares = document.querySelectorAll(`[highlighted="True"]`)
    if (squares) {squares.forEach((square) => square.setAttribute('highlighted', 'False'))}
}

//remake board with different piece style of value change
const pieceStyleDropDown = document.getElementById('pieceStyleDropDown')
pieceStyleDropDown.addEventListener("change", () => {
        setBoard(currentBoard)
    }
)

//custom FEN Button Listener
document.getElementById('submitFEN').addEventListener('click', function() {
    const FEN = document.getElementById('FEN').value;
    initGame(FEN);
  });

//Reset FEN
document.getElementById('resetFEN').addEventListener('click', function() {
    initGame();
  });


