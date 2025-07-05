package com.github.kasnil.kotlin.invent.tictactoe

import kotlin.random.Random
import kotlin.random.nextInt

enum class GuessPlayer {
    Computer,
    Player,
}

/**
 * This function prints out the board that it was passed.
 */
fun drawBoard(board: CharArray) {
    // "board" is a list of 10 strings representing the board (ignore index 0)
    println("${board[7]}|${board[8]}|${board[9]}")
    println("-+-+-")
    println("${board[4]}|${board[5]}|${board[6]}")
    println("-+-+-")
    println("${board[1]}|${board[2]}|${board[3]}")
}

/**
 * Lets the player type which letter they want to be.
 * Returns a list with the player's letter as the first item, and the computer's letter as the second.
 */
fun inputPlayerLetter(): CharArray {
    var letter = ""
    while (!"XO".contains(letter)) {
        println("Do you want to be X or O?")
        letter = readLine()?.trim()?.uppercase() ?: ""
    }

    // the first element in the list is the player's letter, the second is the computer's letter.
    if (letter == "X") {
        return charArrayOf('X', 'O')
    } else {
        return charArrayOf('O', 'X')
    }
}

/**
 * Randomly choose the player who goes first.
 */
fun whoGoesFirst() =
    if (Random.nextInt(0..1) == 0) {
        GuessPlayer.Computer
    } else {
        GuessPlayer.Player
    }

fun makeMove(
    board: CharArray,
    letter: Char,
    move: Int,
) = board.set(move, letter)

/**
 * Given a board and a player's letter, this function returns True if that player has won.
 * We use bo instead of board and le instead of letter so we don't have to type as much.
 */
fun isWinner(
    board: CharArray,
    letter: Char,
) = // across the top
    (board[7] == letter && board[8] == letter && board[9] == letter) ||
        // across the middle
        (board[4] == letter && board[5] == letter && board[6] == letter) ||
        // across the bottom
        (board[1] == letter && board[2] == letter && board[3] == letter) ||
        // down the left side
        (board[7] == letter && board[4] == letter && board[1] == letter) ||
        // down the middle
        (board[8] == letter && board[5] == letter && board[2] == letter) ||
        // down the right side
        (board[9] == letter && board[6] == letter && board[3] == letter) ||
        // diagonal
        (board[7] == letter && board[5] == letter && board[3] == letter) ||
        // diagonal
        (board[9] == letter && board[5] == letter && board[1] == letter)

/**
 * Make a copy of the board list and return it.
 */
fun getBoardCopy(board: CharArray) = board.clone()

/**
 * Return true if the passed move is free on the passed board.
 */
fun isSpaceFree(
    board: CharArray,
    move: Int,
) = board[move] == ' '

fun getPlayerMove(board: CharArray): Int {
    // Let the player type in their move.
    var move = 0
    while (move !in 1..9 || !isSpaceFree(board, move)) {
        println("What is your next move? (1-9)")
        move = readLine()?.trim()?.toIntOrNull() ?: 0
    }
    return move
}

/**
 * Returns a valid move from the passed list on the passed board.
 * Returns null if there is no valid move.
 */
fun chooseRandomMoveFromList(
    board: CharArray,
    vararg movesList: Int,
): Int? {
    val possibleMoves =
        movesList
            .filter { isSpaceFree(board, it) }
            .toTypedArray()

    if (possibleMoves.size != 0) {
        return possibleMoves.random()
    } else {
        return null
    }
}

/**
 * Given a board and the computer's letter, determine where to move and return that move.
 */
fun getComputerMove(
    board: CharArray,
    computerLetter: Char,
): Int {
    val playerLetter =
        if (computerLetter == 'X') {
            'O'
        } else {
            'X'
        }

    // Here is our algorithm for our Tic Tac Toe AI:
    // First, check if we can win in the next move
    (1..9).forEach {
        val boardCopy = getBoardCopy(board)
        if (isSpaceFree(boardCopy, it)) {
            makeMove(boardCopy, computerLetter, it)
            if (isWinner(boardCopy, computerLetter)) {
                return it
            }
        }
    }

    // Check if the player could win on his next move, and block them.
    (1..9).forEach {
        val boardCopy = getBoardCopy(board)
        if (isSpaceFree(boardCopy, it)) {
            makeMove(boardCopy, playerLetter, it)
            if (isWinner(boardCopy, playerLetter)) {
                return it
            }
        }
    }

    // Try to take one of the corners, if they are free.
    val move = chooseRandomMoveFromList(board, 1, 3, 7, 9)
    if (move != null) {
        return move
    }

    // Try to take the center, if it is free.
    if (isSpaceFree(board, 5)) {
        return 5
    }

    // Move on one of the sides.
    return chooseRandomMoveFromList(board, 2, 4, 6, 8)!!
}

/**
 * Return True if every space on the board has been taken. Otherwise return False.
 */
fun isBoardFull(board: CharArray) = (1..9).none { isSpaceFree(board, it) }

/**
 * This function returns True if the player wants to play again, otherwise it returns False.
 */
fun playAgain(): Boolean {
    println("Do you want to play again? (yes or no)")
    return readLine()?.trim()?.lowercase()?.startsWith('y') ?: false
}

fun main() {
    println("Welcome to Tic Tac Toe!")

    while (true) {
        // Reset the board
        val theBoard = CharArray(10) { ' ' }
        val (playerLetter, computerLetter) = inputPlayerLetter()
        var turn = whoGoesFirst()
        println("The $turn will go first.")
        var gameIsPlaying = true

        while (gameIsPlaying) {
            if (turn == GuessPlayer.Player) {
                // Player's turn.
                drawBoard(theBoard)
                val move = getPlayerMove(theBoard)
                makeMove(theBoard, playerLetter, move)

                if (isWinner(theBoard, playerLetter)) {
                    drawBoard(theBoard)
                    println("Hooray! You have won the game!")
                    gameIsPlaying = false
                } else {
                    if (isBoardFull(theBoard)) {
                        drawBoard(theBoard)
                        println("The game is a tie!")
                        break
                    } else {
                        turn = GuessPlayer.Computer
                    }
                }
            } else {
                // Computer's turn.
                val move = getComputerMove(theBoard, computerLetter)
                makeMove(theBoard, computerLetter, move)

                if (isWinner(theBoard, computerLetter)) {
                    drawBoard(theBoard)
                    println("The computer has beaten you! You lose.")
                    gameIsPlaying = false
                } else {
                    if (isBoardFull(theBoard)) {
                        drawBoard(theBoard)
                        println("The game is a tie!")
                        break
                    } else {
                        turn = GuessPlayer.Player
                    }
                }
            }
        }

        if (!playAgain()) {
            break
        }
    }
}
