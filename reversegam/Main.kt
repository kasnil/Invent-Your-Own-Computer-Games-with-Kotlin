package com.github.kasnil.kotlin.invent.reversegam

import kotlin.collections.flatten
import kotlin.random.Random
import kotlin.random.nextInt

const val WIDTH = 8 // Board is 8 spaces wide
const val HEIGHT = 8 // Board is 8 spaces tall
const val DIGITS1TO8 = "12345678"

data class Coord(
    val x: Int,
    val y: Int,
) {
    operator fun plus(other: Coord) = Coord(x + other.x, y + other.y)

    operator fun minus(other: Coord) = Coord(x - other.x, y - other.y)
}

data class Score(
    val x: Int,
    val y: Int,
) {
    operator fun plus(other: Score) = Score(x + other.x, y + other.y)
}

enum class GuessPlayer {
    Computer,
    Player,
}

// This function prints the board that it was passed. Returns None.
fun drawBoard(board: Array<CharArray>) {
    println("  12345678")
    println(" +--------+")
    (0..<HEIGHT).forEach { y ->
        print("${y + 1}|")
        (0..<WIDTH).forEach { x ->
            print("${board[x][y]}")
        }
        println("|${y + 1}")
    }
    println(" +--------+")
    println("  12345678")
}

// Creates a brand-new, blank board data structure.
fun getNewBoard() =
    Array(WIDTH) {
        CharArray(8) { ' ' }
    }

/**
 * Returns false if the player's move on space startCoord is invalid.
 * If it is a valid move, returns a list of spaces that would become the player's if they made a move here.
 */
fun isValidMove(
    board: Array<CharArray>,
    tile: Char,
    startCoord: Coord,
): Array<Coord>? {
    if (board[startCoord.x][startCoord.y] != ' ' || !isOnBoard(startCoord)) {
        return null
    }

    val otherTile =
        if (tile == 'X') {
            'O'
        } else {
            'X'
        }

    var tilesToFlip = mutableListOf<Coord>()
    for (directionCoord in listOf(
        Coord(0, 1),
        Coord(1, 1),
        Coord(1, 0),
        Coord(1, -1),
        Coord(0, -1),
        Coord(-1, -1),
        Coord(-1, 0),
        Coord(-1, 1),
    )) {
        var currentCoord = startCoord
        currentCoord += directionCoord // First step in the direction
        while (isOnBoard(currentCoord) && board[currentCoord.x][currentCoord.y] == otherTile) {
            // Keep moving in this x & y direction.
            currentCoord += directionCoord
            if (isOnBoard(currentCoord) && board[currentCoord.x][currentCoord.y] == tile) {
                // There are pieces to flip over. Go in the reverse direction until we reach the original space, noting all the tiles along the way.
                while (true) {
                    currentCoord -= directionCoord
                    if (currentCoord == startCoord) {
                        break
                    }
                    tilesToFlip += currentCoord
                }
            }
        }
    }

    // If no tiles were flipped, this is not a valid move.
    if (tilesToFlip.isEmpty()) {
        return null
    }
    return tilesToFlip.toTypedArray()
}

/**
 * Returns true if the coordinates are located on the board.
 */
fun isOnBoard(coord: Coord) = coord.x >= 0 && coord.x <= (WIDTH - 1) && coord.y >= 0 && coord.y <= (HEIGHT - 1)

/**
 * Returns a new board with periods marking the valid moves the player can make.
 */
fun getBoardWithValidMoves(
    board: Array<CharArray>,
    tile: Char,
): Array<CharArray> {
    val boardCopy = getBoardCopy(board)

    getValidMoves(boardCopy, tile).forEach {
        boardCopy[it.x][it.y] = '.'
    }

    return boardCopy
}

/**
 * Returns a list of Coord lists of valid moves for the given player on the given board.
 */
fun getValidMoves(
    board: Array<CharArray>,
    tile: Char,
): Array<Coord> =
    (0..<WIDTH)
        .map { x ->
            (0..<HEIGHT).map { y -> Coord(x, y) }.filter {
                isValidMove(board, tile, it) != null
            }
        }.flatten()
        .toTypedArray()

/**
 * Determine the score by counting the tiles. Returns a dictionary with keys 'X' and 'O'.
 */
fun getScoreOfBoard(board: Array<CharArray>) =
    (0..<WIDTH).fold(Score(0, 0)) { accX, x ->
        accX +
            (0..<HEIGHT).fold(Score(0, 0)) { accY, y ->
                accY +
                    if (board[x][y] == 'X') {
                        Score(1, 0)
                    } else if (board[x][y] == 'O') {
                        Score(0, 1)
                    } else {
                        Score(0, 0)
                    }
            }
    }

/**
 * Lets the player type which tile they want to be.
 * Returns a list with the player's tile as the first item and the computer's tile as the second.
 */
fun enterPlayerTile(): CharArray {
    var tile = ""
    while (!(tile == "X" || tile == "O")) {
        println("Do you want to be X or O?")
        tile = readLine()?.trim()?.uppercase() ?: ""
    }

    // The first element in the list is the player's tile, and the second is the computer's tile.
    if (tile == "X") {
        return charArrayOf('X', 'O')
    } else {
        return charArrayOf('O', 'X')
    }
}

/**
 * Randomly choose who goes first.
 */
fun whoGoesFirst() =
    if (Random.nextInt(0..1) == 0) {
        GuessPlayer.Computer
    } else {
        GuessPlayer.Player
    }

/**
 * Place the tile on the board at xstart, ystart, and flip any of the opponent's pieces.
 * Returns False if this is an invalid move; True if it is valid.
 */
fun makeMove(
    board: Array<CharArray>,
    tile: Char,
    start: Coord,
): Boolean {
    val tilesToFlip = isValidMove(board, tile, start)

    if (tilesToFlip == null) {
        return false
    }

    board[start.x][start.y] = tile
    tilesToFlip.forEach {
        board[it.x][it.y] = tile
    }
    return true
}

/**
 * Make a duplicate of the board list and return it.
 */
fun getBoardCopy(board: Array<CharArray>) = board.map { it.clone() }.toTypedArray()

/**
 * Returns True if the position is in one of the four corners.
 */
fun isOnCorner(coord: Coord) = (coord.x == 0 || coord.x == WIDTH - 1) and (coord.y == 0 || coord.y == HEIGHT - 1)

/**
 * Let the player enter their move.
 * Returns the move or returns the strings 'hints' or 'quit'.
 */
fun getPlayerMove(
    board: Array<CharArray>,
    playerTile: Char,
): String {
    while (true) {
        println("Enter your move, \"quit\" to end the game, or \"hints\" to toggle hints.")
        val move = readLine()?.trim()?.lowercase() ?: ""
        if (move == "quit" || move == "hints") {
            return move
        }

        if (move.length == 2 && DIGITS1TO8.contains(move[0]) && DIGITS1TO8.contains(move[1])) {
            val x = (move[0].digitToIntOrNull() ?: 0) - 1
            val y = (move[1].digitToIntOrNull() ?: 0) - 1
            if (isValidMove(board, playerTile, Coord(x, y)) == null) {
                continue
            } else {
                return move
            }
        } else {
            println("That is not a valid move. Enter the column (1-8) and then the row (1-8).")
            println("For example, 81 will move on the top-right corner.")
        }
    }
}

/**
 * Given a board and the computer's tile, determine where to
 * move and return that move as a [x, y] list.
 */
fun getComputerMove(
    board: Array<CharArray>,
    computerTile: Char,
): Coord {
    val possibleMoves = getValidMoves(board, computerTile)
    possibleMoves.shuffle() // randomize the order of the moves

    // Always go for a corner if available.
    val isOnCornerCoord = possibleMoves.firstOrNull { isOnCorner(it) }
    if (isOnCornerCoord != null) {
        return isOnCornerCoord
    }

    // Find the highest-scoring move possible.
    var bestScore = -1
    var bestMove = Coord(0, 0)
    possibleMoves.forEach {
        val boardCopy = getBoardCopy(board)
        makeMove(boardCopy, computerTile, it)
        val score = getScoreOfBoard(boardCopy)
        val computerTile = getScore(score, computerTile)
        if (computerTile > bestScore) {
            bestMove = it
            bestScore = computerTile
        }
    }
    return bestMove
}

fun getScore(
    score: Score,
    tile: Char,
) = if (tile == 'X') {
    score.x
} else {
    score.y
}

fun printScore(
    board: Array<CharArray>,
    playerTile: Char,
    computerTile: Char,
) {
    val scores = getScoreOfBoard(board)
    println("You: ${getScore(scores, playerTile)} points. Computer: ${getScore(scores, computerTile)} points.")
}

fun playGame(
    playerTile: Char,
    computerTile: Char,
): Array<CharArray> {
    var showHints = false
    var turn = whoGoesFirst()
    println("The $turn will go first.")

    // Clear the board and place starting pieces.
    val board = getNewBoard()
    board[3][3] = 'X'
    board[3][4] = 'O'
    board[4][3] = 'O'
    board[4][4] = 'X'

    while (true) {
        val playerValidMoves = getValidMoves(board, playerTile)
        val computerValidMoves = getValidMoves(board, computerTile)

        if (playerValidMoves.isEmpty() && computerValidMoves.isEmpty()) {
            return board // No one can move, so end the game.
        } else if (turn == GuessPlayer.Player) { // Player's turn
            if (playerValidMoves.isNotEmpty()) {
                if (showHints) {
                    val validMovesBoard = getBoardWithValidMoves(board, playerTile)
                    drawBoard(validMovesBoard)
                } else {
                    drawBoard(board)
                }
                printScore(board, playerTile, computerTile)

                val move = getPlayerMove(board, playerTile)
                if (move == "quit") {
                    println("Thanks for playing!")
                    kotlin.system.exitProcess(0) // Terminate the program.
                } else if (move == "hints") {
                    showHints = !showHints
                    continue
                } else {
                    val x = move[0].digitToInt() - 1
                    val y = move[1].digitToInt() - 1
                    makeMove(board, playerTile, Coord(x, y))
                }
            }
            turn = GuessPlayer.Computer
        } else if (turn == GuessPlayer.Computer) { // Computer's turn
            if (computerValidMoves.isNotEmpty()) {
                drawBoard(board)
                printScore(board, playerTile, computerTile)

                println("Press Enter to see the computer\'s move.")
                readLine()
                val move = getComputerMove(board, computerTile)
                makeMove(board, computerTile, move)
            }
            turn = GuessPlayer.Player
        }
    }
}

/**
 * This function returns True if the player wants to play again, otherwise it returns False.
 */
fun playAgain(): Boolean {
    println("Do you want to play again? (yes or no)")
    return readLine()?.trim()?.lowercase()?.startsWith('y') ?: false
}

fun main() {
    println("Welcome to Reversegam!")

    val (playerTile, computerTile) = enterPlayerTile()

    while (true) {
        val finalBoard = playGame(playerTile, computerTile)

        // Display the final score.
        drawBoard(finalBoard)
        val scores = getScoreOfBoard(finalBoard)
        println("X scored ${getScore(scores, 'X')} points. O scored ${getScore(scores, 'O')} points.")
        if (getScore(scores, playerTile) > getScore(scores, computerTile)) {
            println("You beat the computer by ${getScore(scores, playerTile) - getScore(scores, computerTile)} points! Congratulations!")
        } else if (getScore(scores, playerTile) < getScore(scores, computerTile)) {
            println("You lost. The computer beat you by ${getScore(scores, computerTile) - getScore(scores, playerTile)} points.")
        } else {
            print("The game was a tie!")
        }

        if (!playAgain()) {
            break
        }
    }
}
