package com.github.kasnil.kotlin.invent.sonar

import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.random.nextInt

data class Coord(
    val x: Int,
    val y: Int,
) {
    fun distance(other: Coord) =
        sqrt(
            (x.toDouble() - other.x.toDouble()) * (x.toDouble() - other.x.toDouble()) +
                (y.toDouble() - other.y.toDouble()) * (y.toDouble() - other.y.toDouble()),
        )
}

/**
 * Create a new 60x15 board data structure.
 */
fun getNewBoard() =
    Array(60) {
        CharArray(15) {
            if (Random.nextInt(0..1) == 0) {
                '~'
            } else {
                '`'
            }
        }
    }

/**
 * Draw the board data structure.
 */
fun drawBoard(board: Array<CharArray>) {
    // Initial space for the numbers down the left side of the board
    val tensDigitsLine =
        (1..5).fold("    ") { acc, num ->
            acc + " ".repeat(9) + num.toString()
        }

    // Print the numbers across the top of the board.
    println(tensDigitsLine)
    println("   " + "0123456789".repeat(6))
    println()

    // Print each of the 15 rows.
    for (row in 0..14) {
        // Single-digit numbers need to be padded with an extra space.
        val extraSpace =
            if (row < 10) {
                " "
            } else {
                ""
            }

        // Create the string for this row on the board.
        val boardRow =
            (0..59).fold("") { acc, column ->
                acc + board[column][row]
            }

        println("$extraSpace$row $boardRow $row")
    }

    // Print the numbers across the bottom of the board.
    println()
    println("   " + "0123456789".repeat(6))
    println(tensDigitsLine)
}

/**
 * Create a list of chest data structures (two-item lists of x, y int coordinates).
 */
fun getRandomChests(numChests: Int): MutableList<Coord> {
    val chests = mutableListOf<Coord>()
    while (chests.size < numChests) {
        val newChest = Coord(Random.nextInt(0..59), Random.nextInt(0..14))
        // Make sure a chest is not already here.
        if (newChest !in chests) {
            chests += newChest
        }
    }

    return chests
}

/**
 * Return True if the coordinates are on the board; otherwise, return False.
 */
fun isOnBoard(coord: Coord) = coord.x >= 0 && coord.x <= 59 && coord.y >= 0 && coord.y <= 14

/**
 * Change the board data structure with a sonar device character. Remove treasure chests from the chests list as they are found.
 * Return False if this is an invalid move.
 * Otherwise, return the string of the result of this move.
 */
fun makeMove(
    board: Array<CharArray>,
    chests: MutableList<Coord>,
    coord: Coord,
): String? {
    if (!isOnBoard(coord)) {
        return null
    }

    var smallestDistance = 100.0 // Any chest will be closer than 100.
    for (chest in chests) {
        val distance = chest.distance(coord)

        // We want the closest treasure chest.
        if (distance < smallestDistance) {
            smallestDistance = distance
        }
    }

    val roundSmallestDistance = smallestDistance.roundToInt()

    if (roundSmallestDistance == 0) {
        // xy is directly on a treasure chest!
        chests -= coord
        return "You have found a sunken treasure chest!"
    } else {
        if (roundSmallestDistance < 10) {
            board[coord.x][coord.y] = roundSmallestDistance.toChar()
            return "Treasure detected at a distance of $smallestDistance from the sonar device."
        } else {
            board[coord.x][coord.y] = 'X'
            return "Sonar did not detect anything. All treasure chests out of range."
        }
    }
}

/**
 * Returns true if num is a string of only digits. Otherwise, returns false.
 */
fun isOnlyDigits(num: String) = num.matches("""\d+""".toRegex())

/**
 * Let the player enter their move. Return a two-item list of int xy coordinates.
 */
fun enterPlayerMove(previousMoves: List<Coord>): Coord {
    println("Where do you want to drop the next sonar device? (0-59 0-14) (or type quit)")
    while (true) {
        val line = readLine()?.trim()?.lowercase() ?: ""
        if (line == "quit") {
            print("Thanks for playing!")
            kotlin.system.exitProcess(0)
        }

        val move = line.split(" ")

        if (move.size == 2 && isOnlyDigits(move[0]) && isOnlyDigits(move[1])) {
            val coord = Coord(move[0].toInt(), move[1].toInt())
            if (isOnBoard(coord) && coord in previousMoves) {
                println("You already moved there.")
                continue
            }
            return coord
        }

        println("Enter a number from 0 to 59, a space, then a number from 0 to 14.")
    }
}

fun showInstructions() {
    println(
        """
    |Instructions:
    |You are the captain of the Simon, a treasure-hunting ship. Your current mission
    |is to use sonar devices to find three sunken treasure chests at the bottom of
    |the ocean. But you only have cheap sonar that finds distance, not direction.
    |
    |Enter the coordinates to drop a sonar device. The ocean map will be marked with
    |how far away the nearest chest is, or an X if it is beyond the sonar device's
    |range. For example, the C marks are where chests are. The sonar device shows a
    |3 because the closest chest is 3 spaces away.
    |
    |1 2 3
    |012345678901234567890123456789012
    |
    |0 ~~~~`~```~`~``~~~``~`~~``~~~``~`~ 0
    |1 ~`~`~``~~`~```~~~```~~`~`~~~`~~~~ 1
    |2 `~`C``3`~~~~`C`~~~~`````~~``~~~`` 2
    |3 ````````~~~`````~~~`~`````~`~``~` 3
    |4 ~`~~~~`~~`~~`C`~``~~`~~~`~```~``~ 4
    |
    |012345678901234567890123456789012
    |1 2 3
    |(In the real game, the chests are not visible in the ocean.)
    |
    |Press enter to continue...
        """.trimMargin(),
    )
    readLine()

    println(
        """
    |When you drop a sonar device directly on a chest, you retrieve it and the other
    |sonar devices update to show how far away the next nearest chest is. The chests
    |are beyond the range of the sonar device on the left, so it shows an X.
    |
    |1 2 3
    |012345678901234567890123456789012
    |
    |0 ~~~~`~```~`~``~~~``~`~~``~~~``~`~ 0
    |1 ~`~`~``~~`~```~~~```~~`~`~~~`~~~~ 1
    |2 `~`X``7`~~~~`C`~~~~`````~~``~~~`` 2
    |3 ````````~~~`````~~~`~`````~`~``~` 3
    |4 ~`~~~~`~~`~~`C`~``~~`~~~`~```~``~ 4
    |
    |012345678901234567890123456789012
    |1 2 3
    |
    |The treasure chests don't move around. Sonar devices can detect treasure chests
    |up to a distance of 9 spaces. Try to collect all 3 chests before running out of
    |sonar devices. Good luck!
    |
    |Press enter to continue...
        """.trimMargin(),
    )
    readLine()
}

/**
 * This function returns True if the player wants to play again, otherwise it returns False.
 */
fun playAgain(): Boolean {
    println("Do you want to play again? (yes or no)")
    return readLine()?.trim()?.lowercase()?.startsWith('y') ?: false
}

fun main() {
    println("S O N A R !")
    println()
    println("Would you like to view the instructions? (yes/no)")
    if (readLine()?.trim()?.lowercase()?.startsWith('y') ?: false) {
        showInstructions()
    }

    while (true) {
        // Game setup
        var sonarDevices = 20
        val theBoard = getNewBoard()
        val theChests = getRandomChests(3)
        drawBoard(theBoard)
        val previousMoves = mutableListOf<Coord>()

        while (sonarDevices > 0) {
            // Show sonar device and chest statuses.
            println("You have $sonarDevices sonar device(s) left. ${theChests.size} treasure chest(s) remaining.")

            val move = enterPlayerMove(previousMoves)
            previousMoves += move // We must track all moves so that sonar devices can be updated.
            val moveResult = makeMove(theBoard, theChests, move)

            if (moveResult == null) {
                continue
            } else {
                if (moveResult == "You have found a sunken treasure chest!") {
                    // Update all the sonar devices currently on the map.
                    previousMoves.forEach {
                        makeMove(theBoard, theChests, it)
                    }
                }
                drawBoard(theBoard)
                println(moveResult)
            }

            if (theChests.size == 0) {
                println("You have found all the sunken treasure chests! Congratulations and good game!")
                break
            }

            sonarDevices -= 1
        }

        if (sonarDevices == 0) {
            println("We\'ve run out of sonar devices! Now we have to turn the ship around and head")
            println("for home with treasure chests still out there! Game over.")
            println("\tThe remaining chests were here:")
            theChests.forEach {
                print("\t${it.x}, ${it.y}")
            }
        }

        if (!playAgain()) {
            break
        }
    }
}
