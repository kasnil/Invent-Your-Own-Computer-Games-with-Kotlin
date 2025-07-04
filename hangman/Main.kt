package com.github.kasnil.kotlin.invent.hangman

import kotlin.random.Random
import kotlin.random.nextInt

val HANGMAN_PICS =
    mutableListOf<String>(
        """
    |  +---+
    |      |
    |      |
    |      |
    |     ===
        """.trimMargin(),
        """
    |  +---+
    |  O   |
    |      |
    |      |
    |     ===
        """.trimMargin(),
        """
    |  +---+
    |  O   |
    |  |   |
    |      |
    |     ===
        """.trimMargin(),
        """
    |  +---+
    |  O   |
    | /|   |
    |      |
    |     ===
        """.trimMargin(),
        """
    |  +---+
    |  O   |
    | /|\  |
    |      |
    |     ===
        """.trimMargin(),
        """
    |  +---+
    |  O   |
    | /|\  |
    | /    |
    |     ===
        """.trimMargin(),
        """
    |  +---+
    |  O   |
    | /|\  |
    | / \  |
    |     ===
        """.trimMargin(),
        """
    | +---+
    | [O   |
    | /|\  |
    | / \  |
    |     ===
        """.trimMargin(),
        """
    |  +---+
    | [O]  |
    | /|\  |
    | / \  |
    |     ===
        """.trimMargin(),
    )

val words: Map<String, Array<String>> =
    mapOf<String, Array<String>>(
        "Colors" to "red orange yellow green blue indigo violet white black brown".split(" ").toTypedArray(),
        "Shapes" to
            "square triangle rectangle circle ellipse rhombus trapazoid chevron pentagon hexagon septagon octogon"
                .split(
                    " ",
                ).toTypedArray(),
        "Fruits" to
            "apple orange lemon lime pear watermelon grape grapefruit cherry banana cantalope mango strawberry tomato"
                .split(
                    " ",
                ).toTypedArray(),
        "Animals" to
            "bat bear beaver cat cougar crab deer dog donkey duck eagle fish frog goat leech lion lizard monkey moose mouse otter owl panda python rabbit rat shark sheep skunk squid tiger turkey turtle weasel whale wolf wombat zebra"
                .split(
                    " ",
                ).toTypedArray(),
    )

/**
 * This function returns a random string from the passed dictionary of lists of strings, and the key also.
 */
fun getRandomWord(wordDict: Map<String, Array<String>>): Array<String> {
    // First, randomly select a key from the dictionary:
    val wordKey = wordDict.keys.random()
    val words = wordDict.getValue(wordKey)

    // Second, randomly select a word from the key's list in the dictionary:
    val wordIndex = Random.nextInt(0..<(words.size))

    return arrayOf<String>(words.get(wordIndex), wordKey)
}

fun displayBoard(
    missedLetters: String,
    correctLetters: String,
    secretWord: String,
) {
    println(HANGMAN_PICS[missedLetters.length])
    println()

    print("Missed letters: ")
    println(missedLetters.split("").joinToString(" "))

    var blanks = "_".repeat(secretWord.length)

    blanks =
        (0..<(secretWord.length)) // replace blanks with correctly guessed letters
            .filter { secretWord[it] in correctLetters }
            .fold(blanks) { acc, index -> acc.substring(0..<index) + secretWord[index] + acc.substring((index + 1)..<(acc.length)) }

    // show the secret word with spaces in between each letter
    println(blanks.split("").joinToString(" "))
}

/**
 * Returns the letter the player entered. This function makes sure the player entered a single letter, and not something else.
 */
fun getGuess(alreadyGuessed: String): Char {
    while (true) {
        println("Guess a letter.")
        val guess = readLine()?.trim()?.lowercase() ?: ""
        if (guess.length != 1) {
            println("Please enter a single letter.")
        } else if (alreadyGuessed.contains(guess)) {
            println("You have already guessed that letter. Choose again.")
        } else if (!"abcdefghijklmnopqrstuvwxyz".contains(guess)) {
            println("Please enter a LETTER.")
        } else {
            return guess.get(0)
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
    println("H A N G M A N")

    var difficulty = 'X'

    while (difficulty !in "EMH") {
        println("Enter difficulty: E - Easy, M - Medium, H - Hard")
        difficulty = readLine()?.trim()?.uppercase()?.getOrNull(0) ?: ' '
    }
    if (difficulty == 'M') {
        HANGMAN_PICS.removeAt(8)
        HANGMAN_PICS.removeAt(7)
    }
    if (difficulty == 'H') {
        HANGMAN_PICS.removeAt(8)
        HANGMAN_PICS.removeAt(7)
        HANGMAN_PICS.removeAt(5)
        HANGMAN_PICS.removeAt(3)
    }

    var missedLetters = ""
    var correctLetters = ""
    var (secretWord, secretSet) = getRandomWord(words)
    var gameIsDone = false

    while (true) {
        println("The secret word is in the set: $secretSet")
        displayBoard(missedLetters, correctLetters, secretWord)

        // Let the player type in a letter.
        val guess = getGuess(missedLetters + correctLetters)

        if (guess in secretWord) {
            correctLetters = correctLetters + guess

            // Check if the player has won
            val foundAllLetters = secretWord.split("").all { it in correctLetters }
            if (foundAllLetters) {
                println("Yes! The secret word is \"$secretWord\"! You have won!")
                gameIsDone = true
            }
        } else {
            missedLetters = missedLetters + guess

            // Check if player has guessed too many times and lost.
            if (missedLetters.length == (HANGMAN_PICS.size - 1)) {
                displayBoard(missedLetters, correctLetters, secretWord)
                println(
                    "You have run out of guesses!\nAfter ${missedLetters.length} missed guesses and ${correctLetters.length} correct guesses, the word was \"$secretWord\"",
                )
                gameIsDone = true
            }
        }

        // Ask the player if they want to play again (but only if the game is done).
        if (gameIsDone) {
            if (playAgain()) {
                missedLetters = ""
                correctLetters = ""
                gameIsDone = false
                val randomWord = getRandomWord(words)
                secretWord = randomWord[0]
                secretSet = randomWord[1]
            } else {
                break
            }
        }
    }
}
