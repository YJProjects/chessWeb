prevSquare = null
let currentBoard = null
//const currentURL = "https://chessweb-98le.onrender.com/"
const currentURL = "http://localhost:8080/"

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
    
}

function setBoard(board) {
    currentBoard = board
    resetBoard()
    for (let index = 0; index <= 63; index ++){
        const piece = board[index]

        const pieceStyleDropDown = document.getElementById('pieceStyleDropDown')
        const pieceStylePath = pieceStyleDropDown.value

        if (piece != "Empty") {
            const square = document.querySelector(`[index="${index}"]`);
            const img = document.createElement('img');
            img.src = `../static/pieceImages/${pieceStylePath}/${piece}.png`;
            img.height = 90;
            img.draggable = true;
            img.style.zIndex = 100;
            square.appendChild(img);

            img.addEventListener('mousedown', (e) => mouseDown(e, img))
        }
    }    
}

let moveListener; let upListener; let originalX;let originalY;
function mouseDown(e, img) {
    img.style.position = "fixed";
    startX = e.clientX;
    startY = e.clientY;

    let originalPos = img.getBoundingClientRect()
    originalX = originalPos.x;
    originalY = originalPos.y;

    const square = img.parentNode;

    // Move the image to follow the mouse immediately
    const offset = img.height / 2;
    img.style.left = (startX - offset) + 'px';
    img.style.top = (startY - offset) + 'px';
    img.style.pointerEvents = "none"; // Prevent blocking the square underneath

    moveListener = (e) => mouseMove(e, img);
    upListener = (e) => mouseUp(e, img);

    pieceSelected(square)

    document.addEventListener('mousemove', moveListener);
    document.addEventListener('mouseup', upListener);
}


function mouseUp(e, img) {


    document.removeEventListener('mousemove', moveListener);
    document.removeEventListener('mouseup', upListener);


    let square = document.elementFromPoint(e.clientX, e.clientY);
    if (square.nodeName == "IMG") {square = square.parentNode;}

    let isSquareHighlighted = square.getAttribute('highlighted') == "True"? true : false
    console.log(square, square.nodeName)

    if (!isSquareHighlighted ) {
        img.style.removeProperty('top')
        img.style.removeProperty('left')
        img.style.removeProperty('position');
        img.style.removeProperty('pointer-events');
    }

    
    dropPiece(square)
    
}

function mouseMove(e, img) {
    newX = startX - e.clientX;
    newY = startY - e.clientY;
    offset = img.height / 2 //Grabbing an image the left left corner of the img is position at our mouse. to align the centre piece we offset it

    startX = e.clientX;
    startY = e.clientY;

    img.style.left = (startX - offset) + 'px'
    img.style.top = (startY - offset) + 'px'
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


