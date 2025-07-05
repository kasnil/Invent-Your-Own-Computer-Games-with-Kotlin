package com.github.kasnil.kotlin.invent.bagels

const val NUM_DIGITS = 3
const val MAX_GUESS = 10

/**
 * Returns a string of unique random digits that is NUM_DIGITS long.
 */
fun getSecretNum() =
    ('0'..'9')
        .shuffled()
        .take(NUM_DIGITS)
        .fold("") { acc, number -> acc + number }

/**
 * Returns a string with the Pico, Fermi, & Bagels clues to the user.
 */
fun getClues(
    guess: String,
    secretNum: String,
): String {
    if (guess == secretNum) {
        return "You got it!"
    }

    val clues =
        (0..<guess.length).fold(mutableListOf<String>()) { acc, index ->
            if (guess[index] == secretNum[index]) {
                acc += "Fermi"
            } else if (guess[index] in secretNum) {
                acc += "Pico"
            }
            acc
        }

    if (clues.size == 0) {
        return "Bagels"
    }

    return clues.joinToString(" ")
}

/**
 * Returns true if num is a string of only digits. Otherwise, returns false.
 */
fun isOnlyDigits(num: String) = num.matches("""\d+""".toRegex())

/**
 * This function returns True if the player wants to play again, otherwise it returns False.
 */
fun playAgain(): Boolean {
    println("Do you want to play again? (yes or no)")
    return readLine()?.trim()?.lowercase()?.startsWith('y') ?: false
}

fun main() {
    println("I am thinking of a $NUM_DIGITS-digit number. Try to guess what it is.")
    println("The clues I give are...")
    println("When I say:    That means:")
    println("  Bagels       None of the digits is correct.")
    println("  Pico         One digit is correct but in the wrong position.")
    println("  Fermi        One digit is correct and in the right position.")

    while (true) {
        val secretNum = getSecretNum()
        println(secretNum)
        println("I have thought up a number. You have $MAX_GUESS guesses to get it.")

        var guessesTaken = 1
        while (guessesTaken <= MAX_GUESS) {
            var guess = ""
            while (guess.length != NUM_DIGITS || !isOnlyDigits(guess)) {
                print("Guess #$guessesTaken: ")
                guess = readLine()?.trim() ?: ""
            }

            println(getClues(guess, secretNum))
            guessesTaken += 1

            if (guess == secretNum) {
                break
            }
            if (guessesTaken > MAX_GUESS) {
                println("You ran out of guesses. The answer was $secretNum")
            }
        }

        if (!playAgain()) {
            break
        }
    }
}
